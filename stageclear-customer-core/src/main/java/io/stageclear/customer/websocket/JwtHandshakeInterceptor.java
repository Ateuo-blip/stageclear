package io.stageclear.customer.websocket;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.stageclear.common.security.JwtUtil;
import io.stageclear.customer.config.JwtProperties;
import io.stageclear.customer.security.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    public static final String LOGIN_USER_ATTRIBUTE = "loginUser";

    private final JwtProperties jwtProperties;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        String token = resolveToken(request);
        if (token == null || token.isBlank()) {
            log.warn("WebSocket handshake rejected: missing token, uri={}", request.getURI());
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        try {
            Claims claims = JwtUtil.parseToken(jwtProperties.getSecret(), token);
            LoginUser loginUser = LoginUser.builder()
                    .userId(getLongClaim(claims, "userId"))
                    .username(claims.get("username", String.class))
                    .userType(claims.get("userType", String.class))
                    .agentId(getLongClaim(claims, "agentId"))
                    .agentNo(claims.get("agentNo", String.class))
                    .roles(getRoles(claims))
                    .build();

            attributes.put(LOGIN_USER_ATTRIBUTE, loginUser);
            log.info("WebSocket handshake accepted: userType={}, userId={}, agentId={}, username={}",
                    loginUser.getUserType(),
                    loginUser.getUserId(),
                    loginUser.getAgentId(),
                    loginUser.getUsername());
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("WebSocket handshake rejected: invalid token, uri={}, reason={}",
                    request.getURI(),
                    e.getMessage());
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
    }

    private String resolveToken(ServerHttpRequest request) {
        MultiValueMap<String, String> queryParams = UriComponentsBuilder
                .fromUri(request.getURI())
                .build()
                .getQueryParams();
        return queryParams.getFirst("token");
    }

    private Long getLongClaim(Claims claims, String key) {
        Object value = claims.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(value.toString());
    }

    private List<String> getRoles(Claims claims) {
        Object value = claims.get("roles");
        if (!(value instanceof List<?> list)) {
            return List.of();
        }

        List<String> roles = new ArrayList<>();
        for (Object item : list) {
            roles.add(String.valueOf(item));
        }
        return roles;
    }
}
