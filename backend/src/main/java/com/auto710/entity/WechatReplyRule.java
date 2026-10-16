package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 公众号回复规则（wechat_reply_rule）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("wechat_reply_rule")
public class WechatReplyRule implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 类型 关注/关键词/默认 */
    private String ruleType;

    /** 关键词 */
    private String keyword;

    /** 匹配方式 全匹配/半匹配 */
    private String matchType;

    /** 回复类型 文本/图文/图片 */
    private String replyType;

    /** 回复内容 */
    private String content;

    /** 关联素材ID */
    private Long materialId;

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
