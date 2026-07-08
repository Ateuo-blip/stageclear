package io.stageclear.customer.vo;

import io.stageclear.common.entity.KbArticle;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 知识库文章响应 VO
 */
@Data
@NoArgsConstructor
public class KbArticleVO {

    private Long id;
    private String title;
    private String content;
    private String category;
    /**
     * 二级分类，如 退款 / 退货 / 换货
     */
    private String subCategory;
    /**
     * 搜索关键词，逗号分隔，用于 RAG 检索
     */
    private String keywords;
    /**
     * DRAFT（草稿） / PUBLISHED（已发布） / ARCHIVED（下架）
     */
    private String status;
    /**
     * 文章浏览次数
     */
    private Integer viewCount;
    /**
     * 用户标记"有用"的次数
     */
    private Integer helpfulCount;
    /**
     * 用户标记"没用"的次数
     */
    private Integer unhelpfulCount;
    private LocalDateTime createdAt;

    public static KbArticleVO from(KbArticle a) {
        if (a == null) return null;
        KbArticleVO v = new KbArticleVO();
        v.setId(a.getId());
        v.setTitle(a.getTitle());
        v.setContent(a.getContent());
        v.setCategory(a.getCategory());
        v.setSubCategory(a.getSubCategory());
        v.setKeywords(a.getKeywords());
        v.setStatus(a.getStatus());
        v.setViewCount(a.getViewCount());
        v.setHelpfulCount(a.getHelpfulCount());
        v.setUnhelpfulCount(a.getUnhelpfulCount());
        v.setCreatedAt(a.getCreatedAt());
        return v;
    }
}
