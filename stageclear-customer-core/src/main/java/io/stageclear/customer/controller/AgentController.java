package io.stageclear.customer.controller;

import io.stageclear.common.enums.AgentStatus;
import io.stageclear.common.exception.BusinessException;
import io.stageclear.common.exception.ErrorCode;
import io.stageclear.common.result.ApiResponse;
import io.stageclear.customer.security.LoginUser;
import io.stageclear.customer.service.AgentStatusService;
import io.stageclear.customer.vo.AgentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AgentController {
    private final AgentStatusService agentStatusService;

    @PostMapping("/agent/status")
    public ApiResponse<AgentVO> updateStatus(@AuthenticationPrincipal LoginUser loginUser,
                                             @RequestParam("status") String status) {
        AgentStatus agentStatus = AgentStatus.of(status);
        if (agentStatus == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        return ApiResponse.success(agentStatusService.updateStatus(loginUser, agentStatus));
    }
}
