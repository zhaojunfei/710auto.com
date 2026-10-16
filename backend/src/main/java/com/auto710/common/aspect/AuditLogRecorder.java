package com.auto710.common.aspect;

import com.auto710.entity.SysAuditLog;
import com.auto710.mapper.SysAuditLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 审计日志异步落库器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuditLogRecorder {

    private final SysAuditLogMapper auditLogMapper;

    @Async("auditExecutor")
    public void save(SysAuditLog auditLog) {
        try {
            auditLogMapper.insert(auditLog);
        } catch (Exception e) {
            log.warn("审计日志异步写入失败: {}", e.getMessage());
        }
    }
}
