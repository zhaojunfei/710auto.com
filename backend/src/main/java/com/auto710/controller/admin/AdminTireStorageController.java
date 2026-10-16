package com.auto710.controller.admin;

import com.auto710.biz.TireStorageBizService;
import com.auto710.common.annotation.AuditLog;
import com.auto710.common.api.Result;
import com.auto710.common.context.UserContext;
import com.auto710.common.web.BaseController;
import com.auto710.entity.TireStorageItem;
import com.auto710.entity.TireStorageOrder;
import com.auto710.service.TireStorageItemService;
import com.auto710.service.TireStorageOrderService;
import com.auto710.service.TireStorageProofService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 【后台】存胎寄存管理
 */
@Tag(name = "存胎寄存管理")
@RestController
@RequestMapping("/api/admin/tire-storage")
public class AdminTireStorageController extends BaseController<TireStorageOrderService, TireStorageOrder> {

    private final TireStorageBizService bizService;
    private final TireStorageItemService itemService;
    private final TireStorageProofService proofService;

    public AdminTireStorageController(TireStorageOrderService service,
                                      TireStorageBizService bizService,
                                      TireStorageItemService itemService,
                                      TireStorageProofService proofService) {
        super(service);
        this.bizService = bizService;
        this.itemService = itemService;
        this.proofService = proofService;
    }

    @Operation(summary = "新增寄存单（含轮胎明细）")
    @PostMapping("/create")
    @AuditLog(module = "存胎寄存", operation = "下单寄存")
    public Result<TireStorageOrder> create(@RequestBody TireStorageOrder order,
                                           @RequestParam(required = false) List<TireStorageItem> items) {
        return Result.ok(bizService.create(order, items));
    }

    @Operation(summary = "入库拍照留证")
    @PostMapping("/{id}/proof")
    @AuditLog(module = "存胎寄存", operation = "上传凭证")
    public Result<Boolean> proof(@PathVariable Long id,
                                 @RequestParam String proofType,
                                 @RequestParam String images) {
        bizService.addProof(id, proofType, images, UserContext.username());
        return Result.ok(true);
    }

    @Operation(summary = "取胎核销")
    @PostMapping("/{id}/pickup")
    @AuditLog(module = "存胎寄存", operation = "取胎核销")
    public Result<TireStorageOrder> pickup(@PathVariable Long id,
                                           @RequestParam(required = false) String images) {
        return Result.ok(bizService.pickup(id, images, UserContext.username()));
    }

    @Operation(summary = "换季批量到期提醒（提前 N 天）")
    @PostMapping("/remind")
    @AuditLog(module = "存胎寄存", operation = "批量提醒")
    public Result<Integer> remind(@RequestParam(defaultValue = "7") int daysBefore) {
        return Result.ok(bizService.remindBatch(daysBefore));
    }

    @Operation(summary = "超期扫描与超期保管费结算")
    @PostMapping("/scan-overdue")
    @AuditLog(module = "存胎寄存", operation = "超期扫描")
    public Result<Integer> scanOverdue() {
        return Result.ok(bizService.scanOverdue());
    }

    @Operation(summary = "寄存库存统计")
    @GetMapping("/in-stock-count")
    public Result<Long> inStockCount(@RequestParam(required = false) Long storeId) {
        return Result.ok(bizService.inStockCount(storeId));
    }

    @Operation(summary = "轮胎明细")
    @GetMapping("/{id}/items")
    public Result<List<TireStorageItem>> items(@PathVariable Long id) {
        return Result.ok(itemService.list(new LambdaQueryWrapper<TireStorageItem>()
                .eq(TireStorageItem::getStorageOrderId, id)));
    }

    @Operation(summary = "寄存凭证")
    @GetMapping("/{id}/proofs")
    public Result<List<com.auto710.entity.TireStorageProof>> proofs(@PathVariable Long id) {
        return Result.ok(proofService.list(new LambdaQueryWrapper<com.auto710.entity.TireStorageProof>()
                .eq(com.auto710.entity.TireStorageProof::getStorageOrderId, id)));
    }
}
