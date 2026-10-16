package com.auto710.controller.admin;

import com.auto710.biz.AppointmentBizService;
import com.auto710.common.annotation.AuditLog;
import com.auto710.common.api.Result;
import com.auto710.entity.WorkOrder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 【后台】预约管理
 */
@Tag(name = "预约管理")
@RestController
@RequestMapping("/api/admin/appointment")
public class AdminAppointmentController {

    private final AppointmentBizService bizService;

    public AdminAppointmentController(AppointmentBizService bizService) {
        this.bizService = bizService;
    }

    @Operation(summary = "确认预约")
    @PostMapping("/{id}/confirm")
    @AuditLog(module = "预约", operation = "确认预约")
    public Result<Boolean> confirm(@PathVariable Long id) {
        return Result.ok(bizService.confirm(id));
    }

    @Operation(summary = "取消预约")
    @PostMapping("/{id}/cancel")
    @AuditLog(module = "预约", operation = "取消预约")
    public Result<Boolean> cancel(@PathVariable Long id, @RequestParam(required = false) String reason) {
        return Result.ok(bizService.cancel(id, reason));
    }

    @Operation(summary = "到店接车（预约转工单）")
    @PostMapping("/{id}/receive")
    @AuditLog(module = "预约", operation = "到店接车")
    public Result<WorkOrder> receive(@PathVariable Long id, @RequestBody WorkOrder workOrder) {
        return Result.ok(bizService.receive(id, workOrder));
    }

    @Operation(summary = "查询某日剩余产能")
    @GetMapping("/remain-capacity")
    public Result<Integer> remainCapacity(@RequestParam(required = false) Long storeId,
                                          @RequestParam(required = false) String date) {
        LocalDate target = date == null ? LocalDate.now() : LocalDate.parse(date);
        return Result.ok(bizService.remainCapacity(storeId, target));
    }
}
