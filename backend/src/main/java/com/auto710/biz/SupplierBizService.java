package com.auto710.biz;

import com.auto710.common.exception.BusinessException;
import com.auto710.common.util.IdGenUtil;
import com.auto710.dto.SupplierDeliveryForm;
import com.auto710.entity.SupplierDelivery;
import com.auto710.entity.SupplierDeliveryItem;
import com.auto710.service.SupplierDeliveryItemService;
import com.auto710.service.SupplierDeliveryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 【供应商端】送货单提交 / 门店确认入库
 * <p>状态机：待确认 →（门店确认）已入库 / 已拒绝</p>
 */
@Service
@RequiredArgsConstructor
public class SupplierBizService {

    public static final String STATUS_PENDING = "待确认";
    public static final String STATUS_STORED = "已入库";
    public static final String STATUS_REJECTED = "已拒绝";

    private final SupplierDeliveryService deliveryService;
    private final SupplierDeliveryItemService itemService;
    private final InventoryBizService inventoryBizService;

    /**
     * 供应商提交送货单
     */
    @Transactional(rollbackFor = Exception.class)
    public Long submit(Long supplierId, String supplierName, SupplierDeliveryForm form) {
        if (form == null || form.getItems() == null || form.getItems().isEmpty()) {
            throw new BusinessException("请至少添加一条送货明细");
        }
        SupplierDelivery delivery = new SupplierDelivery();
        delivery.setDeliveryNo(IdGenUtil.deliveryNo());
        delivery.setSupplierId(supplierId);
        delivery.setSupplierName(supplierName);
        delivery.setStoreId(form.getStoreId());
        delivery.setExpectDate(form.getExpectDate());
        delivery.setRemark(form.getRemark());
        delivery.setStatus(STATUS_PENDING);
        delivery.setSubmitTime(LocalDateTime.now());
        delivery.setItemCount(form.getItems().size());
        delivery.setTotalAmount(sum(form.getItems()));
        deliveryService.save(delivery);

        for (SupplierDeliveryForm.Item item : form.getItems()) {
            SupplierDeliveryItem entity = new SupplierDeliveryItem();
            entity.setDeliveryId(delivery.getId());
            entity.setPartId(item.getPartId());
            entity.setPartName(item.getPartName());
            entity.setSpec(item.getSpec());
            entity.setUnit(item.getUnit());
            entity.setQuantity(item.getQuantity());
            entity.setUnitPrice(item.getUnitPrice());
            entity.setRemark(item.getRemark());
            entity.setAmount(amount(item.getQuantity(), item.getUnitPrice()));
            itemService.save(entity);
        }
        return delivery.getId();
    }

    /**
     * 供应商撤销尚未处理的送货单
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean cancel(Long supplierId, Long deliveryId) {
        SupplierDelivery delivery = requireOwn(supplierId, deliveryId);
        if (!STATUS_PENDING.equals(delivery.getStatus())) {
            throw new BusinessException("仅待确认的送货单可撤销");
        }
        itemService.remove(new LambdaQueryWrapper<SupplierDeliveryItem>()
                .eq(SupplierDeliveryItem::getDeliveryId, deliveryId));
        return deliveryService.removeById(deliveryId);
    }

    /**
     * 门店确认（入库）或拒绝
     */
    @Transactional(rollbackFor = Exception.class)
    public void confirm(Long deliveryId, Long operatorId, boolean pass, String remark) {
        SupplierDelivery delivery = deliveryService.getById(deliveryId);
        if (delivery == null) {
            throw new BusinessException("送货单不存在");
        }
        if (!STATUS_PENDING.equals(delivery.getStatus())) {
            throw new BusinessException("该送货单已处理，当前状态：" + delivery.getStatus());
        }
        if (pass) {
            List<SupplierDeliveryItem> items = itemService.list(new LambdaQueryWrapper<SupplierDeliveryItem>()
                    .eq(SupplierDeliveryItem::getDeliveryId, deliveryId));
            for (SupplierDeliveryItem item : items) {
                if (item.getPartId() != null && item.getQuantity() != null && item.getQuantity() > 0) {
                    inventoryBizService.stockIn(item.getPartId(), item.getQuantity(), item.getUnitPrice(),
                            "供应商入库", "送货单 " + delivery.getDeliveryNo());
                }
            }
            delivery.setStatus(STATUS_STORED);
        } else {
            delivery.setStatus(STATUS_REJECTED);
        }
        delivery.setConfirmUserId(operatorId);
        delivery.setConfirmTime(LocalDateTime.now());
        delivery.setConfirmRemark(remark);
        deliveryService.updateById(delivery);
    }

    private SupplierDelivery requireOwn(Long supplierId, Long deliveryId) {
        SupplierDelivery delivery = deliveryService.getById(deliveryId);
        if (delivery == null || !supplierId.equals(delivery.getSupplierId())) {
            throw new BusinessException("无权操作该送货单");
        }
        return delivery;
    }

    private BigDecimal sum(List<SupplierDeliveryForm.Item> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (SupplierDeliveryForm.Item item : items) {
            total = total.add(amount(item.getQuantity(), item.getUnitPrice()));
        }
        return total;
    }

    private BigDecimal amount(Integer quantity, BigDecimal unitPrice) {
        if (quantity == null || unitPrice == null) {
            return BigDecimal.ZERO;
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
