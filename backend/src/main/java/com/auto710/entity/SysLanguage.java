package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 语言配置（sys_language）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("sys_language")
public class SysLanguage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 语言编码 zh-CN/en-US */
    private String langCode;

    /** 语言名称 */
    private String langName;

    /** 语言英文名称 */
    private String langNameEn;

    /** 国旗图标 */
    private String flagIcon;

    /** 是否默认 1是 0否 */
    private Integer isDefault;

    /** 是否启用 1是 0否 */
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
