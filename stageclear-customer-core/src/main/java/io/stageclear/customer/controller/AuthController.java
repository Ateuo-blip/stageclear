package io.stageclear.customer.controller;

import io.stageclear.common.result.ApiResponse;
import io.stageclear.customer.dto.LoginRequest;
import io.stageclear.customer.dto.LoginResponse;
import io.stageclear.customer.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
}