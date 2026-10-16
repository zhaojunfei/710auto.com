package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 工位（work_station）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("work_station")
public class WorkStation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 门店ID */
    private Long storeId;

    /** 工位名称 */
    private String stationName;

    /** 工位类型 机修/保养/轮胎/美容 */
    private String stationType;

    /** 同时开工上限 */
    private Integer capacity;

    /** 状态 1启用 0禁用 */
    private Integer status;

    /** 排序 */
    private Integer sort;

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
