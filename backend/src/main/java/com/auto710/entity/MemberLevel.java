package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 会员等级（member_level）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("member_level")
public class MemberLevel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 等级名称 */
    private String levelName;

    /** 等级编码 */
    private String levelCode;

    /** 所需最低积分 */
    private Integer minPoints;

    /** 折扣率 如0.95 */
    private BigDecimal discount;

    /** 等级图标 */
    private String icon;

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
