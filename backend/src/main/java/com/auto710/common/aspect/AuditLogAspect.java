package com.auto710.common.aspect;

import cn.hutool.core.util.StrUtil;
import com.auto710.common.annotation.AuditLog;
import com.auto710.common.context.LoginUser;
import com.auto710.common.context.UserContext;
import com.auto710.common.util.IpUtil;
import com.auto710.entity.SysAuditLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 操作审计日志切面：登录、工单修改、派单、价格修改、导出、退款等全部落库
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditLogRecorder auditLogRecorder;
    private final ObjectMapper objectMapper;

    @Around("@annotation(auditLog)")
    public Object around(ProceedingJoinPoint point, AuditLog auditLog) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = null;
        Throwable error = null;
        try {
            result = point.proceed();
            return result;
        } catch (Throwable e) {
            error = e;
            throw e;
        } finally {
            long cost = System.currentTimeMillis() - start;
            try {
                saveLog(point, auditLog, result, error, cost);
            } catch (Exception e) {
                log.warn("审计日志记录失败: {}", e.getMessage());
            }
        }
    }

    private void saveLog(ProceedingJoinPoint point, AuditLog auditLog, Object result, Throwable error, long cost) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes == null ? null : attributes.getRequest();

        SysAuditLog entity = new SysAuditLog();
        LoginUser user = UserContext.get();
        if (user != null) {
            entity.setUserId(user.getUserId());
            entity.setUsername(user.getUsername());
        }
        entity.setModule(auditLog.module());
        entity.setOperation(auditLog.operation());
        entity.setMethod(point.getSignature().toLongString());
        if (request != null) {
            entity.setRequestUrl(request.getRequestURI());
            entity.setRequestMethod(request.getMethod());
            entity.setIp(IpUtil.getIp(request));
            entity.setUserAgent(StrUtil.sub(request.getHeader("User-Agent"), 0, 500));
        }
        if (auditLog.saveParam()) {
            entity.setRequestParam(StrUtil.sub(toJson(point.getArgs()), 0, 3000));
        }
        if (auditLog.saveResult() && result != null) {
            entity.setResponseResult(StrUtil.sub(toJson(result), 0, 3000));
        }
        entity.setCostTime(cost);
        entity.setStatus(error == null ? 1 : 0);
        if (error != null) {
            entity.setErrorMsg(StrUtil.sub(error.getMessage(), 0, 2000));
        }
        entity.setCreateTime(LocalDateTime.now());
        auditLogRecorder.save(entity);
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            if (obj instanceof Object[] arr) {
                return Arrays.toString(Arrays.stream(arr)
                        .map(o -> o instanceof MultipartFile ? "file" : String.valueOf(o))
                        .toArray());
            }
            return String.valueOf(obj);
        }
    }
}
