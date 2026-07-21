package io.stageclear.customer.strategy;

import io.stageclear.common.entity.CustomerAgent;
import io.stageclear.common.entity.CustomerSession;
import io.stageclear.common.enums.AgentStatus;
import io.stageclear.common.service.CustomerAgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 最少会话分配策略
 */
@Component
@RequiredArgsConstructor
public class LeastBusyAgentAssignStrategy implements AgentAssignStrategy{
    private final CustomerAgentService customerAgentService;
    @Override
    public CustomerAgent selectAgent(CustomerSession session) {

        return customerAgentService.lambdaQuery()
                .eq(CustomerAgent::getStatus, AgentStatus.ONLINE.getCode())
                .apply("current_load < max_sessions")
                .orderByAsc(CustomerAgent::getCurrentLoad)
                .orderByAsc(CustomerAgent::getId)
                .last("LIMIT 1")
                .one();
    }

    @Override
    public String strategyCode() {
        return "LEAST_BUSY";
    }
}
