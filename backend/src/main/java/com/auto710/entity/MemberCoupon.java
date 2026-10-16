package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 会员优惠券（member_coupon）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("member_coupon")
public class MemberCoupon implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 会员ID */
    private Long memberId;

    /** 优惠券ID */
    private Long couponId;

    /** 券码 */
    private String couponCode;

    /** 状态 未使用/已使用/已过期 */
    private String status;

    /** 使用订单号 */
    private String bizNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 领取时间 */
    private LocalDateTime receiveTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 使用时间 */
    private LocalDateTime useTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 过期时间 */
    private LocalDateTime expireTime;

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
