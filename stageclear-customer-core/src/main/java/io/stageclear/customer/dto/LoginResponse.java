package io.stageclear.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /**
     * JWT token
     */
    private String token;

    /**
     * token 类型，固定 Bearer
     */
    private String tokenType;

    /**
     * 过期时间，单位秒
     */
    private Long expiresIn;

    /**
     * USER / AGENT
     */
    private String userType;

    /**
     * 当前登录人信息：普通用户是 SysUserVO，坐席是 AgentVO
     */
    private Object profile;
}