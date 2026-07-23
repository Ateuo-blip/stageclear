package io.stageclear.customer.event.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.stageclear.customer.event.CustomerKafkaTopics;
import io.stageclear.customer.event.dto.SessionAssignedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionEventConsumer {
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = CustomerKafkaTopics.SESSION_EVENT, groupId = "stageclear-session-group")
    public void onMessage(String payload) throws JsonProcessingException {
        SessionAssignedEvent event = objectMapper.readValue(payload, SessionAssignedEvent.class);
        MDC.put("traceId", event.getTraceId());
        try {
            log.info("consume session event, sessionNo={}, agentNo={}, assignType={}",
                    event.getSessionNo(),
                    event.getAgentNo(),
                    event.getAssignType());
        } finally {
            MDC.remove("traceId");
        }
    }
}
