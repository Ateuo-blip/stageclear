package io.stageclear.customer.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageCreatedEvent {
    private String eventId;
    private Long messageId;
    private Long sessionId;
    private String sessionNo;
    private String senderType;
    private Long senderId;
    private String contentType;
    private LocalDateTime sendTime;
    private String traceId;
}