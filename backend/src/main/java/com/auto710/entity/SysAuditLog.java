package com.auto710.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;

/**
 * 操作审计日志（sys_audit_log）
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Data
@TableName("sys_audit_log")
public class SysAuditLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 操作人ID */
    private Long userId;

    /** 操作人账号 */
    private String username;

    /** 模块 */
    private String module;

    /** 操作类型 */
    private String operation;

    /** 请求方法 */
    private String method;

    /** 请求地址 */
    private String requestUrl;

    /** 请求方式 */
    private String requestMethod;

    /** 请求参数 */
    private String requestParam;

    /** 返回结果 */
    private String responseResult;

    /** 操作IP */
    private String ip;

    /** 浏览器UA */
    private String userAgent;

    /** 耗时(ms) */
    private Long costTime;

    /** 状态 1成功 0失败 */
    private Integer status;

    /** 异常信息 */
    private String errorMsg;

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
