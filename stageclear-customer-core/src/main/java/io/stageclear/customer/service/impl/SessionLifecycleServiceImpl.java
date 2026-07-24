package io.stageclear.customer.service.impl;

import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import io.stageclear.common.entity.CustomerAgent;
import io.stageclear.common.entity.CustomerSession;
import io.stageclear.common.enums.AgentStatus;
import io.stageclear.common.enums.SessionStatus;
import io.stageclear.common.exception.BusinessException;
import io.stageclear.common.exception.ErrorCode;
import io.stageclear.common.service.CustomerAgentService;
import io.stageclear.common.service.CustomerSessionService;
import io.stageclear.customer.event.CustomerEventPublisher;
import io.stageclear.customer.event.dto.SessionAssignedEvent;
import io.stageclear.customer.service.SessionLifecycleService;
import io.stageclear.customer.service.SessionNoGenerator;
import io.stageclear.customer.statemachine.SessionStateMachine;
import io.stageclear.customer.vo.SessionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionLifecycleServiceImpl implements SessionLifecycleService {

    private final CustomerSessionService customerSessionService;
    private final SessionStateMachine sessionStateMachine;
    private final CustomerAgentService customerAgentService;
    private final SessionNoGenerator sessionNoGenerator;
    private final CustomerEventPublisher customerEventPublisher;

    @Override
    public SessionVO createSession(Long userId, String channel, String source) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
        String channelOrDefault = channel == null ? "WEB" : channel;
        CustomerSession session = new CustomerSession();
        session.setSessionNo(sessionNoGenerator.nextSessionNo());
        session.setUserId(userId);
        session.setStatus(SessionStatus.WAITING.getCode());
        session.setChannel(channelOrDefault);
        session.setSource(source);
        session.setPriority(0);

        boolean saved = customerSessionService.save(session);
        if (!saved) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR);
        }

        return SessionVO.from(session);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SessionVO assignAgent(Long sessionId, Long agentId) {
        CustomerSession session = getSessionOrThrow(sessionId);
        CustomerAgent targetAgent = getAgentOrThrow(agentId);

        SessionStatus currentStatus = parseStatus(session.getStatus());
        SessionStatus targetStatus = SessionStatus.IN_PROGRESS;

        sessionStateMachine.checkTransit(currentStatus, targetStatus);
        boolean firstAssign = currentStatus == SessionStatus.WAITING;
        //这次分配后，会话绑定的坐席是否发生变化
        boolean agentChanged = !Objects.equals(session.getAgentId(), targetAgent.getId());
        if (agentChanged) {
            occupyAgentLoad(targetAgent.getId());
        }

        LambdaUpdateChainWrapper<CustomerSession> update = customerSessionService.lambdaUpdate()
                .eq(CustomerSession::getId, sessionId)
                .eq(CustomerSession::getStatus, currentStatus.getCode())
                .set(CustomerSession::getStatus, targetStatus.getCode())
                .set(CustomerSession::getAgentId, agentId);
        if (firstAssign) {
            update.set(CustomerSession::getStartedAt, LocalDateTime.now());
        }
        if (session.getAgentId() == null) {
            update.isNull(CustomerSession::getAgentId);
        } else {
            update.eq(CustomerSession::getAgentId,session.getAgentId());
        }

        boolean updated = update.update();
        if (!updated) {
            throw new BusinessException(400, "会话状态已变化，请刷新后重试");
        }
        if (agentChanged) {
            releaseAgentLoad(session);
        }
        CustomerSession updatedSession = customerSessionService.getById(sessionId);
        customerEventPublisher.publishSessionAssigned(SessionAssignedEvent.builder()
                .eventId(UUID.randomUUID().toString().replace("-", ""))
                .sessionId(updatedSession.getId())
                .sessionNo(updatedSession.getSessionNo())
                .agentId(targetAgent.getId())
                .agentNo(targetAgent.getAgentNo())
                .previousAgentId(session.getAgentId())
                .previousAgentNo(null)
                .assignType(firstAssign ? "MANUAL" : "TRANSFER")
                .assignedAt(LocalDateTime.now())
                .traceId(MDC.get("traceId"))
                .build());
        return SessionVO.from(updatedSession);
    }

    @Override
    public SessionVO transferSession(Long sessionId) {
        CustomerSession session = getSessionOrThrow(sessionId);

        SessionStatus currentStatus = parseStatus(session.getStatus());
        SessionStatus targetStatus = SessionStatus.TRANSFERRING;
        sessionStateMachine.checkTransit(currentStatus, targetStatus);

        boolean updated = customerSessionService.lambdaUpdate()
                .eq(CustomerSession::getId, sessionId)
                .eq(CustomerSession::getStatus, currentStatus.getCode())
                .set(CustomerSession::getStatus, targetStatus.getCode())
                .update();

        return returnUpdatedSessionOrThrow(updated, sessionId);
    }

    /**
     * 结束会话同时释放坐席资源
     * @param sessionId
     * @param endReason
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public SessionVO endSession(Long sessionId, String endReason) {
        CustomerSession session = getSessionOrThrow(sessionId);

        SessionStatus currentStatus = parseStatus(session.getStatus());
        SessionStatus targetStatus = SessionStatus.ENDED;
        sessionStateMachine.checkTransit(currentStatus, targetStatus);

        boolean sessionUpdated = customerSessionService.lambdaUpdate()
                .eq(CustomerSession::getId, sessionId)
                .eq(CustomerSession::getStatus, currentStatus.getCode())
                .set(CustomerSession::getStatus, targetStatus.getCode())
                .set(CustomerSession::getEndedAt, LocalDateTime.now())
                .set(CustomerSession::getEndReason, endReason)
                .update();
        if (!sessionUpdated) {
            throw new BusinessException(400, "会话状态已变化，请刷新后重试");
        }
        releaseAgentLoad(session);
        return SessionVO.from(customerSessionService.getById(sessionId));
    }

    private CustomerSession getSessionOrThrow(Long sessionId) {
        if (sessionId == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        CustomerSession session = customerSessionService.getById(sessionId);
        if (session == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }

        return session;
    }

    private SessionStatus parseStatus(String status) {
        SessionStatus sessionStatus = SessionStatus.of(status);
        if (sessionStatus == null) {
            throw new BusinessException(400, "会话状态不合法");
        }
        return sessionStatus;
    }

    private CustomerAgent getAgentOrThrow(Long agentId) {
        if (agentId == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        CustomerAgent agent = customerAgentService.getById(agentId);
        if (agent == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }

        return agent;
    }

    private SessionVO returnUpdatedSessionOrThrow(boolean updated, Long sessionId) {
        if (!updated) {
            throw new BusinessException(400, "会话状态已变化，请刷新后重试");
        }

        return SessionVO.from(customerSessionService.getById(sessionId));
    }

    private void releaseAgentLoad(CustomerSession session) {
        Long agentId = session.getAgentId();
        if (agentId == null) {
            return;
        }
        boolean released = customerAgentService.lambdaUpdate()
                .eq(CustomerAgent::getId, agentId)
                .gt(CustomerAgent::getCurrentLoad, 0)
                .setSql("current_load = current_load - 1")
                .update();
        if (!released) {
            throw new BusinessException(400, "释放坐席负载异常，请重试");
        }
    }

    private void occupyAgentLoad(Long agentId) {
        boolean occupied = customerAgentService.lambdaUpdate()
                .eq(CustomerAgent::getId, agentId)
                .eq(CustomerAgent::getStatus, AgentStatus.ONLINE.getCode())
                .apply("current_load < max_sessions")
                .setSql("current_load = current_load + 1")
                .update();
        if (!occupied) {
            throw new BusinessException(400, "坐席不在线或容量已满，请重试");
        }
    }
}
