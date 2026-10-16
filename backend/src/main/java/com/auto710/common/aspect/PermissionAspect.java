package com.auto710.common.aspect;

import com.auto710.common.annotation.RequiresPermission;
import com.auto710.common.api.ResultCode;
import com.auto710.common.context.LoginUser;
import com.auto710.common.context.UserContext;
import com.auto710.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * RBAC 权限校验切面
 */
@Slf4j
@Aspect
@Component
public class PermissionAspect {

    @Around("@annotation(requiresPermission)")
    public Object around(ProceedingJoinPoint point, RequiresPermission requiresPermission) throws Throwable {
        check(requiresPermission);
        return point.proceed();
    }

    private void check(RequiresPermission permission) {
        LoginUser user = UserContext.require();
        if (user.isSuperAdmin()) {
            return;
        }
        String[] roles = permission.roles();
        if (roles.length > 0 && user.getRoles() != null
                && Arrays.stream(roles).anyMatch(user.getRoles()::contains)) {
            return;
        }
        String[] perms = permission.value();
        if (perms.length > 0 && user.getPermissions() != null
                && Arrays.stream(perms).allMatch(user.getPermissions()::contains)) {
            return;
        }
        if (roles.length == 0 && perms.length == 0) {
            return;
        }
        throw new BusinessException(ResultCode.FORBIDDEN);
    }
}
