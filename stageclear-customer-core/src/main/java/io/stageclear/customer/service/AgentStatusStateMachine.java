package io.stageclear.customer.service;

import io.stageclear.common.enums.AgentStatus;

public interface AgentStatusStateMachine {
    boolean canTransit(AgentStatus from, AgentStatus to);

    void checkTransit(AgentStatus from, AgentStatus to);
}