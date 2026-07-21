package io.stageclear.customer.service.impl;

import io.stageclear.common.entity.CustomerAgent;
import io.stageclear.common.entity.CustomerSession;
import io.stageclear.common.enums.AgentStatus;
import io.stageclear.common.enums.SessionStatus;
import io.stageclear.common.exception.BusinessException;
import io.stageclear.common.exception.ErrorCode;
import io.stageclear.common.service.CustomerAgentService;
import io.stageclear.common.service.CustomerSessionService;
import io.stageclear.customer.service.AgentAssignService;
import io.stageclear.customer.strategy.AgentAssignStrategy;
import io.stageclear.customer.strategy.AgentAssignStrategyFactory;
import io.stageclear.customer.vo.SessionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AgentAssignServiceImpl implements AgentAssignService {
    private final AgentAssignStrategyFactory agentAssignStrategyFactory;
    private final CustomerAgentService customerAgentService;
    private final CustomerSessionService customerSessionService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SessionVO assignAuto(Long sessionId) {
        CustomerSession session = customerSessionService.getById(sessionId);
        if (null == session) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        SessionStatus currentStatus = SessionStatus.of(session.getStatus());
        //校验会话状态
        if (!Objects.equals(currentStatus, SessionStatus.TRANSFERRING)
                && !Objects.equals(currentStatus, SessionStatus.WAITING)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
        AgentAssignStrategy leastBusy = agentAssignStrategyFactory.getStrategy("LEAST_BUSY");
        CustomerAgent customerAgent = leastBusy.selectAgent(session);
        if (null == customerAgent) {
            throw new BusinessException(400, "坐席容量已满，请重试");
        }

        return updateAgentAndSession(customerAgent, session, currentStatus);
    }

    private SessionVO updateAgentAndSession(CustomerAgent agent, CustomerSession session, SessionStatus status) {
        boolean agentUpdated = customerAgentService.lambdaUpdate()
                .eq(CustomerAgent::getId, agent.getId())
                .eq(CustomerAgent::getStatus, AgentStatus.ONLINE.getCode())
                .apply("current_load < max_sessions")
                .setSql("current_load = current_load + 1")
                .update();
        if (!agentUpdated) {
            throw new BusinessException(400, "坐席容量已满，请重试");
        }
        boolean sessionUpdate = customerSessionService.lambdaUpdate()
                .eq(CustomerSession::getId, session.getId())
                .eq(CustomerSession::getStatus, status.getCode())
                .set(CustomerSession::getAgentId, agent.getId())
                .set(CustomerSession::getStatus, SessionStatus.IN_PROGRESS.getCode())
                .set(CustomerSession::getStartedAt, LocalDateTime.now())
                .update();
        if (!sessionUpdate) {
            throw new BusinessException(400,"会话状态已变化，请刷新后重试");
        }
        return SessionVO.from(customerSessionService.getById(session.getId()));
    }
}
