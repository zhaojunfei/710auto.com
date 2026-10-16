package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 维修工单（work_order）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("work_order")
public class WorkOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 工单号 */
    private String orderNo;

    /** 关联预约单ID */
    private Long appointmentId;

    /** 会员ID */
    private Long memberId;

    /** 门店ID */
    private Long storeId;

    /** 车辆ID */
    private Long vehicleId;

    /** 车牌号(加密) */
    private String plateNo;

    /** 品牌车型 */
    private String brandModel;

    /** 进厂里程 */
    private Integer mileage;

    /** 联系人 */
    private String contactName;

    /** 联系电话(加密) */
    private String contactPhone;

    /** 故障描述/客户诉求 */
    private String faultDesc;

    /** 服务项目(JSON) */
    private String serviceItems;

    /** 状态 待接车/待派工/施工中/待质检/待结算/已离店/已评价 */
    private String status;

    /** 工单总额 */
    private BigDecimal totalAmount;

    /** 工时费合计 */
    private BigDecimal laborAmount;

    /** 配件费合计 */
    private BigDecimal partsAmount;

    /** 优惠金额 */
    private BigDecimal discountAmount;

    /** 实收金额 */
    private BigDecimal paidAmount;

    /** 支付方式 微信/储值/现金/混合 */
    private String payType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 支付时间 */
    private LocalDateTime payTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 接车时间 */
    private LocalDateTime receiveTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 开工时间 */
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 完工时间 */
    private LocalDateTime finishTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 交车时间 */
    private LocalDateTime deliverTime;

    /** 主责技师ID */
    private Long technicianId;

    /** 是否加急 1是 0否 */
    private Integer isUrgent;

    /** 评价状态 0未评 1已评 */
    private Integer evaluateStatus;

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
