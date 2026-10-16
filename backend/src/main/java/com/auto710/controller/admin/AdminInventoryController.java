package com.auto710.controller.admin;

import com.auto710.biz.InventoryBizService;
import com.auto710.common.annotation.AuditLog;
import com.auto710.common.api.Result;
import com.auto710.common.context.UserContext;
import com.auto710.entity.PartInfo;
import com.auto710.entity.PartStockRecord;
import com.auto710.service.PartStockRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 【后台】配件/耗材库存管理：进销存、出库关联工单、低库存预警、毛利统计
 */
@Tag(name = "库存管理")
@RestController
@RequestMapping("/api/admin/inventory")
@RequiredArgsConstructor
public class AdminInventoryController {

    private final InventoryBizService inventoryBizService;
    private final PartStockRecordService stockRecordService;

    @Operation(summary = "入库")
    @PostMapping("/stock-in")
    @AuditLog(module = "库存", operation = "入库")
    public Result<Boolean> stockIn(@RequestParam Long partId,
                                   @RequestParam int quantity,
                                   @RequestParam(required = false) BigDecimal unitPrice,
                                   @RequestParam(required = false) String remark) {
        inventoryBizService.stockIn(partId, quantity, unitPrice, UserContext.username(), remark);
        return Result.ok(true);
    }

    @Operation(summary = "出库（可关联工单）")
    @PostMapping("/stock-out")
    @AuditLog(module = "库存", operation = "出库")
    public Result<Boolean> stockOut(@RequestParam Long partId,
                                    @RequestParam int quantity,
                                    @RequestParam(required = false) Long workOrderId,
                                    @RequestParam(required = false) String orderNo,
                                    @RequestParam(required = false) String remark) {
        inventoryBizService.stockOut(partId, quantity, workOrderId, orderNo, UserContext.username(), remark);
        return Result.ok(true);
    }

    @Operation(summary = "盘点")
    @PostMapping("/stock-check")
    @AuditLog(module = "库存", operation = "盘点")
    public Result<Boolean> stockCheck(@RequestParam Long partId,
                                      @RequestParam int realStock,
                                      @RequestParam(required = false) String remark) {
        inventoryBizService.stockCheck(partId, realStock, UserContext.username(), remark);
        return Result.ok(true);
    }

    @Operation(summary = "低库存预警列表")
    @GetMapping("/warnings")
    public Result<List<PartInfo>> warnings() {
        return Result.ok(inventoryBizService.warningList());
    }

    @Operation(summary = "出入库流水")
    @GetMapping("/records")
    public Result<List<PartStockRecord>> records(@RequestParam(required = false) Long partId,
                                                 @RequestParam(required = false) Long workOrderId) {
        return Result.ok(stockRecordService.list(new LambdaQueryWrapper<PartStockRecord>()
                .eq(partId != null, PartStockRecord::getPartId, partId)
                .eq(workOrderId != null, PartStockRecord::getWorkOrderId, workOrderId)
                .orderByDesc(PartStockRecord::getCreateTime)));
    }

    @Operation(summary = "工单配件毛利")
    @GetMapping("/gross-profit")
    public Result<BigDecimal> grossProfit(@RequestParam Long workOrderId) {
        return Result.ok(inventoryBizService.grossProfit(workOrderId));
    }
}
