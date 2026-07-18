package io.stageclear.customer.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WsMessage {

    /**
     * PING / PONG / CHAT_SEND / CHAT_MESSAGE / ACK / ERROR
     */
    private String type;

    /**
     * 客服会话 id，PING/PONG 可以为空
     */
    private Long sessionId;

    /**
     * TEXT / IMAGE / FILE / SYSTEM
     */
    private String contentType;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 客户端发送时间戳，毫秒
     */
    private Long timestamp;
}