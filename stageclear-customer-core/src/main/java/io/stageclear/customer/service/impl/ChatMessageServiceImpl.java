package io.stageclear.customer.service.impl;

import io.stageclear.common.entity.CustomerMessage;
import io.stageclear.common.entity.CustomerSession;
import io.stageclear.common.enums.SenderType;
import io.stageclear.common.enums.SessionStatus;
import io.stageclear.common.exception.BusinessException;
import io.stageclear.common.exception.ErrorCode;
import io.stageclear.common.service.CustomerMessageService;
import io.stageclear.common.service.CustomerSessionService;
import io.stageclear.customer.delay.CustomerDelayPublisher;
import io.stageclear.customer.delay.dto.AgentReplyTimeoutMessage;
import io.stageclear.customer.event.CustomerEventPublisher;
import io.stageclear.customer.event.dto.MessageCreatedEvent;
import io.stageclear.customer.security.LoginUser;
import io.stageclear.customer.service.ChatMessageService;
import io.stageclear.customer.vo.MessageVO;
import io.stageclear.customer.websocket.ChatSendResult;
import io.stageclear.customer.websocket.WsMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    private static final String DEFAULT_CONTENT_TYPE = "TEXT";

    private final CustomerSessionService customerSessionService;
    private final CustomerMessageService customerMessageService;
    private final CustomerEventPublisher customerEventPublisher;
    private final CustomerDelayPublisher customerDelayPublisher;
    @Override
    public ChatSendResult handleChatSend(LoginUser loginUser, WsMessage message) {
        if (message.getSessionId() == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        if (message.getContent() == null || message.getContent().isBlank()) {
            throw new BusinessException(400, "消息内容不能为空");
        }

        CustomerSession session = customerSessionService.getById(message.getSessionId());
        if (session == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }

        checkCanSend(loginUser, session);

        CustomerMessage entity = new CustomerMessage();
        entity.setSessionId(session.getId());
        entity.setSenderType(resolveSenderType(loginUser).getCode());
        entity.setSenderId(resolveSenderId(loginUser));
        entity.setContentType(message.getContentType() == null || message.getContentType().isBlank()
                ? DEFAULT_CONTENT_TYPE
                : message.getContentType());
        entity.setContent(message.getContent());
        entity.setSendTime(LocalDateTime.now());
        entity.setReadFlag(0);

        boolean saved = customerMessageService.save(entity);
        if (!saved) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR);
        }
        customerEventPublisher.publishMessageCreated(MessageCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString().replace("-", ""))
                .messageId(entity.getId())
                .sessionId(session.getId())
                .sessionNo(session.getSessionNo())
                .senderType(entity.getSenderType())
                .senderId(entity.getSenderId())
                .contentType(entity.getContentType())
                .sendTime(entity.getSendTime())
                .traceId(MDC.get("traceId"))
                .build());
        //延时检查用户消息是否回复,未回复提醒坐席
        if (SenderType.USER.getCode().equals(entity.getSenderType())) {
            LocalDateTime now = LocalDateTime.now();
            customerDelayPublisher.publishAgentReplyTimeout(AgentReplyTimeoutMessage.builder()
                    .eventId(UUID.randomUUID().toString().replace("-", ""))
                    .sessionId(session.getId())
                    .sessionNo(session.getSessionNo())
                    .userId(session.getUserId())
                    .agentId(session.getAgentId())
                    .userMessageId(entity.getId())
                    .userMessageSendTime(entity.getSendTime())
                    .checkAt(now.plusMinutes(1))
                    .traceId(MDC.get("traceId"))
                    .build());
        }

        return ChatSendResult.builder()
                .message(MessageVO.from(entity))
                .receiverType(resolveReceiverType(loginUser))
                .receiverId(resolveReceiverId(loginUser, session))
                .build();
    }

    private void checkCanSend(LoginUser loginUser, CustomerSession session) {
        if (!SessionStatus.IN_PROGRESS.getCode().equals(session.getStatus())) {
            throw new BusinessException(400, "当前会话状态不允许发送消息");
        }

        if (loginUser.isUser()) {
            if (!Objects.equals(session.getUserId(), loginUser.getUserId())) {
                throw new BusinessException(ErrorCode.FORBIDDEN);
            }
            return;
        }

        if (loginUser.isAgent()) {
            if (!Objects.equals(session.getAgentId(), loginUser.getAgentId())) {
                throw new BusinessException(ErrorCode.FORBIDDEN);
            }
            return;
        }

        throw new BusinessException(ErrorCode.FORBIDDEN);
    }

    private SenderType resolveSenderType(LoginUser loginUser) {
        if (loginUser.isAgent()) {
            return SenderType.AGENT;
        }
        return SenderType.USER;
    }

    private Long resolveSenderId(LoginUser loginUser) {
        if (loginUser.isAgent()) {
            return loginUser.getAgentId();
        }
        return loginUser.getUserId();
    }

    private String resolveReceiverType(LoginUser loginUser) {
        if (loginUser.isAgent()) {
            return SenderType.USER.getCode();
        }
        return SenderType.AGENT.getCode();
    }

    private Long resolveReceiverId(LoginUser loginUser, CustomerSession session) {
        if (loginUser.isAgent()) {
            return session.getUserId();
        }
        return session.getAgentId();
    }
}