package io.stageclear.customer.vo;

import io.stageclear.common.entity.CustomerMessage;
import io.stageclear.common.enums.SenderType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息响应 VO
 */
@Data
@NoArgsConstructor
public class MessageVO {

    private Long id;
    /**
     * 所属会话 id
     */
    private Long sessionId;
    /**
     * 枚举：USER(用户) / AGENT(坐席) / AI(机器人) / SYSTEM(系统)
     */
    private SenderType senderType;
    /**
     * 发送者 id；SYSTEM 类型时为 null
     */
    private Long senderId;
    /**
     * TEXT / IMAGE / FILE / SYSTEM
     */
    private String contentType;
    /**
     * 消息内容；非 TEXT 类型时存 JSON 包装
     */
    private String content;
    /**
     * 毫秒精度发送时间
     */
    private LocalDateTime sendTime;
    /**
     * 0 未读 / 1 已读（对面是否看过）
     */
    private Integer readFlag;
    private LocalDateTime createdAt;

    public static MessageVO from(CustomerMessage m) {
        if (m == null) return null;
        MessageVO v = new MessageVO();
        v.setId(m.getId());
        v.setSessionId(m.getSessionId());
        v.setSenderType(SenderType.of(m.getSenderType()));
        v.setSenderId(m.getSenderId());
        v.setContentType(m.getContentType());
        v.setContent(m.getContent());
        v.setSendTime(m.getSendTime());
        v.setReadFlag(m.getReadFlag());
        v.setCreatedAt(m.getCreatedAt());
        return v;
    }
}
