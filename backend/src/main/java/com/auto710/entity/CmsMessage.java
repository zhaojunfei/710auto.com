package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 在线留言（cms_message）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("cms_message")
public class CmsMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 会员ID */
    private Long memberId;

    /** 姓名 */
    private String name;

    /** 手机号(加密) */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 留言内容 */
    private String content;

    /** 来源 www/miniapp/h5 */
    private String source;

    /** 状态 0未处理 1已处理 */
    private Integer status;

    /** 回复内容 */
    private String reply;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 回复时间 */
    private LocalDateTime replyTime;

    /** 提交IP */
    private String ip;

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
