package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.WorkOrderLog;
import com.auto710.service.WorkOrderLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】工单状态流转日志 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "工单状态流转日志管理")
@RestController
@RequestMapping("/api/admin/work-order-log")
public class WorkOrderLogController extends BaseController<WorkOrderLogService, WorkOrderLog> {

    public WorkOrderLogController(WorkOrderLogService service) {
        super(service);
    }
}
