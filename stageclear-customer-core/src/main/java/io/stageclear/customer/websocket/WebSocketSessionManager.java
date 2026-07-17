package io.stageclear.customer.websocket;

import io.stageclear.customer.security.LoginUser;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionManager {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public String register(LoginUser loginUser, WebSocketSession session) {
        String clientKey = buildClientKey(loginUser);
        sessions.put(clientKey, session);
        return clientKey;
    }

    public void unregister(LoginUser loginUser,WebSocketSession session) {
        sessions.remove(buildClientKey(loginUser), session);
    }

    public Optional<WebSocketSession> getSession(LoginUser loginUser) {
        return Optional.ofNullable(sessions.get(buildClientKey(loginUser)));
    }

    public int onlineCount() {
        return sessions.size();
    }

    private String buildClientKey(LoginUser loginUser) {
        if (loginUser.isAgent()) {
            return "AGENT:" + loginUser.getAgentId();
        }
        return "USER:" + loginUser.getUserId();
    }
}