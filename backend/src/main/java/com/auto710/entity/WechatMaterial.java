package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 公众号素材（wechat_material）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("wechat_material")
public class WechatMaterial implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 类型 图文/图片/视频 */
    private String materialType;

    /** 标题 */
    private String title;

    /** 微信媒体ID */
    private String mediaId;

    /** 素材地址 */
    private String url;

    /** 缩略图 */
    private String thumbUrl;

    /** 图文内容 */
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 过期时间 */
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
