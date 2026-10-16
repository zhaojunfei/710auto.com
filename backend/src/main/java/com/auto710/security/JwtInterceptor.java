package com.auto710.security;

import cn.hutool.core.util.StrUtil;
import com.auto710.common.api.ResultCode;
import com.auto710.common.context.LoginUser;
import com.auto710.common.context.UserContext;
import com.auto710.common.exception.BusinessException;
import com.auto710.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 登录拦截器
 */
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    public static final String HEADER = "Authorization";
    public static final String PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }
        String token = resolveToken(request);
        if (StrUtil.isBlank(token)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        LoginUser user = jwtUtil.parse(token);
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        UserContext.set(user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader(HEADER);
        if (StrUtil.isNotBlank(bearer) && bearer.startsWith(PREFIX)) {
            return bearer.substring(PREFIX.length());
        }
        if (StrUtil.isNotBlank(bearer)) {
            return bearer;
        }
        return request.getParameter("token");
    }
}
