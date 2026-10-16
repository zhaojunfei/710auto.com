package com.auto710.common.aspect;

import cn.hutool.core.util.StrUtil;
import com.auto710.common.annotation.RateLimit;
import com.auto710.common.api.ResultCode;
import com.auto710.common.context.UserContext;
import com.auto710.common.exception.BusinessException;
import com.auto710.common.util.IpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.TimeUnit;

/**
 * 接口限流切面（Redis 计数器，固定窗口）
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private static final String PREFIX = "auto710:rate:";

    private final StringRedisTemplate redisTemplate;

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint point, RateLimit rateLimit) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String uri = attributes == null ? point.getSignature().toShortString() : attributes.getRequest().getRequestURI();

        String identity;
        switch (rateLimit.type()) {
            case "USER" -> identity = String.valueOf(UserContext.userId());
            case "GLOBAL" -> identity = "global";
            default -> identity = IpUtil.getIp();
        }

        String key = PREFIX + (StrUtil.isNotBlank(rateLimit.key()) ? rateLimit.key() : uri) + ":" + identity;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1L) {
            redisTemplate.expire(key, rateLimit.time(), TimeUnit.SECONDS);
        }
        if (count != null && count > rateLimit.count()) {
            throw new BusinessException(ResultCode.RATE_LIMIT.getCode(), rateLimit.message());
        }
        return point.proceed();
    }
}
