package io.stageclear.common.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("customer_agent")   
public class CustomerAgent implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 工号 A + 5位自增短号
     */
    private String agentNo;

    /**
     * 关联 sys_user.id
     */
    private Long userId;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 花名/昵称
     */
    private String nickName;

    /**
     * 状态 ONLINE/BUSY/OFFLINE/AWAY
     */
    private String status;

    /**
     * 最大并发会话数
     */
    private Integer maxSessions;

    /**
     * 当前会话数
     */
    private Integer currentLoad;

    /**
     * 级别 JUNIOR/SENIOR/EXPERT
     */
    private String level;

    /**
     * 所属组
     */
    private String team;

    /**
     * 头像 URL
     */
    private String avatar;

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