package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 品牌故事（cms_brand_story）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("cms_brand_story")
public class CmsBrandStory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 标题 */
    private String title;

    /** 标题英文 */
    private String titleEn;

    /** 封面 */
    private String cover;

    /** 内容(富文本) */
    private String content;

    /** 英文内容(富文本) */
    private String contentEn;

    /** 图集(JSON) */
    private String images;

    /** 排序 */
    private Integer sort;

    /** 状态 1启用 0禁用 */
    private Integer status;

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
