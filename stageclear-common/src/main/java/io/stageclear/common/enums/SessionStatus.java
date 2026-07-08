package io.stageclear.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
@Getter
@AllArgsConstructor
public enum SessionStatus {
    WAITING       ("WAITING",       "等待分配坐席"),
    IN_PROGRESS   ("IN_PROGRESS",   "服务中"),
    TRANSFERRING  ("TRANSFERRING",  "转接中"),
    ENDED         ("ENDED",         "已结束");

    @EnumValue
    @JsonValue
    private final String code;

    private final String desc;

    public static SessionStatus of(String code) {
        return Arrays.stream(values())
                .filter(s -> Objects.equals(s.code, code))
                .findFirst()
                .orElse(null);
    }
}
