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
public class AgentReplyTimeoutMessage {
    private String eventId;

    private Long sessionId;

    private String sessionNo;

    private Long userId;

    private Long agentId;

    private Long userMessageId;

    private LocalDateTime userMessageSendTime;

    private LocalDateTime checkAt;

    private String traceId;
}
