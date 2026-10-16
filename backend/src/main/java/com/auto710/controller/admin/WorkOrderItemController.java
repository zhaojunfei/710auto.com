package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.WorkOrderItem;
import com.auto710.service.WorkOrderItemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】工单明细 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "工单明细管理")
@RestController
@RequestMapping("/api/admin/work-order-item")
public class WorkOrderItemController extends BaseController<WorkOrderItemService, WorkOrderItem> {

    public WorkOrderItemController(WorkOrderItemService service) {
        super(service);
    }
}
