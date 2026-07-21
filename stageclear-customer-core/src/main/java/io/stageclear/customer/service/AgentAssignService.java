package io.stageclear.customer.service;

import io.stageclear.customer.vo.SessionVO;

public interface AgentAssignService {
    SessionVO assignAuto(Long sessionId);
}
