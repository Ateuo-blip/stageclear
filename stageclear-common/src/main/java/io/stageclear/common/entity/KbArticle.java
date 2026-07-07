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
@TableName("kb_article")   
public class KbArticle implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 正文（支持 Markdown/HTML）
     */
    private String content;

    /**
     * 一级分类
     */
    private String category;

    /**
     * 二级分类
     */
    private String subCategory;

    /**
     * 搜索关键词，逗号分隔
     */
    private String keywords;

    /**
     * 状态 DRAFT/PUBLISHED/ARCHIVED
     */
    private String status;

    /**
     * 浏览数
     */
    private Integer viewCount;

    /**
     * 觉得有用次数
     */
    private Integer helpfulCount;

    /**
     * 觉得没用次数
     */
    private Integer unhelpfulCount;
    
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