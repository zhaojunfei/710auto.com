package com.auto710.biz;

import com.auto710.common.constant.TireStorageStatus;
import com.auto710.common.exception.BusinessException;
import com.auto710.common.util.EncryptUtil;
import com.auto710.common.util.IdGenUtil;
import com.auto710.entity.TireStorageItem;
import com.auto710.entity.TireStorageOrder;
import com.auto710.entity.TireStorageProof;
import com.auto710.entity.TireStorageStock;
import com.auto710.msg.MessageService;
import com.auto710.service.TireStorageItemService;
import com.auto710.service.TireStorageOrderService;
import com.auto710.service.TireStorageProofService;
import com.auto710.service.TireStorageStockService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * 存胎寄存业务服务（特色业务）
 * <p>下单 → 入库拍照留证 → 寄存中 → 换季到期提醒 → 取胎核销；含超期保管费结算、寄存库存。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TireStorageBizService {

    private final TireStorageOrderService storageOrderService;
    private final TireStorageItemService storageItemService;
    private final TireStorageProofService proofService;
    private final TireStorageStockService stockService;
    private final MessageService messageService;

    /**
     * 在线下单存胎
     */
    @Transactional(rollbackFor = Exception.class)
    public TireStorageOrder create(TireStorageOrder order, List<TireStorageItem> items) {
        order.setId(null);
        order.setStorageNo(IdGenUtil.tireStorageNo());
        order.setPlateNo(EncryptUtil.encrypt(order.getPlateNo()));
        order.setStatus(TireStorageStatus.STORED);
        order.setStartDate(LocalDate.now().toString());
        if (order.getStorageFee() == null) {
            order.setStorageFee(BigDecimal.ZERO);
        }
        storageOrderService.save(order);

        if (items != null) {
            for (TireStorageItem item : items) {
                item.setId(null);
                item.setStorageOrderId(order.getId());
            }
            storageItemService.saveBatch(items);
        }

        // 入库：写库存 + 入库拍照留证
        TireStorageStock stock = new TireStorageStock();
        stock.setStoreId(order.getStoreId());
        stock.setPositionNo(order.getPositionNo());
        stock.setStorageOrderId(order.getId());
        stock.setPlateNo(order.getPlateNo());
        stock.setTireCount(order.getTireCount());
        stock.setStatus("在库");
        stock.setInTime(LocalDateTime.now());
        stockService.save(stock);
        return order;
    }

    /**
     * 上传入库/取胎凭证（拍照留证）
     */
    public void addProof(Long storageOrderId, String proofType, String images, String operatorName) {
        TireStorageProof proof = new TireStorageProof();
        proof.setStorageOrderId(storageOrderId);
        proof.setProofType(proofType);
        proof.setImages(images);
        proof.setOperatorName(operatorName);
        proof.setProofTime(LocalDateTime.now());
        proofService.save(proof);
    }

    /**
     * 取胎核销
     */
    @Transactional(rollbackFor = Exception.class)
    public TireStorageOrder pickup(Long storageOrderId, String images, String operatorName) {
        TireStorageOrder order = require(storageOrderId);
        if (TireStorageStatus.PICKED.equals(order.getStatus())) {
            throw new BusinessException("该寄存单已取胎");
        }
        // 超期保管费结算
        BigDecimal overdue = calcOverdueFee(order);
        if (overdue.compareTo(BigDecimal.ZERO) > 0) {
            order.setOverdueFee(overdue);
        }
        order.setStatus(TireStorageStatus.PICKED);
        order.setRealOutDate(LocalDate.now().toString());
        storageOrderService.updateById(order);

        addProof(storageOrderId, "取胎", images, operatorName);

        TireStorageStock stock = stockService.getOne(new LambdaQueryWrapper<TireStorageStock>()
                .eq(TireStorageStock::getStorageOrderId, storageOrderId)
                .eq(TireStorageStock::getStatus, "在库")
                .last("limit 1"));
        if (stock != null) {
            stock.setStatus("已出库");
            stock.setOutTime(LocalDateTime.now());
            stockService.updateById(stock);
        }
        return order;
    }

    /**
     * 换季批量到期提醒
     */
    public int remindBatch(int daysBefore) {
        LocalDate target = LocalDate.now().plusDays(daysBefore);
        List<TireStorageOrder> orders = storageOrderService.list(new LambdaQueryWrapper<TireStorageOrder>()
                .in(TireStorageOrder::getStatus, List.of(TireStorageStatus.STORED, TireStorageStatus.STORING))
                .eq(TireStorageOrder::getEndDate, target.toString()));
        for (TireStorageOrder order : orders) {
            order.setStatus(TireStorageStatus.WAIT_PICK);
            storageOrderService.updateById(order);
            messageService.pushTireRemind(order.getId());
            messageService.sendTemplate(order.getMemberId(), MessageService.MSG_TYPE_TIRE_REMIND, Map.of(
                    "storageNo", order.getStorageNo(),
                    "endDate", order.getEndDate()));
        }
        return orders.size();
    }

    /**
     * 超期扫描：标记超期
     */
    public int scanOverdue() {
        List<TireStorageOrder> orders = storageOrderService.list(new LambdaQueryWrapper<TireStorageOrder>()
                .in(TireStorageOrder::getStatus,
                        List.of(TireStorageStatus.STORED, TireStorageStatus.STORING, TireStorageStatus.WAIT_PICK)));
        int count = 0;
        for (TireStorageOrder order : orders) {
            if (order.getEndDate() != null && LocalDate.parse(order.getEndDate()).isBefore(LocalDate.now())) {
                order.setStatus(TireStorageStatus.OVERDUE);
                order.setOverdueFee(calcOverdueFee(order));
                storageOrderService.updateById(order);
                count++;
            }
        }
        return count;
    }

    /**
     * 超期保管费 = 超期天数 × 每日保管费（默认 5 元/天/车，可从系统参数配置）
     */
    public BigDecimal calcOverdueFee(TireStorageOrder order) {
        if (order.getEndDate() == null) {
            return BigDecimal.ZERO;
        }
        LocalDate end = LocalDate.parse(order.getEndDate());
        long overdueDays = ChronoUnit.DAYS.between(end, LocalDate.now());
        if (overdueDays <= 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(overdueDays).multiply(BigDecimal.valueOf(5));
    }

    /**
     * 寄存库存统计
     */
    public long inStockCount(Long storeId) {
        return stockService.count(new LambdaQueryWrapper<TireStorageStock>()
                .eq(storeId != null, TireStorageStock::getStoreId, storeId)
                .eq(TireStorageStock::getStatus, "在库"));
    }

    private TireStorageOrder require(Long id) {
        TireStorageOrder order = storageOrderService.getById(id);
        if (order == null) {
            throw new BusinessException("寄存单不存在");
        }
        return order;
    }
}
