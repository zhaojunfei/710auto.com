package com.auto710.common.context;

import com.auto710.common.api.ResultCode;
import com.auto710.common.exception.BusinessException;

/**
 * 当前登录用户上下文（基于 ThreadLocal）
 */
public final class UserContext {

    private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(LoginUser user) {
        HOLDER.set(user);
    }

    public static LoginUser get() {
        return HOLDER.get();
    }

    public static LoginUser require() {
        LoginUser user = HOLDER.get();
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return user;
    }

    public static Long userId() {
        LoginUser user = HOLDER.get();
        return user == null ? null : user.getUserId();
    }

    public static String username() {
        LoginUser user = HOLDER.get();
        return user == null ? null : user.getUsername();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
