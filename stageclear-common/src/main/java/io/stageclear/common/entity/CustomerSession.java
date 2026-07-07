package io.stageclear.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("customer_session")
public class CustomerSession implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String sessionNo;
    private Long userId;
    private Long agentId;
    private String status;
    private String channel;
    private String source;
    /**
     * 优先级
     */
    private Integer priority;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    /**
     * 结束原因
     */
    private String endReason;
    /**
     * 满意度
     */
    private Integer rating;

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
