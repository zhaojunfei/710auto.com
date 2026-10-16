package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.WorkOrderCheck;
import com.auto710.service.WorkOrderCheckService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】接车检查/报价 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "接车检查/报价管理")
@RestController
@RequestMapping("/api/admin/work-order-check")
public class WorkOrderCheckController extends BaseController<WorkOrderCheckService, WorkOrderCheck> {

    public WorkOrderCheckController(WorkOrderCheckService service) {
        super(service);
    }
}
