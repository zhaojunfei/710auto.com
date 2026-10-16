package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 会员车辆（member_vehicle）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("member_vehicle")
public class MemberVehicle implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 会员ID */
    private Long memberId;

    /** 车牌号(加密) */
    private String plateNo;

    /** 车牌颜色 */
    private String plateColor;

    /** 品牌 */
    private String brand;

    /** 车型 */
    private String model;

    /** 车架号VIN(加密) */
    private String vin;

    /** 发动机号 */
    private String engineNo;

    /** 当前里程(公里) */
    private Integer mileage;

    /** 购车日期 */
    private String buyDate;

    /** 是否默认 1是 0否 */
    private Integer isDefault;

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
