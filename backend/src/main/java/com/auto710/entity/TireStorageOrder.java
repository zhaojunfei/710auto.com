package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 存胎寄存单（tire_storage_order）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("tire_storage_order")
public class TireStorageOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 寄存单号 */
    private String storageNo;

    /** 会员ID */
    private Long memberId;

    /** 门店ID */
    private Long storeId;

    /** 车辆ID */
    private Long vehicleId;

    /** 车牌号(加密) */
    private String plateNo;

    /** 轮胎数量 */
    private Integer tireCount;

    /** 轮胎类型 冬胎/夏胎/四季胎 */
    private String tireType;

    /** 轮胎品牌 */
    private String tireBrand;

    /** 轮胎规格 */
    private String tireSize;

    /** 寄存费 */
    private BigDecimal storageFee;

    /** 超期保管费 */
    private BigDecimal overdueFee;

    /** 寄存货位号 */
    private String positionNo;

    /** 寄存开始日期 */
    private String startDate;

    /** 预计取胎日期 */
    private String endDate;

    /** 实际取胎日期 */
    private String realOutDate;

    /** 取胎提醒偏好 */
    private String remindPreference;

    /** 状态 已寄存/寄存中/待取胎/已取胎/超期 */
    private String status;

    /** 备注 */
    private String remark;

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
