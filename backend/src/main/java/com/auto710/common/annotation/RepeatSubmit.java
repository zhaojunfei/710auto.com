package com.auto710.common.annotation;

import java.lang.annotation.*;

/**
 * 接口防重复提交
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RepeatSubmit {

    /** 幂等 Key，默认取请求 URI */
    String key() default "";

    /** 锁定时间(秒) */
    int expire() default 5;

    /** 提示信息 */
    String message() default "请勿重复提交";
}
