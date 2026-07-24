package io.stageclear.customer.event.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.stageclear.customer.event.CustomerKafkaTopics;
import io.stageclear.customer.event.dto.MessageCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageEventConsumer {
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    @KafkaListener(topics = CustomerKafkaTopics.MESSAGE_CREATED, groupId = "stageclear-message-group")
    public void onMessage(String payload) throws Exception {
        MessageCreatedEvent event = objectMapper.readValue(payload, MessageCreatedEvent.class);
        if (event.getEventId() == null || event.getEventId().isBlank()) {
            log.warn("kafka eventId is blank, payload={}", payload);
            return;
        }
        String key = "stageclear:kafka:consumed:message-consumer:" + event.getEventId();
        Boolean setFlag = redisTemplate.opsForValue().setIfAbsent(key, "1", Duration.ofDays(7));
        if (!Boolean.TRUE.equals(setFlag)) {
            log.info("duplicate kafka event skipped, key={}", key);
            return;
        }
        MDC.put("traceId", event.getTraceId());
        try {
            log.info("consume message event, messageId={}, sessionNo={}, senderType={}",
                    event.getMessageId(),
                    event.getSessionNo(),
                    event.getSenderType());
        } catch (Exception e) {
            redisTemplate.delete(key);
            throw e;
        } finally {
            MDC.remove("traceId");
        }
    }
}
