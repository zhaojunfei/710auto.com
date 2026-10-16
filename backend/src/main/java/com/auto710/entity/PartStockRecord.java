package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 配件出入库记录（part_stock_record）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("part_stock_record")
public class PartStockRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 配件ID */
    private Long partId;

    /** 配件名称 */
    private String partName;

    /** 类型 入库/出库/盘点/退货 */
    private String recordType;

    /** 数量 */
    private Integer quantity;

    /** 变动前库存 */
    private Integer beforeStock;

    /** 变动后库存 */
    private Integer afterStock;

    /** 单价 */
    private BigDecimal unitPrice;

    /** 金额 */
    private BigDecimal amount;

    /** 关联工单ID */
    private Long workOrderId;

    /** 关联单号 */
    private String orderNo;

    /** 操作人ID */
    private Long operatorId;

    /** 操作人 */
    private String operatorName;

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
