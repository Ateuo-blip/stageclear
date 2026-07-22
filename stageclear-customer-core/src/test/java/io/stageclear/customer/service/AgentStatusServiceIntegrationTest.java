package io.stageclear.customer.service;

import io.stageclear.common.entity.CustomerAgent;
import io.stageclear.common.enums.AgentStatus;
import io.stageclear.common.exception.BusinessException;
import io.stageclear.common.service.CustomerAgentService;
import io.stageclear.customer.security.LoginUser;
import io.stageclear.customer.vo.AgentVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class AgentStatusServiceIntegrationTest {

    @Autowired
    private AgentStatusService agentStatusService;

    @Autowired
    private CustomerAgentService customerAgentService;

    @Test
    void agentCanTransitFromOfflineToOnline() {
        CustomerAgent agent = resetSeedAgent(AgentStatus.OFFLINE);
        LoginUser loginUser = agentLoginUser(agent);

        AgentVO result = agentStatusService.updateStatus(loginUser, AgentStatus.ONLINE);

        assertEquals(AgentStatus.ONLINE, result.getStatus());
        assertEquals(AgentStatus.ONLINE.getCode(), customerAgentService.getById(agent.getId()).getStatus());
    }

    @Test
    void agentCanNotTransitFromOfflineToBusy() {
        CustomerAgent agent = resetSeedAgent(AgentStatus.OFFLINE);
        LoginUser loginUser = agentLoginUser(agent);

        assertThrows(BusinessException.class,
                () -> agentStatusService.updateStatus(loginUser, AgentStatus.BUSY));
    }

    private CustomerAgent resetSeedAgent(AgentStatus status) {
        CustomerAgent agent = customerAgentService.getById(1L);
        agent.setStatus(status.getCode());
        agent.setCurrentLoad(0);
        agent.setMaxSessions(5);
        customerAgentService.updateById(agent);
        return agent;
    }

    private LoginUser agentLoginUser(CustomerAgent agent) {
        return LoginUser.builder()
                .userId(agent.getUserId())
                .username("agent_zhang")
                .userType("AGENT")
                .agentId(agent.getId())
                .agentNo(agent.getAgentNo())
                .build();
    }
}
