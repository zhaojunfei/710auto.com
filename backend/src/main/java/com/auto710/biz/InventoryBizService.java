package com.auto710.biz;

import com.auto710.common.exception.BusinessException;
import com.auto710.entity.PartInfo;
import com.auto710.entity.PartStockRecord;
import com.auto710.service.PartInfoService;
import com.auto710.service.PartStockRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 配件/耗材库存业务服务：进销存、出库关联工单、低库存预警、成本与售价分离、毛利统计
 */
@Service
@RequiredArgsConstructor
public class InventoryBizService {

    private final PartInfoService partInfoService;
    private final PartStockRecordService stockRecordService;

    /**
     * 入库
     */
    @Transactional(rollbackFor = Exception.class)
    public void stockIn(Long partId, int quantity, BigDecimal unitPrice, String operatorName, String remark) {
        PartInfo part = requirePart(partId);
        int before = part.getStock() == null ? 0 : part.getStock();
        int after = before + quantity;
        part.setStock(after);
        if (unitPrice != null) {
            part.setPurchasePrice(unitPrice);
        }
        partInfoService.updateById(part);
        saveRecord(part, "入库", quantity, before, after, unitPrice, null, null, operatorName, remark);
    }

    /**
     * 出库（关联工单），库存不足抛异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void stockOut(Long partId, int quantity, Long workOrderId, String orderNo,
                         String operatorName, String remark) {
        PartInfo part = requirePart(partId);
        int before = part.getStock() == null ? 0 : part.getStock();
        if (before < quantity) {
            throw new BusinessException("配件[" + part.getPartName() + "]库存不足，当前库存 " + before);
        }
        int after = before - quantity;
        part.setStock(after);
        partInfoService.updateById(part);
        saveRecord(part, "出库", quantity, before, after, part.getSalePrice(),
                workOrderId, orderNo, operatorName, remark);
        assertWarning(part);
    }

    /**
     * 盘点
     */
    @Transactional(rollbackFor = Exception.class)
    public void stockCheck(Long partId, int realStock, String operatorName, String remark) {
        PartInfo part = requirePart(partId);
        int before = part.getStock() == null ? 0 : part.getStock();
        part.setStock(realStock);
        partInfoService.updateById(part);
        saveRecord(part, "盘点", realStock - before, before, realStock,
                part.getPurchasePrice(), null, null, operatorName, remark);
    }

    /**
     * 低库存预警列表
     */
    public List<PartInfo> warningList() {
        return partInfoService.list().stream()
                .filter(p -> p.getWarnStock() != null && p.getStock() != null && p.getStock() <= p.getWarnStock())
                .toList();
    }

    /**
     * 毛利统计（售价 - 成本）
     */
    public BigDecimal grossProfit(Long workOrderId) {
        List<PartStockRecord> records = stockRecordService.list(new LambdaQueryWrapper<PartStockRecord>()
                .eq(PartStockRecord::getWorkOrderId, workOrderId)
                .eq(PartStockRecord::getRecordType, "出库"));
        BigDecimal profit = BigDecimal.ZERO;
        for (PartStockRecord record : records) {
            PartInfo part = partInfoService.getById(record.getPartId());
            if (part != null && part.getSalePrice() != null && part.getPurchasePrice() != null) {
                profit = profit.add(part.getSalePrice().subtract(part.getPurchasePrice())
                        .multiply(BigDecimal.valueOf(record.getQuantity())));
            }
        }
        return profit;
    }

    private void assertWarning(PartInfo part) {
        if (part.getWarnStock() != null && part.getStock() != null && part.getStock() <= part.getWarnStock()) {
            // 预警通过查询接口暴露；此处仅记录日志
        }
    }

    private PartInfo requirePart(Long partId) {
        PartInfo part = partInfoService.getById(partId);
        if (part == null) {
            throw new BusinessException("配件不存在");
        }
        return part;
    }

    private void saveRecord(PartInfo part, String type, int quantity, int before, int after,
                            BigDecimal unitPrice, Long workOrderId, String orderNo,
                            String operatorName, String remark) {
        PartStockRecord record = new PartStockRecord();
        record.setPartId(part.getId());
        record.setPartName(part.getPartName());
        record.setRecordType(type);
        record.setQuantity(quantity);
        record.setBeforeStock(before);
        record.setAfterStock(after);
        record.setUnitPrice(unitPrice);
        record.setAmount(unitPrice == null ? null : unitPrice.multiply(BigDecimal.valueOf(Math.abs(quantity))));
        record.setWorkOrderId(workOrderId);
        record.setOrderNo(orderNo);
        record.setOperatorName(operatorName);
        record.setRemark(remark);
        stockRecordService.save(record);
    }
}
