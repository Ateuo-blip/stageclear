package io.stageclear.customer.service;

import io.stageclear.customer.security.LoginUser;
import io.stageclear.customer.websocket.ChatSendResult;
import io.stageclear.customer.websocket.WsMessage;

public interface ChatMessageService {

    ChatSendResult handleChatSend(LoginUser loginUser, WsMessage message);
}
