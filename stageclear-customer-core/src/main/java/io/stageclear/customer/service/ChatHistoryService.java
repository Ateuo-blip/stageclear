package io.stageclear.customer.service;

import io.stageclear.common.result.CursorPageResult;
import io.stageclear.customer.security.LoginUser;
import io.stageclear.customer.vo.MessageVO;

public interface ChatHistoryService {

    CursorPageResult<MessageVO> scrollSessionMessages(LoginUser loginUser, Long sessionId, Long beforeMessageId, int limit);

    long countUnread(LoginUser loginUser, Long sessionId);

    int markRead(LoginUser loginUser, Long sessionId);
}
