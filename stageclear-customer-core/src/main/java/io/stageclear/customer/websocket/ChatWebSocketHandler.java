package io.stageclear.customer.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.stageclear.common.exception.BusinessException;
import io.stageclear.customer.security.LoginUser;
import io.stageclear.customer.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final WebSocketSessionManager sessionManager;
    private final ObjectMapper objectMapper;
    private final ChatMessageService chatMessageService;
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        LoginUser loginUser = getLoginUser(session);
        String clientKey = sessionManager.register(loginUser, session);
        sendJson(session, Map.of(
                "type", "CONNECTED",
                "clientKey", clientKey,
                "userType", loginUser.getUserType(),
                "userId", loginUser.getUserId(),
                "onlineCount", sessionManager.onlineCount()
        ));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        WsMessage payload = objectMapper.readValue(message.getPayload(), WsMessage.class);
        String type = payload.getType();
        LoginUser loginUser = getLoginUser(session);
        if ("PING".equals(type)) {
            sendJson(session, WsMessage.builder()
                    .type("PONG")
                    .timestamp(System.currentTimeMillis())
                    .build());
            return;
        }

        if ("CHAT_SEND".equals(type)) {
            try {
                ChatSendResult result = chatMessageService.handleChatSend(loginUser, payload);
                sendJson(session, Map.of(
                        "type", "ACK",
                        "ackType", "SAVED",
                        "message", result.getMessage()
                ));

                sessionManager.getSession(result.getReceiverType(), result.getReceiverId())
                        .ifPresent(receiverSession -> {
                            try {
                                sendJson(receiverSession, Map.of(
                                        "type", "CHAT_MESSAGE",
                                        "message", result.getMessage()
                                ));
                            } catch (IOException e) {
                                log.error("Failed to push websocket message to receiver: receiverType={}, receiverId={}",
                                        result.getReceiverType(), result.getReceiverId(), e);
                            }
                        });
            } catch (BusinessException e) {
                sendJson(session, Map.of(
                        "type", "ERROR",
                        "code", e.getCode(),
                        "message", e.getMessage()
                ));
            } catch (Exception e) {
                sendJson(session, Map.of(
                        "type", "ERROR",
                        "code", 500,
                        "message", "消息处理失败"
                ));
            }
            return;
        }
        sendJson(session, Map.of(
                "type", "ERROR",
                "message", "Unsupported message type"
        ));
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        LoginUser loginUser = getLoginUser(session);
        sessionManager.unregister(loginUser, session);
    }

    private LoginUser getLoginUser(WebSocketSession session) {
        return (LoginUser) session.getAttributes()
                .get(JwtHandshakeInterceptor.LOGIN_USER_ATTRIBUTE);
    }

    private void sendJson(WebSocketSession session, Object payload) throws IOException {
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(payload)));
    }
}
