package io.stageclear.common.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("customer_ticket")   
public class CustomerTicket implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 工单号 T + yyyyMMdd + 6位seq
     */
    private String ticketNo;

    /**
     * 来源会话 id
     */
    private Long sessionId;

    /**
     * 工单所属用户 id
     */
    private Long userId;

    /**
     * 处理人坐席 id
     */
    private Long assigneeId;

    /**
     * 优先级 0低/1中/2高/3紧急
     */
    private Integer priority;

    /**
     * 状态 OPEN/PROCESSING/PENDING/RESOLVED/CLOSED
     */
    private String status;

    /**
     * 工单标题
     */
    private String title;

    /**
     * 详细描述
     */
    private String description;

    /**
     * 分类 售后/咨询/投诉/建议
     */
    private String category;

    /**
     * 解决时间
     */
    private LocalDateTime resolvedAt;

    /**
     * 关闭时间
     */
    private LocalDateTime closedAt;
    
    @TableLogic
    private Integer isDeleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(fill = FieldFill.INSERT)
    private Long createdBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updatedBy;
}