package io.stageclear.customer.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.stageclear.customer.security.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        LoginUser loginUser = getLoginUser(session);

        sendJson(session, Map.of(
                "type", "CONNECTED",
                "userType", loginUser.getUserType(),
                "userId", loginUser.getUserId()
        ));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<?, ?> payload = objectMapper.readValue(message.getPayload(), Map.class);
        Object type = payload.get("type");

        if ("PING".equals(type)) {
            sendJson(session, Map.of(
                    "type", "PONG"
            ));
            return;
        }

        sendJson(session, Map.of(
                "type", "ERROR",
                "message", "Unsupported message type"
        ));
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // TODO下一步再做连接管理，这里先空着。
    }

    private LoginUser getLoginUser(WebSocketSession session) {
        return (LoginUser) session.getAttributes()
                .get(JwtHandshakeInterceptor.LOGIN_USER_ATTRIBUTE);
    }

    private void sendJson(WebSocketSession session, Map<String, Object> payload) throws IOException {
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(payload)));
    }
}
