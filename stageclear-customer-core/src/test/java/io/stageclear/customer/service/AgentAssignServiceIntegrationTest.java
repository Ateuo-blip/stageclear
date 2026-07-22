package io.stageclear.customer.service;

import io.stageclear.common.entity.CustomerAgent;
import io.stageclear.common.entity.CustomerSession;
import io.stageclear.common.enums.AgentStatus;
import io.stageclear.common.enums.SessionStatus;
import io.stageclear.common.exception.BusinessException;
import io.stageclear.common.service.CustomerAgentService;
import io.stageclear.common.service.CustomerSessionService;
import io.stageclear.customer.vo.SessionVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class AgentAssignServiceIntegrationTest {

    @Autowired
    private AgentAssignService agentAssignService;

    @Autowired
    private CustomerAgentService customerAgentService;

    @Autowired
    private CustomerSessionService customerSessionService;

    @Test
    void assignAutoShouldAssignOnlineLeastBusyAgent() {
        CustomerAgent agent = resetSeedAgent(AgentStatus.ONLINE, 0, 5);
        CustomerSession session = createWaitingSession();

        SessionVO result = agentAssignService.assignAuto(session.getId());

        assertEquals(SessionStatus.IN_PROGRESS, result.getStatus());
        assertEquals(agent.getId(), result.getAgentId());
        assertNotNull(result.getStartedAt());
        assertEquals(1, customerAgentService.getById(agent.getId()).getCurrentLoad());
    }

    @Test
    void assignAutoShouldFailWhenNoAgentIsAvailable() {
        resetSeedAgent(AgentStatus.OFFLINE, 0, 5);
        CustomerSession session = createWaitingSession();

        assertThrows(BusinessException.class,
                () -> agentAssignService.assignAuto(session.getId()));
    }

    private CustomerAgent resetSeedAgent(AgentStatus status, int currentLoad, int maxSessions) {
        CustomerAgent agent = customerAgentService.getById(1L);
        agent.setStatus(status.getCode());
        agent.setCurrentLoad(currentLoad);
        agent.setMaxSessions(maxSessions);
        customerAgentService.updateById(agent);
        return agent;
    }

    private CustomerSession createWaitingSession() {
        CustomerSession session = new CustomerSession();
        session.setSessionNo("T" + System.currentTimeMillis());
        session.setUserId(1L);
        session.setStatus(SessionStatus.WAITING.getCode());
        session.setChannel("WEB");
        session.setPriority(0);
        customerSessionService.save(session);
        return session;
    }
}
