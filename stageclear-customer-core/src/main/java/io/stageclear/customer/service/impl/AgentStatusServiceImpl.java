package io.stageclear.customer.service.impl;

import io.stageclear.common.entity.CustomerAgent;
import io.stageclear.common.enums.AgentStatus;
import io.stageclear.common.exception.BusinessException;
import io.stageclear.common.service.CustomerAgentService;
import io.stageclear.customer.security.LoginUser;
import io.stageclear.customer.service.AgentStatusService;
import io.stageclear.customer.service.AgentStatusStateMachine;
import io.stageclear.customer.vo.AgentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static io.stageclear.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AgentStatusServiceImpl implements AgentStatusService {
    private final AgentStatusStateMachine agentStatusStateMachine;
    private final CustomerAgentService customerAgentService;
    @Override
    public AgentVO updateStatus(LoginUser loginUser, AgentStatus status) {
        if (loginUser == null || status == null) {
            throw new BusinessException(BAD_REQUEST);
        }
        //判断是否是坐席
        if(!loginUser.isAgent()){
            //非坐席不可更改
            throw new BusinessException(FORBIDDEN);
        }
        //获取修改前状态
        CustomerAgent agent = customerAgentService.getById(loginUser.getAgentId());
        if (agent == null) {
            throw new BusinessException(NOT_FOUND);
        }
        AgentStatus fromStatus = AgentStatus.of(agent.getStatus());
        agentStatusStateMachine.checkTransit(fromStatus, status);
        boolean updated = customerAgentService.lambdaUpdate()
                .eq(CustomerAgent::getId, agent.getId())
                .eq(CustomerAgent::getStatus, agent.getStatus())
                .set(CustomerAgent::getStatus, status.getCode())
                .update();
        if (!updated) {
            throw new BusinessException(400, "坐席状态已变化，请刷新后重试");
        }
        CustomerAgent updatedAgent = customerAgentService.getById(agent.getId());
        return AgentVO.from(updatedAgent);
    }

}
