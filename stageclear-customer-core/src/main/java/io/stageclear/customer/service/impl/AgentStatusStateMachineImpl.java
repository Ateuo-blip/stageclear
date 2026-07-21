package io.stageclear.customer.service.impl;

import io.stageclear.common.enums.AgentStatus;
import io.stageclear.common.exception.BusinessException;
import io.stageclear.customer.service.AgentStatusStateMachine;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class AgentStatusStateMachineImpl implements AgentStatusStateMachine {

    private static final Map<AgentStatus, Set<AgentStatus>> RULES = Map.of(
            AgentStatus.OFFLINE, Set.of(AgentStatus.ONLINE),
            AgentStatus.ONLINE, Set.of(AgentStatus.AWAY, AgentStatus.BUSY, AgentStatus.OFFLINE),
            AgentStatus.AWAY, Set.of(AgentStatus.ONLINE, AgentStatus.OFFLINE),
            AgentStatus.BUSY, Set.of(AgentStatus.ONLINE, AgentStatus.OFFLINE)
    );

    @Override
    public boolean canTransit(AgentStatus from, AgentStatus to) {
        if (from == null || to == null) {
            return false;
        }
                    //如果from不在map就返回一个空集合
        return RULES.getOrDefault(from, Set.of()).contains(to);
    }

    @Override
    public void checkTransit(AgentStatus from, AgentStatus to) {
        if (!canTransit(from, to)) {
            throw new BusinessException(400, "坐席状态流转不合法");
        }
    }
}