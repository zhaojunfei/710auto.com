package com.auto710.common.api;

import lombok.Getter;

/**
 * 响应码
 */
@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    REPEAT_SUBMIT(429, "请勿重复提交"),
    RATE_LIMIT(430, "操作过于频繁，请稍后再试"),
    SENSITIVE_WORD(431, "内容包含敏感词"),
    CAPACITY_FULL(432, "工位产能已满，请选择其他时间"),
    STORAGE_ERROR(440, "存储服务异常"),
    BUSINESS_ERROR(600, "业务处理失败");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
