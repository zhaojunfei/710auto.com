package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 寄存库存（tire_storage_stock）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("tire_storage_stock")
public class TireStorageStock implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 门店ID */
    private Long storeId;

    /** 货位号 */
    private String positionNo;

    /** 寄存单ID */
    private Long storageOrderId;

    /** 车牌号(加密) */
    private String plateNo;

    /** 轮胎数量 */
    private Integer tireCount;

    /** 状态 在库/已出库 */
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 入库时间 */
    private LocalDateTime inTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 出库时间 */
    private LocalDateTime outTime;

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
