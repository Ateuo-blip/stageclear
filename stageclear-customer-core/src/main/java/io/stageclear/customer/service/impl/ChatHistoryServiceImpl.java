package io.stageclear.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.stageclear.common.entity.CustomerMessage;
import io.stageclear.common.entity.CustomerSession;
import io.stageclear.common.enums.SenderType;
import io.stageclear.common.exception.BusinessException;
import io.stageclear.common.exception.ErrorCode;
import io.stageclear.common.result.CursorPageResult;
import io.stageclear.common.service.CustomerMessageService;
import io.stageclear.common.service.CustomerSessionService;
import io.stageclear.customer.security.LoginUser;
import io.stageclear.customer.service.ChatHistoryService;
import io.stageclear.customer.vo.MessageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatHistoryServiceImpl implements ChatHistoryService {

    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 100;
    private static final int READ = 1;
    private static final int UNREAD = 0;

    private final CustomerSessionService customerSessionService;
    private final CustomerMessageService customerMessageService;

    @Override
    public CursorPageResult<MessageVO> scrollSessionMessages(LoginUser loginUser,
                                                             Long sessionId,
                                                             Long beforeMessageId,
                                                             int limit) {
        checkSessionAccess(loginUser, sessionId);

        int safeLimit = normalizeLimit(limit);
        LambdaQueryWrapper<CustomerMessage> query = new LambdaQueryWrapper<CustomerMessage>()
                .eq(CustomerMessage::getSessionId, sessionId)
                .orderByDesc(CustomerMessage::getId)
                .last("LIMIT " + (safeLimit + 1));

        if (beforeMessageId != null) {
            query.lt(CustomerMessage::getId, beforeMessageId);
        }

        List<CustomerMessage> rawMessages = customerMessageService.list(query);
        /**
         * 如果能查出21条说明还有历史消息，可以返回nextCursor
         */
        boolean hasMore = rawMessages.size() > safeLimit;
        List<CustomerMessage> currentSlice = hasMore
                ? new ArrayList<>(rawMessages.subList(0, safeLimit))
                : new ArrayList<>(rawMessages);

        Collections.reverse(currentSlice);
        List<MessageVO> records = currentSlice.stream()
                .map(MessageVO::from)
                .toList();
        Long nextCursor = hasMore && !currentSlice.isEmpty()
                ? currentSlice.get(0).getId()
                : null;

        return CursorPageResult.of(records, nextCursor, hasMore);
    }

    @Override
    public long countUnread(LoginUser loginUser, Long sessionId) {
        checkSessionAccess(loginUser, sessionId);
        return customerMessageService.count(unreadQuery(loginUser, sessionId));
    }

    @Override
    public int markRead(LoginUser loginUser, Long sessionId) {
        checkSessionAccess(loginUser, sessionId);
        return Math.toIntExact(customerMessageService.getBaseMapper().update(
                null,
                unreadUpdate(loginUser, sessionId).set(CustomerMessage::getReadFlag, READ)
        ));
    }

    private void checkSessionAccess(LoginUser loginUser, Long sessionId) {
        if (loginUser == null || sessionId == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        CustomerSession session = customerSessionService.getById(sessionId);
        if (session == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }

        if (loginUser.isUser() && Objects.equals(session.getUserId(), loginUser.getUserId())) {
            return;
        }
        if (loginUser.isAgent() && Objects.equals(session.getAgentId(), loginUser.getAgentId())) {
            return;
        }
        throw new BusinessException(ErrorCode.FORBIDDEN);
    }

    private LambdaQueryWrapper<CustomerMessage> unreadQuery(LoginUser loginUser, Long sessionId) {
        return new LambdaQueryWrapper<CustomerMessage>()
                .eq(CustomerMessage::getSessionId, sessionId)
                .eq(CustomerMessage::getReadFlag, UNREAD)
                .ne(CustomerMessage::getSenderType, currentSenderType(loginUser).getCode());
    }

    private LambdaUpdateWrapper<CustomerMessage> unreadUpdate(LoginUser loginUser, Long sessionId) {
        return new LambdaUpdateWrapper<CustomerMessage>()
                .eq(CustomerMessage::getSessionId, sessionId)
                .eq(CustomerMessage::getReadFlag, UNREAD)
                .ne(CustomerMessage::getSenderType, currentSenderType(loginUser).getCode());
    }

    private SenderType currentSenderType(LoginUser loginUser) {
        return loginUser.isAgent() ? SenderType.AGENT : SenderType.USER;
    }

    private int normalizeLimit(int limit) {
        if (limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }
}
