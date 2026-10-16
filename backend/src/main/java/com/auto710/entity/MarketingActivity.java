package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 营销活动（marketing_activity）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("marketing_activity")
public class MarketingActivity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 活动名称 */
    private String activityName;

    /** 类型 拼团/节日/充值赠/新人 */
    private String activityType;

    /** 活动封面 */
    private String cover;

    /** 活动说明 */
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 开始时间 */
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 结束时间 */
    private LocalDateTime endTime;

    /** 活动规则(JSON) */
    private String rules;

    /** 状态 1进行中 0已结束 */
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
