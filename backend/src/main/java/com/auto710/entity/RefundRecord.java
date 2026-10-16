package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 退款记录（refund_record）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("refund_record")
public class RefundRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 退款单号 */
    private String refundNo;

    /** 工单ID */
    private Long workOrderId;

    /** 原订单号 */
    private String orderNo;

    /** 会员ID */
    private Long memberId;

    /** 退款金额 */
    private BigDecimal refundAmount;

    /** 退款原因 */
    private String reason;

    /** 凭证图片(JSON) */
    private String images;

    /** 状态 待审核/已通过/已拒绝/已退款 */
    private String status;

    /** 审核人ID */
    private Long auditUserId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 审核时间 */
    private LocalDateTime auditTime;

    /** 审核备注 */
    private String auditRemark;

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
