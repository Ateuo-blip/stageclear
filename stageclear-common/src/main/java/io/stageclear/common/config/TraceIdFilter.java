package io.stageclear.common.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdFilter extends OncePerRequestFilter {
    private static final String TRACE_ID = "traceId";
    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final int MAX_TRACE_ID_LENGTH = 64;
    private static final Pattern TRACE_ID_PATTERN = Pattern.compile("^[A-Za-z0-9_\\-]+$");


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String incoming = request.getHeader(TRACE_ID_HEADER);
        String traceId;
        if (StringUtils.hasText(incoming) && incoming.length() <= MAX_TRACE_ID_LENGTH && TRACE_ID_PATTERN.matcher(incoming).matches()) {
            traceId = incoming;
        } else {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }
        MDC.put(TRACE_ID, traceId);
        response.setHeader(TRACE_ID_HEADER, traceId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(TRACE_ID);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/actuator/") || uri.equals("/actuator")
                || uri.endsWith("/error");
    }
}
