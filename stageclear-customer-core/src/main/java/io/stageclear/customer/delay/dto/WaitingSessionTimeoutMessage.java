package io.stageclear.customer.delay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaitingSessionTimeoutMessage {
    private String eventId;

    private Long sessionId;

    private String sessionNo;

    private Long userId;

    private LocalDateTime createdAt;

    private LocalDateTime checkAt;

    private String reason;

    private String traceId;
}
