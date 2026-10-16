package com.auto710.service.impl;

import com.auto710.entity.SysAuditLog;
import com.auto710.mapper.SysAuditLogMapper;
import com.auto710.service.SysAuditLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 操作审计日志 Service 实现
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Service
public class SysAuditLogServiceImpl extends ServiceImpl<SysAuditLogMapper, SysAuditLog> implements SysAuditLogService {
}
