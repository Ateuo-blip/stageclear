package io.stageclear.customer.websocket;

import io.stageclear.customer.vo.MessageVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSendResult {

    private MessageVO message;

    private String receiverType;

    private Long receiverId;
}