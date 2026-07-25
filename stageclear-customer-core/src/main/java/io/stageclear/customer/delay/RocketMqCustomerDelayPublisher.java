package io.stageclear.customer.delay;

import io.stageclear.customer.delay.dto.WaitingSessionTimeoutMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RocketMqCustomerDelayPublisher implements CustomerDelayPublisher {

    private final RocketMQTemplate rocketMQTemplate;

    @Value("${stageclear.customer.delay.waiting-session-timeout-delay-level:6}")
    private int waitingSessionTimeoutDelayLevel;
    @Override
    public void publishWaitingSessionTimeout(WaitingSessionTimeoutMessage message) {
        String destination = CustomerDelayTopics.CUSTOMER_DELAY + ":" + CustomerDelayTopics.WAITING_SESSION_TIMEOUT;
        Message<WaitingSessionTimeoutMessage> rocketMessage = MessageBuilder
                .withPayload(message)
                .build();

        rocketMQTemplate.syncSend(
                destination,
                rocketMessage,
                3000,
                waitingSessionTimeoutDelayLevel
        );
        log.info("publish waiting session timeout delay message, sessionNo={}, eventId={}, delayLevel={}",
                message.getSessionNo(), message.getEventId(), waitingSessionTimeoutDelayLevel);
    }
}
