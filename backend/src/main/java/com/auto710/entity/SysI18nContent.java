package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 内容多语言字段（sys_i18n_content）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("sys_i18n_content")
public class SysI18nContent implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 业务类型 service/article/menu/banner/config */
    private String bizType;

    /** 业务主键 */
    private Long bizId;

    /** 语言编码 */
    private String langCode;

    /** 字段名 */
    private String fieldKey;

    /** 该语言下的字段内容 */
    private String fieldValue;

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
