package io.stageclear.customer.vo;

import io.stageclear.common.entity.CustomerTicket;
import io.stageclear.common.enums.TicketStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 工单响应 VO
 */
@Data
@NoArgsConstructor
public class TicketVO {

    private Long id;
    /**
     * 工单号，格式 T + yyyyMMdd + 6位 seq
     */
    private String ticketNo;
    /**
     * 来源会话 id（可空，有些工单不来自会话）
     */
    private Long sessionId;
    private Long userId;
    /**
     * 处理人坐席 id，null 表示待分配
     */
    private Long assigneeId;
    /**
     * 0 低 / 1 中 / 2 高 / 3 紧急
     */
    private Integer priority;
    /**
     * 枚举：OPEN / PROCESSING / PENDING / RESOLVED / CLOSED
     */
    private TicketStatus status;
    private String title;
    private String description;
    /**
     * 售后 / 咨询 / 投诉 / 建议
     */
    private String category;
    /**
     * 解决时间，状态到 RESOLVED 时填
     */
    private LocalDateTime resolvedAt;
    /**
     * 关闭时间，状态到 CLOSED 时填
     */
    private LocalDateTime closedAt;
    private LocalDateTime createdAt;

    public static TicketVO from(CustomerTicket t) {
        if (t == null) return null;
        TicketVO v = new TicketVO();
        v.setId(t.getId());
        v.setTicketNo(t.getTicketNo());
        v.setSessionId(t.getSessionId());
        v.setUserId(t.getUserId());
        v.setAssigneeId(t.getAssigneeId());
        v.setPriority(t.getPriority());
        v.setStatus(TicketStatus.of(t.getStatus()));
        v.setTitle(t.getTitle());
        v.setDescription(t.getDescription());
        v.setCategory(t.getCategory());
        v.setResolvedAt(t.getResolvedAt());
        v.setClosedAt(t.getClosedAt());
        v.setCreatedAt(t.getCreatedAt());
        return v;
    }
}
