package io.stageclear.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum SenderType {
    USER    ("USER",    "用户"),
    AGENT   ("AGENT",   "客服坐席"),
    AI      ("AI",      "AI 机器人"),
    SYSTEM  ("SYSTEM",  "系统消息");
    @EnumValue
    @JsonValue
    private final String code;

    private final String desc;

    public static SenderType of(String code) {
        return Arrays.stream(values())
                .filter(s -> Objects.equals(s.code, code))
                .findFirst()
                .orElse(null);
    }
}
