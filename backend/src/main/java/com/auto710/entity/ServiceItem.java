package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 服务项目（service_item）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("service_item")
public class ServiceItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 分类ID */
    private Long categoryId;

    /** 项目名称 */
    private String itemName;

    /** 项目英文名称 */
    private String itemNameEn;

    /** 项目编码 */
    private String itemCode;

    /** 封面图 */
    private String cover;

    /** 图集(JSON数组) */
    private String images;

    /** 项目详情(富文本) */
    private String description;

    /** 项目详情英文(富文本) */
    private String descriptionEn;

    /** 工时费 */
    private BigDecimal laborFee;

    /** 参考价格 */
    private BigDecimal price;

    /** 价格说明 */
    private String priceDesc;

    /** 所需时长(分钟) */
    private Integer duration;

    /** 是否支持上门 1是 0否 */
    private Integer supportHome;

    /** 是否可预约 1是 0否 */
    private Integer bookable;

    /** 所需工位类型 */
    private String stationType;

    /** 技师资质要求 */
    private String techRequire;

    /** 施工案例图(JSON数组) */
    private String caseImages;

    /** 审核状态 0待审 1通过 2驳回 */
    private Integer auditStatus;

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
