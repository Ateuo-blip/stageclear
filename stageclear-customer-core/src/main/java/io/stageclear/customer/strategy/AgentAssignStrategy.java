package io.stageclear.customer.strategy;

import io.stageclear.common.entity.CustomerAgent;
import io.stageclear.common.entity.CustomerSession;

public interface AgentAssignStrategy {

    CustomerAgent selectAgent(CustomerSession session);

    String strategyCode();
}