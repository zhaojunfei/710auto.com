package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 寄存轮胎明细（tire_storage_item）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("tire_storage_item")
public class TireStorageItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 寄存单ID */
    private Long storageOrderId;

    /** 位置 左前/右前/左后/右后/备胎 */
    private String tirePosition;

    /** 品牌 */
    private String tireBrand;

    /** 规格 */
    private String tireSize;

    /** 花纹深度 */
    private String treadDepth;

    /** 照片(JSON) */
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
