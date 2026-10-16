package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 公众号粉丝（wechat_fans）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("wechat_fans")
public class WechatFans implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** OpenID */
    private String openid;

    /** UnionID */
    private String unionid;

    /** 昵称 */
    private String nickname;

    /** 头像 */
    private String avatar;

    /** 性别 */
    private Integer gender;

    /** 是否关注 1是 0否 */
    private Integer subscribe;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 关注时间 */
    private LocalDateTime subscribeTime;

    /** 标签 */
    private String tags;

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
