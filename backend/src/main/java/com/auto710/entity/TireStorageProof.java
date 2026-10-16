package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 寄存凭证（tire_storage_proof）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("tire_storage_proof")
public class TireStorageProof implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 寄存单ID */
    private Long storageOrderId;

    /** 类型 入库/取胎 */
    private String proofType;

    /** 凭证图片(JSON) */
    private String images;

    /** 操作人ID */
    private Long operatorId;

    /** 操作人 */
    private String operatorName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 凭证时间 */
    private LocalDateTime proofTime;

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
