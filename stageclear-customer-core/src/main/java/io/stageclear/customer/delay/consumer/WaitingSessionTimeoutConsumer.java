package io.stageclear.customer.delay.consumer;

import io.stageclear.common.entity.CustomerSession;
import io.stageclear.common.enums.SessionStatus;
import io.stageclear.common.exception.NoAvailableAgentException;
import io.stageclear.common.service.CustomerSessionService;
import io.stageclear.customer.delay.CustomerDelayTopics;
import io.stageclear.customer.delay.SessionCompensationNotifyService;
import io.stageclear.customer.delay.dto.WaitingSessionTimeoutMessage;
import io.stageclear.customer.service.AgentAssignService;
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
        selectorExpression = CustomerDelayTopics.WAITING_SESSION_TIMEOUT,
        consumerGroup = "stageclear-waiting-session-timeout-group"
)
public class WaitingSessionTimeoutConsumer implements RocketMQListener<WaitingSessionTimeoutMessage> {

    private static final int MAX_NO_AGENT_ATTEMPTS = 2;

    private final CustomerSessionService customerSessionService;
    private final AgentAssignService agentAssignService;
    private final SessionCompensationNotifyService sessionCompensationNotifyService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onMessage(WaitingSessionTimeoutMessage message) {
        if (message == null || message.getEventId() == null || message.getEventId().isBlank()) {
            log.warn("rocketmq delay message eventId is blank, message={}", message);
            return;
        }
        String consumedKey = "stageclear:rocketmq:consumed:" + message.getEventId();
        String retryKey = "stageclear:rocketmq:retry:waiting-session-timeout:" + message.getEventId();

        Boolean firstConsume = redisTemplate.opsForValue()
                .setIfAbsent(consumedKey, "1", Duration.ofDays(7));

        if (!Boolean.TRUE.equals(firstConsume)) {
            log.info("duplicate rocketmq delay message ignored, eventId={}", message.getEventId());
            return;
        }

        try {
            MDC.put("traceId", message.getTraceId());

            CustomerSession session = customerSessionService.getById(message.getSessionId());
            if (session == null) {
                log.warn("waiting session timeout ignored, session not found, sessionId={}", message.getSessionId());
                return;
            }

            if (!Objects.equals(session.getSessionNo(), message.getSessionNo())) {
                log.warn("waiting session timeout ignored, sessionNo mismatch, messageSessionNo={}, dbSessionNo={}",
                        message.getSessionNo(), session.getSessionNo());
                return;
            }

            if (!Objects.equals(session.getStatus(), SessionStatus.WAITING.getCode())) {
                log.info("waiting session timeout ignored, session already changed, sessionNo={}, status={}",
                        session.getSessionNo(), session.getStatus());
                return;
            }

            if (session.getAgentId() != null) {
                log.info("waiting session timeout ignored, session already assigned, sessionNo={}, agentId={}",
                        session.getSessionNo(), session.getAgentId());
                return;
            }

            log.info("waiting session timeout detected, try auto assign, sessionNo={}", session.getSessionNo());
            agentAssignService.assignAuto(session.getId());
        } catch (NoAvailableAgentException e) {
            CustomerSession session = customerSessionService.getById(message.getSessionId());
            Long attempts = redisTemplate.opsForValue().increment(retryKey);
            redisTemplate.expire(retryKey, Duration.ofDays(7));

            if (attempts != null && attempts >= MAX_NO_AGENT_ATTEMPTS && session != null) {
                log.warn("waiting session auto assign failed twice, notify user, sessionNo={}, attempts={}",
                        session.getSessionNo(), attempts);
                sessionCompensationNotifyService.notifyWaitingSessionBusy(session);
                return;
            }

            redisTemplate.delete(consumedKey);
            log.warn("waiting session auto assign no available agent, retry later, eventId={}, sessionId={}, attempts={}",
                    message.getEventId(), message.getSessionId(), attempts);
            throw e;
        } catch (Exception e) {
            redisTemplate.delete(consumedKey);
            log.error("consume waiting session timeout message failed, eventId={}, sessionId={}",
                    message.getEventId(), message.getSessionId(), e);
            throw e;
        } finally {
            MDC.remove("traceId");
        }
    }
}
