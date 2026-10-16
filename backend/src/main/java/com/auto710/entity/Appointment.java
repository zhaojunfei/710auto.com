package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 预约单（appointment）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("appointment")
public class Appointment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 预约单号 */
    private String appointmentNo;

    /** 会员ID */
    private Long memberId;

    /** 门店ID */
    private Long storeId;

    /** 服务项目ID */
    private Long itemId;

    /** 服务项目名称 */
    private String itemName;

    /** 车辆ID */
    private Long vehicleId;

    /** 车牌号(加密) */
    private String plateNo;

    /** 品牌车型 */
    private String brandModel;

    /** 联系人 */
    private String contactName;

    /** 联系电话(加密) */
    private String contactPhone;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 预约到店时间 */
    private LocalDateTime appointTime;

    /** 客户备注 */
    private String remark;

    /** 状态 待确认/已确认/待接车/已完成/已取消 */
    private String status;

    /** 来源 miniapp/h5/mp/admin */
    private String source;

    /** 取消原因 */
    private String cancelReason;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 确认时间 */
    private LocalDateTime confirmTime;

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
