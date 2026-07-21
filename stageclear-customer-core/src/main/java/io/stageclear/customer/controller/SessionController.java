package io.stageclear.customer.controller;

import io.stageclear.common.exception.BusinessException;
import io.stageclear.common.exception.ErrorCode;
import io.stageclear.common.result.ApiResponse;
import io.stageclear.customer.security.LoginUser;
import io.stageclear.customer.service.AgentAssignService;
import io.stageclear.customer.service.SessionLifecycleService;
import io.stageclear.customer.vo.SessionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/session")
@RequiredArgsConstructor
public class SessionController {
    private final SessionLifecycleService sessionLifecycleService;
    private final AgentAssignService agentAssignService;

    @PostMapping("/create")
    public ApiResponse<SessionVO> createSession(@AuthenticationPrincipal LoginUser loginUser,
                                                @RequestParam(value = "channel",required = false) String channel,
                                                @RequestParam(value = "source",required = false) String source){
        if (!loginUser.isUser()) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return ApiResponse.success(
                sessionLifecycleService.createSession(loginUser.getUserId(), channel, source)
        );
    }

    @PostMapping("/{sessionId}/assign/{agentId}")
    public ApiResponse<SessionVO> assignAgent(@AuthenticationPrincipal LoginUser loginUser,
                                              @PathVariable("sessionId") Long sessionId,
                                              @PathVariable("agentId") Long agentId) {
        if (!loginUser.isAgent()) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return ApiResponse.success(
                sessionLifecycleService.assignAgent(sessionId, agentId)
        );
    }

    @PostMapping("/{sessionId}/transfer")
    public ApiResponse<SessionVO> transferSession (@AuthenticationPrincipal LoginUser loginUser,
                                                   @PathVariable("sessionId") Long sessionId) {
        if (!loginUser.isAgent()) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return ApiResponse.success(
                sessionLifecycleService.transferSession(sessionId)
        );
    }

    @PostMapping("/{sessionId}/endSession")
    public ApiResponse<SessionVO> endSession (@AuthenticationPrincipal LoginUser loginUser,
                                              @PathVariable("sessionId") Long sessionId,@RequestParam("endReason") String endReason) {
        if (!loginUser.isAgent()) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return ApiResponse.success(
                sessionLifecycleService.endSession(sessionId, endReason)
        );
    }

    @PostMapping("/{sessionId}/assign-auto")
    public ApiResponse<SessionVO> assignAuto(@AuthenticationPrincipal LoginUser loginUser,
                                             @PathVariable("sessionId") Long sessionId) {
        if (!loginUser.isAgent()) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return ApiResponse.success(agentAssignService.assignAuto(sessionId));
    }
}
