package io.stageclear.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
@Getter
@AllArgsConstructor
public enum TicketStatus {
    OPEN        ("OPEN",        "待处理"),
    PROCESSING  ("PROCESSING",  "处理中"),
    PENDING     ("PENDING",     "挂起（等用户反馈）"),
    RESOLVED    ("RESOLVED",    "已解决"),
    CLOSED      ("CLOSED",      "已关闭");

    @EnumValue
    @JsonValue
    private final String code;

    private final String desc;

    public static TicketStatus of(String code) {
        return Arrays.stream(values())
                .filter(s -> Objects.equals(s.code, code))
                .findFirst()
                .orElse(null);
    }
}
