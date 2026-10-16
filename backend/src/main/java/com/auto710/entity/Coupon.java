package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 优惠券（coupon）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("coupon")
public class Coupon implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 优惠券名称 */
    private String couponName;

    /** 类型 满减/折扣/工时抵扣/洗车券 */
    private String couponType;

    /** 面额 */
    private BigDecimal faceValue;

    /** 使用门槛金额 */
    private BigDecimal minAmount;

    /** 折扣率 */
    private BigDecimal discount;

    /** 发行总量 0不限 */
    private Integer totalCount;

    /** 已领取数量 */
    private Integer receivedCount;

    /** 已使用数量 */
    private Integer usedCount;

    /** 有效期类型 固定/领取后N天 */
    private String validType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 生效时间 */
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 失效时间 */
    private LocalDateTime endTime;

    /** 领取后有效天数 */
    private Integer validDays;

    /** 适用范围 */
    private String scope;

    /** 使用说明 */
    private String description;

    /** 状态 1启用 0停用 */
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
