package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 每日经营统计（stat_daily）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("stat_daily")
public class StatDaily implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 统计日期 yyyy-MM-dd */
    private String statDate;

    /** 访问人数UV */
    private Integer uv;

    /** 访问量PV */
    private Integer pv;

    /** 预约量 */
    private Integer appointmentCount;

    /** 工单量 */
    private Integer orderCount;

    /** 营收 */
    private BigDecimal revenue;

    /** 毛利 */
    private BigDecimal grossProfit;

    /** 存胎量 */
    private Integer tireCount;

    /** 新增会员 */
    private Integer memberCount;

    /** 储值充值额 */
    private BigDecimal rechargeAmount;

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
