package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 会员（member）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("member")
public class Member implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 微信UnionID */
    private String unionid;

    /** 微信公众号OpenID */
    private String openid;

    /** 小程序OpenID */
    private String miniappOpenid;

    /** H5 OpenID */
    private String h5Openid;

    /** 昵称 */
    private String nickname;

    /** 头像 */
    private String avatar;

    /** 真实姓名 */
    private String realName;

    /** 手机号(加密) */
    private String phone;

    /** 性别 0未知 1男 2女 */
    private Integer gender;

    /** 生日 */
    private String birthday;

    /** 会员等级ID */
    private Long levelId;

    /** 积分 */
    private Integer points;

    /** 储值余额 */
    private BigDecimal balance;

    /** 注册来源 mp/miniapp/h5 */
    private String source;

    /** 状态 1正常 0禁用 */
    private Integer status;

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
