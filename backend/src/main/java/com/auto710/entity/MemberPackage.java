package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 会员套餐（member_package）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("member_package")
public class MemberPackage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 会员ID */
    private Long memberId;

    /** 套餐ID */
    private Long packageId;

    /** 套餐名称 */
    private String packageName;

    /** 总次数 */
    private Integer totalTimes;

    /** 已用次数 */
    private Integer usedTimes;

    /** 剩余次数 */
    private Integer remainTimes;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 购买时间 */
    private LocalDateTime buyTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 到期时间 */
    private LocalDateTime expireTime;

    /** 状态 1有效 0失效 */
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
