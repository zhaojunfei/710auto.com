package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 接车检查/报价（work_order_check）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("work_order_check")
public class WorkOrderCheck implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 工单ID */
    private Long workOrderId;

    /** 类型 检查项/报价项 */
    private String checkType;

    /** 检查项名称 */
    private String checkName;

    /** 检查结果 */
    private String checkResult;

    /** 报价 */
    private BigDecimal quotePrice;

    /** 客户是否确认 1是 0否 */
    private Integer customerConfirm;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 确认时间 */
    private LocalDateTime confirmTime;

    /** 图片(JSON) */
    private String images;

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
