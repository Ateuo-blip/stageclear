package io.stageclear.customer.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Component
@ConfigurationProperties(prefix = "stageclear.jwt")
public class JwtProperties {

    @NotBlank(message = "JWT secret 不能为空")
    private String secret;

    @Min(value = 60, message = "JWT 过期时间不能小于 60 秒")
    private Long expireSeconds = 7200L;
}