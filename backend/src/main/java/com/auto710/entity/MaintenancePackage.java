package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 养护套餐（maintenance_package）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("maintenance_package")
public class MaintenancePackage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 套餐名称 */
    private String packageName;

    /** 套餐英文名称 */
    private String packageNameEn;

    /** 封面 */
    private String cover;

    /** 套餐说明 */
    private String description;

    /** 套餐价 */
    private BigDecimal price;

    /** 原价 */
    private BigDecimal originalPrice;

    /** 包含次数 */
    private Integer times;

    /** 有效天数 */
    private Integer validDays;

    /** 包含项目(JSON) */
    private String items;

    /** 状态 1上架 0下架 */
    private Integer status;

    /** 排序 */
    private Integer sort;

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
