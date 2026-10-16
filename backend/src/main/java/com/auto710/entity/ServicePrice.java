package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 价格配置（service_price）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("service_price")
public class ServicePrice implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 服务项目ID */
    private Long itemId;

    /** 价格名称 */
    private String priceName;

    /** 类型 基础/车型/时段 */
    private String priceType;

    /** 价格 */
    private BigDecimal price;

    /** 说明 */
    private String description;

    /** 状态 1启用 0禁用 */
    private Integer status;

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
