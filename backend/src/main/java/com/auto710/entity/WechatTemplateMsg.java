package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 模板消息配置（wechat_template_msg）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("wechat_template_msg")
public class WechatTemplateMsg implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 消息类型 预约确认/接车/施工进度/完工/结算/存胎到期/退款 */
    private String msgType;

    /** 微信模板ID */
    private String templateId;

    /** 模板标题 */
    private String title;

    /** 内容模板(变量占位) */
    private String contentTemplate;

    /** 状态 1启用 0禁用 */
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
