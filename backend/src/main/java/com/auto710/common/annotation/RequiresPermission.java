package com.auto710.common.annotation;

import java.lang.annotation.*;

/**
 * 权限校验（RBAC），标注在方法或类上
 * <p>示例：@RequiresPermission("service:item:add")</p>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermission {

    /** 需要的权限标识，多个为 AND */
    String[] value() default {};

    /** 需要的角色编码，多个为 OR */
    String[] roles() default {};
}
