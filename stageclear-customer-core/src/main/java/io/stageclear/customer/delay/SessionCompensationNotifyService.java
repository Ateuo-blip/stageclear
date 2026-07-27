package io.stageclear.customer.delay;

import io.stageclear.common.entity.CustomerSession;

public interface SessionCompensationNotifyService {
    void notifyWaitingSessionBusy(CustomerSession session);
    void notifyAgentReplyTimeout(CustomerSession session, Long userMessageId);
}
