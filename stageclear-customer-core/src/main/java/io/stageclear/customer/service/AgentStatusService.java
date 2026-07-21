package io.stageclear.customer.service;

import io.stageclear.common.enums.AgentStatus;
import io.stageclear.customer.security.LoginUser;
import io.stageclear.customer.vo.AgentVO;

public interface AgentStatusService {
    AgentVO updateStatus(LoginUser loginUser, AgentStatus status);
}