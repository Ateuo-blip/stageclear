package io.stageclear.customer.service.impl;

import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import io.stageclear.common.entity.CustomerAgent;
import io.stageclear.common.entity.CustomerSession;
import io.stageclear.common.enums.SessionStatus;
import io.stageclear.common.exception.BusinessException;
import io.stageclear.common.exception.ErrorCode;
import io.stageclear.common.service.CustomerAgentService;
import io.stageclear.common.service.CustomerSessionService;
import io.stageclear.customer.service.SessionLifecycleService;
import io.stageclear.customer.service.SessionNoGenerator;
import io.stageclear.customer.statemachine.SessionStateMachine;
import io.stageclear.customer.vo.SessionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SessionLifecycleServiceImpl implements SessionLifecycleService {

    private final CustomerSessionService customerSessionService;
    private final SessionStateMachine sessionStateMachine;
    private final CustomerAgentService customerAgentService;
    private final SessionNoGenerator sessionNoGenerator;

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
    public SessionVO assignAgent(Long sessionId, Long agentId) {
        CustomerSession session = getSessionOrThrow(sessionId);
        getAgentOrThrow(agentId);

        SessionStatus currentStatus = parseStatus(session.getStatus());
        SessionStatus targetStatus = SessionStatus.IN_PROGRESS;

        sessionStateMachine.checkTransit(currentStatus, targetStatus);
        boolean firstAssign = currentStatus == SessionStatus.WAITING;

        LambdaUpdateChainWrapper<CustomerSession> update = customerSessionService.lambdaUpdate()
                .eq(CustomerSession::getId, sessionId)
                .eq(CustomerSession::getStatus, currentStatus.getCode())
                .set(CustomerSession::getStatus, targetStatus.getCode())
                .set(CustomerSession::getAgentId, agentId);
        if (firstAssign) {
            update.set(CustomerSession::getStartedAt, LocalDateTime.now());
        }

        boolean updated = update.update();

        return returnUpdatedSessionOrThrow(updated, sessionId);
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

    @Override
    public SessionVO endSession(Long sessionId, String endReason) {
        CustomerSession session = getSessionOrThrow(sessionId);

        SessionStatus currentStatus = parseStatus(session.getStatus());
        SessionStatus targetStatus = SessionStatus.ENDED;
        sessionStateMachine.checkTransit(currentStatus, targetStatus);

        boolean updated = customerSessionService.lambdaUpdate()
                .eq(CustomerSession::getId, sessionId)
                .eq(CustomerSession::getStatus, currentStatus.getCode())
                .set(CustomerSession::getStatus, targetStatus.getCode())
                .set(CustomerSession::getEndedAt, LocalDateTime.now())
                .set(CustomerSession::getEndReason, endReason)
                .update();

        return returnUpdatedSessionOrThrow(updated, sessionId);
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
}