package com.auto710.common.aspect;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.auto710.common.annotation.RepeatSubmit;
import com.auto710.common.api.ResultCode;
import com.auto710.common.context.UserContext;
import com.auto710.common.exception.BusinessException;
import com.auto710.common.util.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 接口防重复提交切面（Redis 分布式锁实现）
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RepeatSubmitAspect {

    private static final String PREFIX = "auto710:repeat:";

    private final StringRedisTemplate redisTemplate;

    @Around("@annotation(repeatSubmit)")
    public Object around(ProceedingJoinPoint point, RepeatSubmit repeatSubmit) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String uri = attributes == null ? "" : attributes.getRequest().getRequestURI();
        String methodName = ((MethodSignature) point.getSignature()).getMethod().getName();
        String custom = repeatSubmit.key();

        StringBuilder keyBuilder = new StringBuilder(PREFIX);
        keyBuilder.append(StrUtil.isNotBlank(custom) ? custom : methodName).append(":");
        Long userId = UserContext.userId();
        keyBuilder.append(userId != null ? userId : IpUtil.getIp());
        keyBuilder.append(":").append(DigestUtil.md5Hex(uri + "|" + argsHash(point)));

        String key = keyBuilder.toString();
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, "1", repeatSubmit.expire(), TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(success)) {
            throw new BusinessException(ResultCode.REPEAT_SUBMIT.getCode(), repeatSubmit.message());
        }
        try {
            return point.proceed();
        } catch (Throwable e) {
            // 业务失败时释放锁，允许重试
            redisTemplate.delete(key);
            throw e;
        }
    }

    private String argsHash(ProceedingJoinPoint point) {
        StringBuilder sb = new StringBuilder();
        Object[] args = point.getArgs();
        if (args != null) {
            for (Object arg : args) {
                if (arg == null) {
                    continue;
                }
                String name = arg.getClass().getName();
                if (name.startsWith("jakarta.servlet") || name.startsWith("org.springframework")) {
                    continue;
                }
                sb.append(arg);
            }
        }
        return sb.toString();
    }
}
