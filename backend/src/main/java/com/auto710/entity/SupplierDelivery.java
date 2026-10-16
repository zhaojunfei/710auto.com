package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 供应商送货单（supplier_delivery）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("supplier_delivery")
public class SupplierDelivery implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 送货单号 */
    private String deliveryNo;

    /** 供应商ID */
    private Long supplierId;

    /** 供应商名称 */
    private String supplierName;

    /** 收货门店ID */
    private Long storeId;

    /** 明细条数 */
    private Integer itemCount;

    /** 合计金额 */
    private BigDecimal totalAmount;

    /** 预计到货日期 */
    private String expectDate;

    /** 状态 待确认/已入库/已拒绝 */
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 提交时间 */
    private LocalDateTime submitTime;

    /** 确认人ID */
    private Long confirmUserId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 确认时间 */
    private LocalDateTime confirmTime;

    /** 确认备注 */
    private String confirmRemark;

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
