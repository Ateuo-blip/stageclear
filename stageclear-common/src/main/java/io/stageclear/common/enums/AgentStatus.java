package io.stageclear.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
@Getter
@AllArgsConstructor
public enum AgentStatus {
    ONLINE  ("ONLINE",  "在线空闲"),
    BUSY    ("BUSY",    "已满"),
    OFFLINE ("OFFLINE", "离线"),
    AWAY    ("AWAY",    "暂时离开");

    @EnumValue
    @JsonValue
    private final String code;

    private final String desc;

    public static AgentStatus of(String code) {
        return Arrays.stream(values())
                .filter(s -> Objects.equals(s.code, code))
                .findFirst()
                .orElse(null);
    }
}
