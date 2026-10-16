package com.auto710.common.annotation;

import java.lang.annotation.*;

/**
 * 接口限流（防刷）
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /** 资源标识，默认取 URI */
    String key() default "";

    /** 时间窗口(秒) */
    int time() default 60;

    /** 窗口内最大请求次数 */
    int count() default 60;

    /** 限流维度 IP / USER / GLOBAL */
    String type() default "IP";

    String message() default "操作过于频繁，请稍后再试";
}
