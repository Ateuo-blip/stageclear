package io.stageclear.customer.service.impl;

import io.stageclear.common.entity.CustomerAgent;
import io.stageclear.common.entity.SysUser;
import io.stageclear.common.exception.BusinessException;
import io.stageclear.common.exception.ErrorCode;
import io.stageclear.common.security.JwtUtil;
import io.stageclear.common.service.CustomerAgentService;
import io.stageclear.common.service.SysUserService;
import io.stageclear.customer.config.JwtProperties;
import io.stageclear.customer.dto.LoginRequest;
import io.stageclear.customer.dto.LoginResponse;
import io.stageclear.customer.service.AuthService;
import io.stageclear.customer.vo.AgentVO;
import io.stageclear.customer.vo.SysUserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final Integer USER_STATUS_ENABLED = 1;
    private static final String TOKEN_TYPE = "Bearer";
    private static final String USER_TYPE_USER = "USER";
    private static final String USER_TYPE_AGENT = "AGENT";
    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_AGENT = "ROLE_AGENT";

    private final SysUserService sysUserService;
    private final CustomerAgentService customerAgentService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;
    @Override
    public LoginResponse loginUser(LoginRequest request) {
        SysUser user = getEnabledUserByUsername(request.getUsername());
        checkPassword(request.getPassword(),user.getPassword());

        HashMap<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        claims.put("userType", USER_TYPE_USER);
        claims.put("roles", List.of(ROLE_USER));

        String token = JwtUtil.generateToken(
                jwtProperties.getSecret(),
                jwtProperties.getExpireSeconds(),
                String.valueOf(user.getId()),
                claims
        );
        return LoginResponse.builder()
                .token(token)
                .tokenType(TOKEN_TYPE)
                .expiresIn(jwtProperties.getExpireSeconds())
                .userType(USER_TYPE_USER)
                .profile(SysUserVO.from(user))
                .build();
    }

    @Override
    public LoginResponse loginAgent(LoginRequest request) {
        SysUser user = getEnabledUserByUsername(request.getUsername());
        checkPassword(request.getPassword(), user.getPassword());

        CustomerAgent agent = customerAgentService.lambdaQuery()
                .eq(CustomerAgent::getUserId, user.getId())
                .one();

        if (agent == null) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        claims.put("userType", USER_TYPE_AGENT);
        claims.put("agentId", agent.getId());
        claims.put("agentNo", agent.getAgentNo());
        claims.put("roles", List.of(ROLE_AGENT));

        String token = JwtUtil.generateToken(
                jwtProperties.getSecret(),
                jwtProperties.getExpireSeconds(),
                String.valueOf(user.getId()),
                claims
        );

        return LoginResponse.builder()
                .token(token)
                .tokenType(TOKEN_TYPE)
                .expiresIn(jwtProperties.getExpireSeconds())
                .userType(USER_TYPE_AGENT)
                .profile(AgentVO.from(agent))
                .build();
    }


    private SysUser getEnabledUserByUsername(String username) {
        SysUser user = sysUserService.lambdaQuery()
                .eq(SysUser::getUsername, username)
                .one();

        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        if (!USER_STATUS_ENABLED.equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        return user;
    }

    private void checkPassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
    }

}
