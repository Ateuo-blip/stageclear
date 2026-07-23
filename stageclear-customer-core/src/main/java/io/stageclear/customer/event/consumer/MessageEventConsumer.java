package io.stageclear.customer.event.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.stageclear.customer.event.CustomerKafkaTopics;
import io.stageclear.customer.event.dto.MessageCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageEventConsumer {
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = CustomerKafkaTopics.MESSAGE_CREATED, groupId = "stageclear-message-group")
    public void onMessage(String payload) throws JsonProcessingException {
        MessageCreatedEvent event = objectMapper.readValue(payload, MessageCreatedEvent.class);

        MDC.put("traceId", event.getTraceId());
        try {
            log.info("consume message event, messageId={}, sessionNo={}, senderType={}",
                    event.getMessageId(),
                    event.getSessionNo(),
                    event.getSenderType());
        } finally {
            MDC.remove("traceId");
        }
    }
}
