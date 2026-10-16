package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.SysAuditLog;
import com.auto710.service.SysAuditLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】操作审计日志 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "操作审计日志管理")
@RestController
@RequestMapping("/api/admin/sys-audit-log")
public class SysAuditLogController extends BaseController<SysAuditLogService, SysAuditLog> {

    public SysAuditLogController(SysAuditLogService service) {
        super(service);
    }
}
