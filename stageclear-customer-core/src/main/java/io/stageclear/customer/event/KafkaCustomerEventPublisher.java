package io.stageclear.customer.event;

import io.stageclear.customer.event.dto.MessageCreatedEvent;
import io.stageclear.customer.event.dto.SessionAssignedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCustomerEventPublisher implements CustomerEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishMessageCreated(MessageCreatedEvent event) {
        send(CustomerKafkaTopics.MESSAGE_CREATED, event);
    }

    @Override
    public void publishSessionAssigned(SessionAssignedEvent event) {
        send(CustomerKafkaTopics.SESSION_EVENT, event);
    }

    private void send(String topic, Object event) {
        kafkaTemplate.send(topic, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("publish kafka event failed, topic={}, event={}", topic, event, ex);
                        return;
                    }
                    log.info("publish kafka event success, topic={}, offset={}",
                            topic,
                            result.getRecordMetadata().offset());
                });
    }
}
