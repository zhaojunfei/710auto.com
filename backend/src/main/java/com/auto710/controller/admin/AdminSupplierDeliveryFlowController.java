package com.auto710.controller.admin;

import com.auto710.biz.SupplierBizService;
import com.auto710.common.annotation.AuditLog;
import com.auto710.common.api.Result;
import com.auto710.common.context.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * 【后台】供应商送货单确认（入库 / 拒绝）
 * <p>与生成的 SupplierDeliveryController 共用 /api/admin/supplier-delivery 前缀，仅补充流转动作。</p>
 */
@Tag(name = "供应商送货单流转")
@RestController("adminSupplierDeliveryFlowController")
@RequestMapping("/api/admin/supplier-delivery")
public class AdminSupplierDeliveryFlowController {

    private final SupplierBizService supplierBizService;

    public AdminSupplierDeliveryFlowController(SupplierBizService supplierBizService) {
        this.supplierBizService = supplierBizService;
    }

    @Operation(summary = "确认送货单（入库/拒绝）")
    @PostMapping("/{id}/confirm")
    @AuditLog(module = "供应商", operation = "确认送货单")
    public Result<Boolean> confirm(@PathVariable Long id,
                                   @RequestParam boolean pass,
                                   @RequestParam(required = false) String remark) {
        supplierBizService.confirm(id, UserContext.userId(), pass, remark);
        return Result.ok(true);
    }
}
