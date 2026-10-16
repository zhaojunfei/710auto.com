package com.auto710.common.annotation;

import java.lang.annotation.*;

/**
 * 操作审计日志
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditLog {

    /** 模块 */
    String module() default "";

    /** 操作类型 */
    String operation() default "";

    /** 是否记录请求参数 */
    boolean saveParam() default true;

    /** 是否记录返回结果 */
    boolean saveResult() default false;
}
