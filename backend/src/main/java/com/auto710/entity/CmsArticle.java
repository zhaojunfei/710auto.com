package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 养护知识文章（cms_article）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("cms_article")
public class CmsArticle implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 分类ID */
    private Long categoryId;

    /** 标题 */
    private String title;

    /** 标题英文 */
    private String titleEn;

    /** 封面图 */
    private String cover;

    /** 摘要 */
    private String summary;

    /** 摘要英文 */
    private String summaryEn;

    /** 正文(富文本) */
    private String content;

    /** 正文英文(富文本) */
    private String contentEn;

    /** 作者 */
    private String author;

    /** 来源 */
    private String source;

    /** SEO标题 */
    private String seoTitle;

    /** SEO关键词 */
    private String seoKeywords;

    /** SEO描述 */
    private String seoDescription;

    /** 标签 */
    private String tags;

    /** 浏览量 */
    private Integer viewCount;

    /** 是否置顶 1是 0否 */
    private Integer isTop;

    /** 状态 1发布 0草稿 */
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 发布时间 */
    private LocalDateTime publishTime;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除 0否 1是 */
    @TableLogic
    @TableField(select = false)
    private Integer deleted;
}
