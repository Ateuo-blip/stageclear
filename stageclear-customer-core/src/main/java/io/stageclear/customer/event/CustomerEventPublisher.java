package io.stageclear.customer.event;

import io.stageclear.customer.event.dto.MessageCreatedEvent;
import io.stageclear.customer.event.dto.SessionAssignedEvent;

public interface CustomerEventPublisher {

    void publishMessageCreated(MessageCreatedEvent event);

    void publishSessionAssigned(SessionAssignedEvent event);
}