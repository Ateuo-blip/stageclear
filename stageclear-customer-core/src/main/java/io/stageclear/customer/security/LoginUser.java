package io.stageclear.customer.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginUser {
    private Long userId;
    private String username;
    private String userType; // USER / AGENT
    private Long agentId;    // 只有坐席有
    private String agentNo;  // 只有坐席有
    private List<String> roles;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null) {
            return List.of();
        }
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    public boolean isUser() {
        return "USER".equals(userType);
    }

    public boolean isAgent() {
        return "AGENT".equals(userType);
    }
}
