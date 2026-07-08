package io.stageclear.customer.vo;

import io.stageclear.common.entity.CustomerSession;
import io.stageclear.common.enums.SessionStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SessionVO {

    // ===== 字段全部跟 Entity 对应，但用枚举替换 String =====
    private Long id;
    /**
     * 业务流水号，格式 S + yyyyMMdd + 6位 seq
     */
    private String sessionNo;
    private Long userId;
    /**
     * null 表示还没分配坐席（WAITING 状态）
     */
    private Long agentId;
    private SessionStatus status;        // ← 注意：枚举，不是 String
    /**
     * 用户来源渠道：WEB / H5 / WECHAT / APP
     */
    private String channel;
    /**
     * 用户来源 URL 或推广参数
     */
    private String source;
    /**
     * 0 普通 / 1 VIP / 2 紧急
     */
    private Integer priority;
    /**
     * 分配坐席的时间，END 时不再变
     */
    private LocalDateTime startedAt;
    /**
     * 会话关闭时间
     */
    private LocalDateTime endedAt;
    /**
     * 结束原因：USER_LEAVE / AGENT_CLOSE / TRANSFER / TIMEOUT
     */
    private String endReason;
    /**
     * 用户评价 1-5 星，null 表示未评价
     */
    private Integer rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ===== 静态工厂方法：Entity 转 VO =====
    public static SessionVO from(CustomerSession s) {
        if (s == null) return null;
        SessionVO v = new SessionVO();
        v.setId(s.getId());
        v.setSessionNo(s.getSessionNo());
        v.setUserId(s.getUserId());
        v.setAgentId(s.getAgentId());
        v.setStatus(SessionStatus.of(s.getStatus()));  // ← 关键转换
        v.setChannel(s.getChannel());
        v.setSource(s.getSource());
        v.setPriority(s.getPriority());
        v.setStartedAt(s.getStartedAt());
        v.setEndedAt(s.getEndedAt());
        v.setEndReason(s.getEndReason());
        v.setRating(s.getRating());
        v.setCreatedAt(s.getCreatedAt());
        v.setUpdatedAt(s.getUpdatedAt());
        return v;
    }
}