package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 工单评价（work_order_evaluate）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("work_order_evaluate")
public class WorkOrderEvaluate implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 工单ID */
    private Long workOrderId;

    /** 会员ID */
    private Long memberId;

    /** 综合评分 1-5 */
    private Integer score;

    /** 服务评分 */
    private Integer serviceScore;

    /** 技师评分 */
    private Integer techScore;

    /** 评价内容 */
    private String content;

    /** 评价图片(JSON) */
    private String images;

    /** 门店回复 */
    private String reply;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 回复时间 */
    private LocalDateTime replyTime;

    /** 状态 1显示 0隐藏 */
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
