package io.stageclear.customer.delay;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.stageclear.common.entity.CustomerMessage;
import io.stageclear.common.entity.CustomerSession;
import io.stageclear.common.enums.SenderType;
import io.stageclear.common.service.CustomerMessageService;
import io.stageclear.customer.vo.MessageVO;
import io.stageclear.customer.websocket.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionCompensationNotifyServiceImpl implements SessionCompensationNotifyService {

    private static final String BUSY_NOTICE = "当前坐席繁忙，系统正在为您排队，请稍后。";
    private static final String AGENT_REPLY_TIMEOUT_NOTICE = "用户已等待较久，请尽快回复。";

    private final CustomerMessageService customerMessageService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final WebSocketSessionManager webSocketSessionManager;
    private final ObjectMapper objectMapper;

    @Override
    public void notifyWaitingSessionBusy(CustomerSession session) {
        String key = "stageclear:rocketmq:notified:waiting-session-timeout:" + session.getId();
        Boolean firstNotify = redisTemplate.opsForValue().setIfAbsent(key, "1", Duration.ofDays(7));
        if (!Boolean.TRUE.equals(firstNotify)) {
            log.info("waiting session busy notice skipped, already notified, sessionNo={}", session.getSessionNo());
            return;
        }

        CustomerMessage message = new CustomerMessage();
        message.setSessionId(session.getId());
        message.setSenderType(SenderType.SYSTEM.getCode());
        message.setSenderId(null);
        message.setContentType("SYSTEM");
        message.setContent(BUSY_NOTICE);
        message.setSendTime(LocalDateTime.now());
        message.setReadFlag(0);

        boolean saved = customerMessageService.save(message);
        if (!saved) {
            redisTemplate.delete(key);
            throw new IllegalStateException("save waiting session busy notice failed");
        }

        MessageVO messageVO = MessageVO.from(message);
        webSocketSessionManager.getSession(SenderType.USER.getCode(), session.getUserId())
                .ifPresent(userSession -> {
                    try {
                        userSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(Map.of(
                                "type", "CHAT_MESSAGE",
                                "message", messageVO
                        ))));
                    } catch (Exception e) {
                        log.error("push waiting session busy notice failed, sessionNo={}, userId={}",
                                session.getSessionNo(), session.getUserId(), e);
                    }
                });
    }

    @Override
    public void notifyAgentReplyTimeout(CustomerSession session, Long userMessageId) {
        String key = "stageclear:rocketmq:notified:agent-reply-timeout:"
                + session.getId() + ":" + userMessageId;

        Boolean firstNotify = redisTemplate.opsForValue()
                .setIfAbsent(key, "1", Duration.ofDays(7));

        if (!Boolean.TRUE.equals(firstNotify)) {
            log.info("agent reply timeout notice skipped, already notified, sessionNo={}, userMessageId={}",
                    session.getSessionNo(), userMessageId);
            return;
        }

        CustomerMessage message = new CustomerMessage();
        message.setSessionId(session.getId());
        message.setSenderType(SenderType.SYSTEM.getCode());
        message.setSenderId(null);
        message.setContentType("SYSTEM");
        message.setContent(AGENT_REPLY_TIMEOUT_NOTICE);
        message.setSendTime(LocalDateTime.now());
        message.setReadFlag(0);

        boolean saved = customerMessageService.save(message);
        if (!saved) {
            redisTemplate.delete(key);
            throw new IllegalStateException("save agent reply timeout notice failed");
        }

        MessageVO messageVO = MessageVO.from(message);
        webSocketSessionManager.getSession(SenderType.AGENT.getCode(), session.getAgentId())
                .ifPresent(agentSession -> {
                    try {
                        agentSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(Map.of(
                                "type", "CHAT_MESSAGE",
                                "message", messageVO
                        ))));
                    } catch (Exception e) {
                        log.error("push agent reply timeout notice failed, sessionNo={}, agentId={}",
                                session.getSessionNo(), session.getAgentId(), e);
                    }
                });
    }
}
