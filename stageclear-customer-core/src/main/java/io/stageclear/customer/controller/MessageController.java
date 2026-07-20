package io.stageclear.customer.controller;

import io.stageclear.common.result.ApiResponse;
import io.stageclear.common.result.CursorPageResult;
import io.stageclear.customer.security.LoginUser;
import io.stageclear.customer.service.ChatHistoryService;
import io.stageclear.customer.vo.MessageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/session/{sessionId}/messages")
@RequiredArgsConstructor
public class MessageController {

    private final ChatHistoryService chatHistoryService;

    @GetMapping
    public ApiResponse<CursorPageResult<MessageVO>> scrollSessionMessages(@AuthenticationPrincipal LoginUser loginUser,
                                                                          @PathVariable("sessionId") Long sessionId,
                                                                          @RequestParam(value = "beforeMessageId", required = false) Long beforeMessageId,
                                                                          @RequestParam(value = "limit", defaultValue = "20") int limit) {
        return ApiResponse.success(
                chatHistoryService.scrollSessionMessages(loginUser, sessionId, beforeMessageId, limit)
        );
    }

    @GetMapping("/unread-count")
    public ApiResponse<Map<String, Long>> countUnread(@AuthenticationPrincipal LoginUser loginUser,
                                                      @PathVariable("sessionId") Long sessionId) {
        return ApiResponse.success(Map.of("unreadCount", chatHistoryService.countUnread(loginUser, sessionId)));
    }

    @PostMapping("/read")
    public ApiResponse<Map<String, Integer>> markRead(@AuthenticationPrincipal LoginUser loginUser,
                                                      @PathVariable("sessionId") Long sessionId) {
        return ApiResponse.success(Map.of("readCount", chatHistoryService.markRead(loginUser, sessionId)));
    }
}
