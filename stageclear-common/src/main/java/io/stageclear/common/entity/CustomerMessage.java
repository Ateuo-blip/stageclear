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
@TableName("customer_message")   
public class CustomerMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属会话 id
     */
    private Long sessionId;

    /**
     * 发送类型 USER/AGENT/AI/SYSTEM
     */
    private String senderType;

    /**
     * 发送者 id（系统消息可空）
     */
    private Long senderId;

    /**
     * 内容类型 TEXT/IMAGE/FILE/SYSTEM
     */
    private String contentType;

    /**
     * 消息内容（文本或 JSON）
     */
    private String content;

    /**
     * 发送时间（毫秒精度）
     */
    private LocalDateTime sendTime;

    /**
     * 是否已读 0未读 1已读
     */
    private Integer readFlag;
    
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