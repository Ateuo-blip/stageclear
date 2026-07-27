package io.stageclear.customer.delay.consumer;

import io.stageclear.common.entity.CustomerMessage;
import io.stageclear.common.entity.CustomerSession;
import io.stageclear.common.enums.SenderType;
import io.stageclear.common.enums.SessionStatus;
import io.stageclear.common.service.CustomerMessageService;
import io.stageclear.common.service.CustomerSessionService;
import io.stageclear.customer.delay.CustomerDelayTopics;
import io.stageclear.customer.delay.SessionCompensationNotifyService;
import io.stageclear.customer.delay.dto.AgentReplyTimeoutMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.MDC;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = CustomerDelayTopics.CUSTOMER_DELAY,
        selectorExpression = CustomerDelayTopics.AGENT_REPLY_TIMEOUT,
        consumerGroup = "stageclear-agent-reply-timeout-group"
)
public class AgentReplyTimeoutConsumer implements RocketMQListener<AgentReplyTimeoutMessage> {

    private final CustomerSessionService customerSessionService;
    private final CustomerMessageService customerMessageService;
    private final SessionCompensationNotifyService sessionCompensationNotifyService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onMessage(AgentReplyTimeoutMessage message) {
        if (message == null || message.getEventId() == null || message.getEventId().isBlank()
                || message.getSessionId() == null || message.getUserMessageId() == null) {
            log.warn("invalid agent reply timeout message, message={}", message);
            return;
        }

        String consumedKey = "stageclear:rocketmq:consumed:" + message.getEventId();
        Boolean firstConsume = redisTemplate.opsForValue()
                .setIfAbsent(consumedKey, "1", Duration.ofDays(7));

        if (!Boolean.TRUE.equals(firstConsume)) {
            log.info("duplicate agent reply timeout message ignored, eventId={}", message.getEventId());
            return;
        }

        try {
            MDC.put("traceId", message.getTraceId());

            CustomerSession session = customerSessionService.getById(message.getSessionId());
            if (session == null) {
                log.warn("agent reply timeout ignored, session not found, sessionId={}", message.getSessionId());
                return;
            }

            if (!Objects.equals(session.getSessionNo(), message.getSessionNo())) {
                log.warn("agent reply timeout ignored, sessionNo mismatch, messageSessionNo={}, dbSessionNo={}",
                        message.getSessionNo(), session.getSessionNo());
                return;
            }

            if (!SessionStatus.IN_PROGRESS.getCode().equals(session.getStatus())) {
                log.info("agent reply timeout ignored, session status changed, sessionNo={}, status={}",
                        session.getSessionNo(), session.getStatus());
                return;
            }

            if (!Objects.equals(session.getAgentId(), message.getAgentId())) {
                log.info("agent reply timeout ignored, agent changed, sessionNo={}, messageAgentId={}, dbAgentId={}",
                        session.getSessionNo(), message.getAgentId(), session.getAgentId());
                return;
            }

            CustomerMessage userMessage = customerMessageService.getById(message.getUserMessageId());
            if (userMessage == null || !SenderType.USER.getCode().equals(userMessage.getSenderType())) {
                log.warn("agent reply timeout ignored, user message invalid, userMessageId={}",
                        message.getUserMessageId());
                return;
            }

            boolean replied = customerMessageService.lambdaQuery()
                    .eq(CustomerMessage::getSessionId, session.getId())
                    .eq(CustomerMessage::getSenderType, SenderType.AGENT.getCode())
                    .gt(CustomerMessage::getId, message.getUserMessageId())
                    .exists();

            if (replied) {
                log.info("agent reply timeout ignored, agent already replied, sessionNo={}, userMessageId={}",
                        session.getSessionNo(), message.getUserMessageId());
                return;
            }

            log.warn("agent reply timeout detected, notify agent, sessionNo={}, agentId={}, userMessageId={}",
                    session.getSessionNo(), session.getAgentId(), message.getUserMessageId());

            sessionCompensationNotifyService.notifyAgentReplyTimeout(session, message.getUserMessageId());
        } catch (Exception e) {
            redisTemplate.delete(consumedKey);
            log.error("consume agent reply timeout message failed, eventId={}, sessionId={}, userMessageId={}",
                    message.getEventId(), message.getSessionId(), message.getUserMessageId(), e);
            throw e;
        } finally {
            MDC.remove("traceId");
        }
    }
}