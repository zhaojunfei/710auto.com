package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 技师排班（technician_schedule）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("technician_schedule")
public class TechnicianSchedule implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 技师ID */
    private Long technicianId;

    /** 排班日期 yyyy-MM-dd */
    private String workDate;

    /** 班次 早班/中班/晚班/休息 */
    private String shift;

    /** 开始时间 */
    private String startTime;

    /** 结束时间 */
    private String endTime;

    /** 状态 1有效 0无效 */
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
