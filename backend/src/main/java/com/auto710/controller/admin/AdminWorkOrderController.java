package com.auto710.controller.admin;

import com.auto710.biz.WorkOrderBizService;
import com.auto710.common.annotation.AuditLog;
import com.auto710.common.api.Result;
import com.auto710.common.context.UserContext;
import com.auto710.entity.*;
import com.auto710.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 【后台】预约 & 工单管理 + 工单看板
 */
@Tag(name = "工单管理")
@RestController
@RequestMapping("/api/admin/work-order")
public class AdminWorkOrderController {

    private final WorkOrderBizService bizService;
    private final WorkOrderItemService itemService;
    private final WorkOrderCheckService checkService;
    private final WorkOrderLogService logService;
    private final RefundRecordService refundService;

    public AdminWorkOrderController(WorkOrderBizService bizService,
                                    WorkOrderItemService itemService,
                                    WorkOrderCheckService checkService,
                                    WorkOrderLogService logService,
                                    RefundRecordService refundService) {
        this.bizService = bizService;
        this.itemService = itemService;
        this.checkService = checkService;
        this.logService = logService;
        this.refundService = refundService;
    }

    @Operation(summary = "可视化看板（按状态分列）")
    @GetMapping("/kanban")
    public Result<Map<String, List<WorkOrder>>> kanban() {
        return Result.ok(bizService.kanban());
    }

    @Operation(summary = "手动录入线下工单")
    @PostMapping("/manual")
    @AuditLog(module = "工单", operation = "手动开单")
    public Result<WorkOrder> manual(@RequestBody WorkOrder order) {
        return Result.ok(bizService.createManual(order));
    }

    @Operation(summary = "接车开单：录入检查项/报价项")
    @PostMapping("/{id}/checks")
    @AuditLog(module = "工单", operation = "接车开单")
    public Result<Boolean> addChecks(@PathVariable Long id, @RequestBody List<WorkOrderCheck> checks) {
        bizService.addChecks(id, checks);
        return Result.ok(true);
    }

    @Operation(summary = "读取检查项/报价项")
    @GetMapping("/{id}/checks")
    public Result<List<WorkOrderCheck>> checks(@PathVariable Long id) {
        return Result.ok(checkService.list(new LambdaQueryWrapper<WorkOrderCheck>()
                .eq(WorkOrderCheck::getWorkOrderId, id)));
    }

    @Operation(summary = "接车完成")
    @PostMapping("/{id}/receive")
    @AuditLog(module = "工单", operation = "接车")
    public Result<Boolean> receive(@PathVariable Long id) {
        return Result.ok(bizService.receive(id));
    }

    @Operation(summary = "开工")
    @PostMapping("/{id}/start")
    @AuditLog(module = "工单", operation = "开工")
    public Result<Boolean> start(@PathVariable Long id) {
        return Result.ok(bizService.start(id));
    }

    @Operation(summary = "派工")
    @PostMapping("/{id}/dispatch")
    @AuditLog(module = "工单", operation = "派工")
    public Result<Boolean> dispatch(@PathVariable Long id, @RequestParam Long technicianId) {
        bizService.dispatch(id, technicianId);
        return Result.ok(true);
    }

    @Operation(summary = "质检")
    @PostMapping("/{id}/check")
    @AuditLog(module = "工单", operation = "质检")
    public Result<Boolean> qualityCheck(@PathVariable Long id,
                                        @RequestParam boolean pass,
                                        @RequestParam(required = false) String remark) {
        return Result.ok(bizService.qualityCheck(id, pass, remark));
    }

    @Operation(summary = "结算")
    @PostMapping("/{id}/settle")
    @AuditLog(module = "工单", operation = "结算")
    public Result<WorkOrder> settle(@PathVariable Long id,
                                    @RequestParam String payType,
                                    @RequestParam(required = false) BigDecimal useBalance,
                                    @RequestParam(required = false) Integer usePoints) {
        return Result.ok(bizService.settle(id, payType, useBalance, usePoints));
    }

    @Operation(summary = "用料登记")
    @PostMapping("/{id}/items")
    @AuditLog(module = "工单", operation = "用料登记")
    public Result<Boolean> addItem(@PathVariable Long id,
                                   @RequestBody WorkOrderItem item,
                                   @RequestParam(defaultValue = "true") boolean outStock) {
        bizService.addItem(id, item, outStock);
        return Result.ok(true);
    }

    @Operation(summary = "工单明细")
    @GetMapping("/{id}/items")
    public Result<List<WorkOrderItem>> items(@PathVariable Long id) {
        return Result.ok(itemService.list(new LambdaQueryWrapper<WorkOrderItem>()
                .eq(WorkOrderItem::getWorkOrderId, id)));
    }

    @Operation(summary = "状态流转日志")
    @GetMapping("/{id}/logs")
    public Result<List<WorkOrderLog>> logs(@PathVariable Long id) {
        return Result.ok(logService.list(new LambdaQueryWrapper<WorkOrderLog>()
                .eq(WorkOrderLog::getWorkOrderId, id)
                .orderByDesc(WorkOrderLog::getCreateTime)));
    }

    @Operation(summary = "加急/取消加急")
    @PostMapping("/{id}/urgent")
    public Result<Boolean> urgent(@PathVariable Long id, @RequestParam boolean urgent) {
        return Result.ok(bizService.setUrgent(id, urgent));
    }

    @Operation(summary = "后台发起退款审核通过/拒绝")
    @PostMapping("/refund/{refundId}/audit")
    @AuditLog(module = "工单", operation = "退款审核")
    public Result<Boolean> auditRefund(@PathVariable Long refundId,
                                       @RequestParam boolean pass,
                                       @RequestParam(required = false) String remark) {
        return Result.ok(bizService.auditRefund(refundId, pass, remark, UserContext.userId()));
    }

    @Operation(summary = "退款单列表")
    @GetMapping("/refund/list")
    public Result<List<RefundRecord>> refundList() {
        return Result.ok(refundService.list(new LambdaQueryWrapper<RefundRecord>()
                .orderByDesc(RefundRecord::getCreateTime)));
    }
}
