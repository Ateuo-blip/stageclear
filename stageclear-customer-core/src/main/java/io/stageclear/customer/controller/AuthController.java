package io.stageclear.customer.controller;

import io.stageclear.common.exception.BusinessException;
import io.stageclear.common.exception.ErrorCode;
import io.stageclear.common.result.ApiResponse;
import io.stageclear.customer.dto.LoginRequest;
import io.stageclear.customer.dto.LoginResponse;
import io.stageclear.customer.security.LoginUser;
import io.stageclear.customer.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/login")
    public ApiResponse<LoginResponse> loginUser(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.loginUser(request));
    }

    @PostMapping("/agent/login")
    public ApiResponse<LoginResponse> loginAgent(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.loginAgent(request));
    }

    @GetMapping("/auth/me")
    public ApiResponse<LoginUser> authMe(@AuthenticationPrincipal LoginUser loginUser) {
        if (!loginUser.isUser()){
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return ApiResponse.success(loginUser);
    }

    @GetMapping("/agent/me")
    public ApiResponse<LoginUser> agentMe(@AuthenticationPrincipal LoginUser loginUser) {
        if (!loginUser.isAgent()){
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return ApiResponse.success(loginUser);
    }
}