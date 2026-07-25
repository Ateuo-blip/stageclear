package io.stageclear.customer.delay;

import io.stageclear.customer.delay.dto.WaitingSessionTimeoutMessage;

public interface CustomerDelayPublisher {

    void publishWaitingSessionTimeout(WaitingSessionTimeoutMessage message);
}