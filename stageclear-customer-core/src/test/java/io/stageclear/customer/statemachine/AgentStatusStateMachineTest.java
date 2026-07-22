package io.stageclear.customer.statemachine;

import io.stageclear.common.enums.AgentStatus;
import io.stageclear.common.exception.BusinessException;
import io.stageclear.customer.service.AgentStatusStateMachine;
import io.stageclear.customer.service.impl.AgentStatusStateMachineImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AgentStatusStateMachineTest {

    private final AgentStatusStateMachine stateMachine = new AgentStatusStateMachineImpl();

    @Test
    void offlineCanOnlyTransitToOnline() {
        assertTrue(stateMachine.canTransit(AgentStatus.OFFLINE, AgentStatus.ONLINE));
        assertFalse(stateMachine.canTransit(AgentStatus.OFFLINE, AgentStatus.BUSY));
        assertFalse(stateMachine.canTransit(AgentStatus.OFFLINE, AgentStatus.AWAY));
    }

    @Test
    void onlineCanTransitToAwayBusyOrOffline() {
        assertTrue(stateMachine.canTransit(AgentStatus.ONLINE, AgentStatus.AWAY));
        assertTrue(stateMachine.canTransit(AgentStatus.ONLINE, AgentStatus.BUSY));
        assertTrue(stateMachine.canTransit(AgentStatus.ONLINE, AgentStatus.OFFLINE));
    }

    @Test
    void checkTransitThrowsWhenTransitionIsIllegal() {
        assertThrows(BusinessException.class,
                () -> stateMachine.checkTransit(AgentStatus.BUSY, AgentStatus.AWAY));
    }
}
