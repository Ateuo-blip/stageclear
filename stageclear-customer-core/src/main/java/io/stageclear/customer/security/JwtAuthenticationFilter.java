package io.stageclear.customer.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.stageclear.common.security.JwtUtil;
import io.stageclear.customer.config.JwtProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProperties jwtProperties;
    @Override
    protected void doFilterInternal(HttpServletRequest request
            , HttpServletResponse response
            , FilterChain filterChain)
            throws ServletException, IOException {
        String token = resolveToken(request);

        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
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

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            loginUser,
                            null,
                            loginUser.getAuthorities()
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );
            /* 把登录状态放在上下文 */
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException | IllegalArgumentException e) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
    private String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);

        if (!StringUtils.hasText(authorization)) {
            return null;
        }

        if (!authorization.startsWith(BEARER_PREFIX)) {
            return null;
        }

        return authorization.substring(BEARER_PREFIX.length());
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


