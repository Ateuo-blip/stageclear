package io.stageclear.customer.vo;

import io.stageclear.common.entity.SysUser;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户响应 VO（不返回密码）
 */
@Data
@NoArgsConstructor
public class SysUserVO {

    private Long id;
    /**
     * 登录名，唯一
     */
    private String username;
    /**
     * 昵称（前端显示）
     */
    private String nickname;
    /**
     * 邮箱，唯一
     */
    private String email;
    /**
     * 0 禁用 / 1 启用
     */
    private Integer status;
    private LocalDateTime createdAt;

    public static SysUserVO from(SysUser u) {
        if (u == null) return null;
        SysUserVO v = new SysUserVO();
        v.setId(u.getId());
        v.setUsername(u.getUsername());
        v.setNickname(u.getNickname());
        v.setEmail(u.getEmail());
        v.setStatus(u.getStatus());
        v.setCreatedAt(u.getCreatedAt());
        // 注意：故意不返回 password 字段，安全考虑
        return v;
    }
}
