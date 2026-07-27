package io.stageclear.customer.delay;

import io.stageclear.customer.delay.dto.AgentReplyTimeoutMessage;
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
    @Value("${stageclear.customer.delay.agent-reply-timeout-delay-level:5}")
    private int agentReplyTimeoutDelayLevel;
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

    @Override
    public void publishAgentReplyTimeout(AgentReplyTimeoutMessage message) {
        String destination = CustomerDelayTopics.CUSTOMER_DELAY
                + ":"
                + CustomerDelayTopics.AGENT_REPLY_TIMEOUT;

        Message<AgentReplyTimeoutMessage> rocketMessage = MessageBuilder
                .withPayload(message)
                .setHeader("KEYS", "session:" + message.getSessionId() + ":message:" + message.getUserMessageId())
                .build();
        rocketMQTemplate.syncSend(
                destination,
                rocketMessage,
                3000,
                agentReplyTimeoutDelayLevel
        );

        log.info("publish agent reply timeout delay message, sessionNo={}, userMessageId={}, eventId={}, delayLevel={}",
                message.getSessionNo(), message.getUserMessageId(), message.getEventId(), agentReplyTimeoutDelayLevel);
    }
}
