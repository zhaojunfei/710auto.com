package com.auto710.controller.admin;

import com.auto710.biz.AppointmentBizService;
import com.auto710.common.annotation.AuditLog;
import com.auto710.common.api.PageResult;
import com.auto710.common.api.Result;
import com.auto710.common.exception.BusinessException;
import com.auto710.common.util.EncryptUtil;
import com.auto710.common.web.BaseController;
import com.auto710.entity.Appointment;
import com.auto710.entity.WorkOrder;
import com.auto710.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * 【后台】预约管理
 * <p>提供预约单的完整维护（分页/详情/新增/修改/删除），以及确认、取消、到店接车、剩余产能。
 * 车牌号与联系电话出参统一解密；写入路径的单号生成与加密由 AppointmentServiceImpl 钩子保证。</p>
 * <p>注意：基础路径为复数 /api/admin/appointments，避免与生成器产出的
 * /api/admin/appointment 通用 CRUD 发生映射冲突。</p>
 */
@Tag(name = "预约管理")
@RestController("adminAppointmentController")
@RequestMapping("/api/admin/appointments")
public class AdminAppointmentController extends BaseController<AppointmentService, Appointment> {

    private final AppointmentBizService bizService;

    public AdminAppointmentController(AppointmentService appointmentService, AppointmentBizService bizService) {
        super(appointmentService);
        this.bizService = bizService;
    }

    /* ==================== 维护 CRUD（出参解密） ==================== */

    @Override
    @Operation(summary = "分页查询")
    @GetMapping("/page")
    public Result<PageResult<Appointment>> page(@RequestParam Map<String, Object> params) {
        Result<PageResult<Appointment>> result = super.page(params);
        result.getData().getRecords().forEach(this::decrypt);
        return result;
    }

    @Override
    @Operation(summary = "详情")
    @GetMapping("/{id}")
    public Result<Appointment> detail(@PathVariable Long id) {
        Result<Appointment> result = super.detail(id);
        decrypt(result.getData());
        return result;
    }

    /** 新增前校验：必须选择到店时间 */
    @Override
    protected void beforeSave(Appointment entity) {
        if (entity.getAppointTime() == null) {
            throw new BusinessException("请选择到店时间");
        }
    }

    private void decrypt(Appointment appointment) {
        if (appointment == null) {
            return;
        }
        appointment.setPlateNo(EncryptUtil.decrypt(appointment.getPlateNo()));
        appointment.setContactPhone(EncryptUtil.decrypt(appointment.getContactPhone()));
    }

    /* ==================== 流转动作 ==================== */

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
        LocalDate target = (date == null || date.isBlank()) ? LocalDate.now() : LocalDate.parse(date);
        return Result.ok(bizService.remainCapacity(storeId, target));
    }
}
