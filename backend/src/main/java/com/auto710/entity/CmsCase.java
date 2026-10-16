package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 施工案例（cms_case）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("cms_case")
public class CmsCase implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 案例名称 */
    private String caseName;

    /** 案例英文名称 */
    private String caseNameEn;

    /** 类型 维修/保养/美容/存胎 */
    private String caseType;

    /** 封面 */
    private String cover;

    /** 施工前图(JSON) */
    private String beforeImages;

    /** 施工后图(JSON) */
    private String afterImages;

    /** 案例描述 */
    private String content;

    /** 英文描述 */
    private String contentEn;

    /** 关联服务项目ID */
    private Long serviceItemId;

    /** 浏览量 */
    private Integer viewCount;

    /** 排序 */
    private Integer sort;

    /** 状态 1上架 0下架 */
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
