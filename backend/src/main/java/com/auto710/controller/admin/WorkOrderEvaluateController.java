package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.WorkOrderEvaluate;
import com.auto710.service.WorkOrderEvaluateService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】工单评价 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "工单评价管理")
@RestController
@RequestMapping("/api/admin/work-order-evaluate")
public class WorkOrderEvaluateController extends BaseController<WorkOrderEvaluateService, WorkOrderEvaluate> {

    public WorkOrderEvaluateController(WorkOrderEvaluateService service) {
        super(service);
    }
}
