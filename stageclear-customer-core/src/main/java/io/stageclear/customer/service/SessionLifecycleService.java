package io.stageclear.customer.service;

import io.stageclear.customer.vo.SessionVO;

public interface SessionLifecycleService {

    SessionVO createSession(Long userId, String channel, String source);

    SessionVO assignAgent(Long sessionId, Long agentId);

    SessionVO transferSession(Long sessionId);

    SessionVO endSession(Long sessionId, String endReason);
}