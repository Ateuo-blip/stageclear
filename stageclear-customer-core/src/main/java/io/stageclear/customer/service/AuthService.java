package io.stageclear.customer.service;

import io.stageclear.customer.dto.LoginRequest;
import io.stageclear.customer.dto.LoginResponse;

public interface AuthService {

    LoginResponse loginUser(LoginRequest request);

    LoginResponse loginAgent(LoginRequest request);
}