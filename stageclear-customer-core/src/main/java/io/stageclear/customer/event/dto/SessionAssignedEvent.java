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
public class SessionAssignedEvent {
    private String eventId;
    private Long sessionId;
    private String sessionNo;
    private Long agentId;
    private String agentNo;
    private Long previousAgentId;
    private String previousAgentNo;
    private String assignType;
    private LocalDateTime assignedAt;
    private String traceId;
}
