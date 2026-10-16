package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 配件耗材（part_info）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("part_info")
public class PartInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 配件编码 */
    private String partCode;

    /** 配件名称 */
    private String partName;

    /** 分类ID */
    private Long categoryId;

    /** 品牌 */
    private String brand;

    /** 规格型号 */
    private String spec;

    /** 单位 */
    private String unit;

    /** 成本价 */
    private BigDecimal purchasePrice;

    /** 售价 */
    private BigDecimal salePrice;

    /** 当前库存 */
    private Integer stock;

    /** 预警库存 */
    private Integer warnStock;

    /** 图片 */
    private String cover;

    /** 供应商 */
    private String supplier;

    /** 状态 1启用 0禁用 */
    private Integer status;

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
