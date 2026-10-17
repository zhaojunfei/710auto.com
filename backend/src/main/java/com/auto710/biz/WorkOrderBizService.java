package com.auto710.biz;

import cn.hutool.core.util.StrUtil;
import com.auto710.common.constant.WorkOrderStatus;
import com.auto710.common.exception.BusinessException;
import com.auto710.common.util.IdGenUtil;
import com.auto710.entity.*;
import com.auto710.msg.MessageService;
import com.auto710.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 工单业务服务：完整状态机
 * <p>待接车 → 待派工 → 施工中 → 待质检 → 待结算 → 已离店 → 已评价；
 * 含接车开单(检查项/报价/客户确认)、派工、用料登记(关联库存出库)、质检、结算(储值抵扣)、评价、退款。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkOrderBizService {

    private final WorkOrderService workOrderService;
    private final WorkOrderItemService workOrderItemService;
    private final WorkOrderLogService workOrderLogService;
    private final WorkOrderCheckService workOrderCheckService;
    private final DispatchRecordService dispatchRecordService;
    private final RefundRecordService refundRecordService;
    private final WorkOrderEvaluateService workOrderEvaluateService;
    private final TechnicianService technicianService;
    private final MemberService memberService;
    private final MemberBizService memberBizService;
    private final MemberBalanceLogService memberBalanceLogService;
    private final MemberPointsLogService memberPointsLogService;
    private final InventoryBizService inventoryBizService;
    private final MessageService messageService;

    /**
     * 前台手动录入线下到店工单
     */
    @Transactional(rollbackFor = Exception.class)
    public WorkOrder createManual(WorkOrder order) {
        order.setId(null);
        order.setOrderNo(IdGenUtil.workOrderNo());
        order.setStatus(WorkOrderStatus.WAIT_RECEIVE);
        order.setReceiveTime(LocalDateTime.now());
        // 手机号即会员：未指定会员时按联系电话自动匹配 / 建档
        if (order.getMemberId() == null && StrUtil.isNotBlank(order.getContactPhone())) {
            Member member = memberBizService.findOrCreateByPhone(order.getContactPhone(), order.getContactName());
            if (member != null) {
                order.setMemberId(member.getId());
            }
        }
        if (order.getTotalAmount() == null) {
            order.setTotalAmount(BigDecimal.ZERO);
        }
        workOrderService.save(order);
        addLog(order.getId(), null, WorkOrderStatus.WAIT_RECEIVE, order.getContactName(), "线下到店开单");
        return order;
    }

    /**
     * 接车开单：录入检查项 / 报价项
     */
    @Transactional(rollbackFor = Exception.class)
    public void addChecks(Long workOrderId, List<WorkOrderCheck> checks) {
        WorkOrder order = require(workOrderId);
        // 检查项仅在「待接车 / 待派工」阶段可录入；进入施工中后锁定，禁止修改
        if (!WorkOrderStatus.WAIT_RECEIVE.equals(order.getStatus())
                && !WorkOrderStatus.WAIT_DISPATCH.equals(order.getStatus())) {
            throw new BusinessException("工单已进入施工环节（当前：" + order.getStatus() + "），检查项不可修改");
        }
        for (WorkOrderCheck check : checks) {
            check.setId(null);
            check.setWorkOrderId(workOrderId);
            if (check.getCustomerConfirm() == null) {
                check.setCustomerConfirm(0);
            }
        }
        workOrderCheckService.saveBatch(checks);

        BigDecimal quote = checks.stream()
                .filter(c -> c.getQuotePrice() != null)
                .map(WorkOrderCheck::getQuotePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (quote.compareTo(BigDecimal.ZERO) > 0) {
            order.setLaborAmount(quote);
            recalcTotals(order);
        }
        workOrderService.updateById(order);
    }

    /**
     * 客户线上确认报价
     */
    public void confirmQuote(Long workOrderId) {
        WorkOrder order = require(workOrderId);
        List<WorkOrderCheck> checks = workOrderCheckService.list(
                new LambdaQueryWrapper<WorkOrderCheck>().eq(WorkOrderCheck::getWorkOrderId, workOrderId));
        for (WorkOrderCheck check : checks) {
            check.setCustomerConfirm(1);
            check.setConfirmTime(LocalDateTime.now());
        }
        if (!checks.isEmpty()) {
            workOrderCheckService.updateBatchById(checks);
        }
        addLog(order.getId(), order.getStatus(), order.getStatus(), order.getContactName(), "客户已确认报价");
    }

    /**
     * 接车完成 → 待派工
     */
    public boolean receive(Long workOrderId) {
        WorkOrder order = require(workOrderId);
        transfer(order, WorkOrderStatus.WAIT_DISPATCH, "前台接车完成");
        order.setReceiveTime(LocalDateTime.now());
        return workOrderService.updateById(order);
    }

    /**
     * 派工
     */
    @Transactional(rollbackFor = Exception.class)
    public void dispatch(Long workOrderId, Long technicianId) {
        WorkOrder order = require(workOrderId);
        Technician technician = technicianService.getById(technicianId);
        if (technician == null) {
            throw new BusinessException("技师不存在");
        }
        order.setTechnicianId(technicianId);
        if (WorkOrderStatus.WAIT_RECEIVE.equals(order.getStatus())) {
            order.setStatus(WorkOrderStatus.WAIT_DISPATCH);
        }
        workOrderService.updateById(order);

        DispatchRecord record = new DispatchRecord();
        record.setWorkOrderId(workOrderId);
        record.setTechnicianId(technicianId);
        record.setTechnicianName(technician.getTechName());
        record.setDispatchType("派工");
        record.setDispatchTime(LocalDateTime.now());
        record.setStatus("已派工");
        dispatchRecordService.save(record);

        addLog(workOrderId, order.getStatus(), order.getStatus(), technician.getTechName(), "派工给技师");
        messageService.sendTemplate(order.getMemberId(), MessageService.MSG_TYPE_PROGRESS, Map.of(
                "orderNo", order.getOrderNo(),
                "technician", technician.getTechName()));
    }

    /**
     * 开工 → 施工中
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean start(Long workOrderId) {
        WorkOrder order = require(workOrderId);
        transfer(order, WorkOrderStatus.WORKING, "技师开工");
        order.setStartTime(LocalDateTime.now());
        workOrderService.updateById(order);
        messageService.sendTemplate(order.getMemberId(), MessageService.MSG_TYPE_PROGRESS, Map.of(
                "orderNo", order.getOrderNo(), "status", WorkOrderStatus.WORKING));
        return true;
    }

    /**
     * 用料登记（关联库存出库）
     */
    @Transactional(rollbackFor = Exception.class)
    public void addItem(Long workOrderId, WorkOrderItem item, boolean outStock) {
        WorkOrder order = require(workOrderId);
        item.setId(null);
        item.setWorkOrderId(workOrderId);
        if (item.getAmount() == null && item.getPrice() != null && item.getQuantity() != null) {
            item.setAmount(item.getPrice().multiply(item.getQuantity()));
        }
        workOrderItemService.save(item);

        if ("配件".equals(item.getItemType()) && outStock && item.getItemId() != null) {
            inventoryBizService.stockOut(item.getItemId(),
                    item.getQuantity() == null ? 0 : item.getQuantity().intValue(),
                    workOrderId, order.getOrderNo(), "技师用料", "工单用料出库");
        }
        recalcTotals(order);
    }

    /**
     * 完工提交 → 待质检
     */
    public boolean finish(Long workOrderId) {
        WorkOrder order = require(workOrderId);
        transfer(order, WorkOrderStatus.WAIT_CHECK, "技师完工提交");
        order.setFinishTime(LocalDateTime.now());
        workOrderService.updateById(order);
        messageService.sendTemplate(order.getMemberId(), MessageService.MSG_TYPE_FINISH, Map.of(
                "orderNo", order.getOrderNo(), "status", "待质检"));
        return true;
    }

    /**
     * 质检：通过 → 待结算；不通过 → 退回施工中
     */
    public boolean qualityCheck(Long workOrderId, boolean pass, String remark) {
        WorkOrder order = require(workOrderId);
        transfer(order, pass ? WorkOrderStatus.WAIT_PAY : WorkOrderStatus.WORKING,
                StrUtil.blankToDefault(remark, pass ? "质检通过" : "质检不通过，返工"));
        return workOrderService.updateById(order);
    }

    /**
     * 结算 → 已离店（支持储值抵扣、积分抵工时）
     */
    @Transactional(rollbackFor = Exception.class)
    public WorkOrder settle(Long workOrderId, String payType, BigDecimal useBalance, Integer usePoints) {
        WorkOrder order = require(workOrderId);
        // 结算只允许在「待结算」发起；为兼容历史数据，允许从「待质检」直接结算（自动补一次质检通过）
        boolean fromWaitPay = WorkOrderStatus.WAIT_PAY.equals(order.getStatus());
        boolean fromWaitCheck = WorkOrderStatus.WAIT_CHECK.equals(order.getStatus());
        if (!fromWaitPay && !fromWaitCheck) {
            throw new BusinessException("工单当前状态【" + order.getStatus() + "】不可结算，请先完成质检");
        }
        if (fromWaitCheck) {
            transfer(order, WorkOrderStatus.WAIT_PAY, "质检通过（结算）");
        }
        recalcTotals(order);

        BigDecimal paid = order.getTotalAmount() == null ? BigDecimal.ZERO : order.getTotalAmount();

        if (useBalance != null && useBalance.compareTo(BigDecimal.ZERO) > 0 && order.getMemberId() != null) {
            Member member = memberService.getById(order.getMemberId());
            if (member == null) {
                throw new BusinessException("会员不存在");
            }
            BigDecimal balance = member.getBalance() == null ? BigDecimal.ZERO : member.getBalance();
            if (balance.compareTo(useBalance) < 0) {
                throw new BusinessException("储值余额不足");
            }
            BigDecimal newBalance = balance.subtract(useBalance);
            member.setBalance(newBalance);
            memberService.updateById(member);
            saveBalanceLog(member.getId(), useBalance.negate(), newBalance, "消费", order.getOrderNo(), "工单结算储值抵扣");
            paid = paid.subtract(useBalance);
        }

        if (usePoints != null && usePoints > 0 && order.getMemberId() != null) {
            Member member = memberService.getById(order.getMemberId());
            if (member != null && (member.getPoints() == null ? 0 : member.getPoints()) >= usePoints) {
                BigDecimal pointsDeduct = BigDecimal.valueOf(usePoints).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.DOWN);
                paid = paid.subtract(pointsDeduct).max(BigDecimal.ZERO);
                int newPoints = member.getPoints() - usePoints;
                member.setPoints(newPoints);
                memberService.updateById(member);
                MemberPointsLog pointsLog = new MemberPointsLog();
                pointsLog.setMemberId(member.getId());
                pointsLog.setChangePoints(-usePoints);
                pointsLog.setBalancePoints(newPoints);
                pointsLog.setType("消耗");
                pointsLog.setSource("工单结算积分抵工时");
                pointsLog.setBizNo(order.getOrderNo());
                memberPointsLogService.save(pointsLog);
            }
        }

        order.setPaidAmount(paid.max(BigDecimal.ZERO));
        order.setPayType(payType);
        order.setPayTime(LocalDateTime.now());
        order.setDeliverTime(LocalDateTime.now());
        transfer(order, WorkOrderStatus.DELIVERED, "结算完成，车辆离店");

        // 结算完成后赠送积分
        if (order.getMemberId() != null && paid.compareTo(BigDecimal.ZERO) > 0) {
            Member member = memberService.getById(order.getMemberId());
            if (member != null) {
                int giftPoints = paid.intValue();
                member.setPoints((member.getPoints() == null ? 0 : member.getPoints()) + giftPoints);
                memberService.updateById(member);
                MemberPointsLog pointsLog = new MemberPointsLog();
                pointsLog.setMemberId(member.getId());
                pointsLog.setChangePoints(giftPoints);
                pointsLog.setBalancePoints(member.getPoints());
                pointsLog.setType("获取");
                pointsLog.setSource("消费赠送");
                pointsLog.setBizNo(order.getOrderNo());
                memberPointsLogService.save(pointsLog);
            }
        }

        messageService.sendTemplate(order.getMemberId(), MessageService.MSG_TYPE_SETTLE, Map.of(
                "orderNo", order.getOrderNo(),
                "paidAmount", String.valueOf(order.getPaidAmount())));
        return order;
    }

    /**
     * 客户评价
     */
    public boolean evaluate(Long workOrderId, WorkOrderEvaluate evaluate) {
        WorkOrder order = require(workOrderId);
        evaluate.setId(null);
        evaluate.setWorkOrderId(workOrderId);
        evaluate.setMemberId(order.getMemberId());
        if (evaluate.getStatus() == null) {
            evaluate.setStatus(1);
        }
        workOrderEvaluateService.save(evaluate);
        order.setEvaluateStatus(1);
        transfer(order, WorkOrderStatus.EVALUATED, "客户已评价");
        return workOrderService.updateById(order);
    }

    /**
     * 发起退款申请
     */
    public RefundRecord applyRefund(Long workOrderId, BigDecimal amount, String reason, String images) {
        WorkOrder order = require(workOrderId);
        RefundRecord record = new RefundRecord();
        record.setRefundNo(IdGenUtil.refundNo());
        record.setWorkOrderId(workOrderId);
        record.setOrderNo(order.getOrderNo());
        record.setMemberId(order.getMemberId());
        record.setRefundAmount(amount);
        record.setReason(reason);
        record.setImages(images);
        record.setStatus("待审核");
        refundRecordService.save(record);
        return record;
    }

    /**
     * 退款审核
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean auditRefund(Long refundId, boolean pass, String remark, Long auditorId) {
        RefundRecord record = refundRecordService.getById(refundId);
        if (record == null) {
            throw new BusinessException("退款单不存在");
        }
        record.setStatus(pass ? "已退款" : "已拒绝");
        record.setAuditUserId(auditorId);
        record.setAuditTime(LocalDateTime.now());
        record.setAuditRemark(remark);
        refundRecordService.updateById(record);

        if (pass && record.getMemberId() != null && record.getRefundAmount() != null) {
            Member member = memberService.getById(record.getMemberId());
            if (member != null) {
                BigDecimal balance = (member.getBalance() == null ? BigDecimal.ZERO : member.getBalance())
                        .add(record.getRefundAmount());
                member.setBalance(balance);
                memberService.updateById(member);
                saveBalanceLog(member.getId(), record.getRefundAmount(), balance, "退款", record.getOrderNo(), "退款到余额");
            }
        }
        messageService.sendTemplate(record.getMemberId(), MessageService.MSG_TYPE_REFUND, Map.of(
                "refundNo", record.getRefundNo(), "status", record.getStatus()));
        return true;
    }

    /**
     * 看板：按状态分列
     */
    public Map<String, List<WorkOrder>> kanban() {
        Map<String, List<WorkOrder>> board = new LinkedHashMap<>();
        for (String status : WorkOrderStatus.KANBAN) {
            board.put(status, workOrderService.list(new LambdaQueryWrapper<WorkOrder>()
                    .eq(WorkOrder::getStatus, status)
                    .orderByDesc(WorkOrder::getIsUrgent)
                    .orderByAsc(WorkOrder::getCreateTime)));
        }
        return board;
    }

    /**
     * 调整优先级（加急）
     */
    public boolean setUrgent(Long workOrderId, boolean urgent) {
        WorkOrder order = require(workOrderId);
        order.setIsUrgent(urgent ? 1 : 0);
        return workOrderService.updateById(order);
    }

    /**
     * 工单状态流转（含日志）
     */
    public void transfer(WorkOrder order, String toStatus, String remark) {
        String from = order.getStatus();
        if (!WorkOrderStatus.canTransfer(from, toStatus)) {
            throw new BusinessException("非法状态流转：" + from + " → " + toStatus);
        }
        order.setStatus(toStatus);
        workOrderService.updateById(order);
        addLog(order.getId(), from, toStatus, null, remark);
    }

    private void addLog(Long workOrderId, String from, String to, String operator, String remark) {
        WorkOrderLog log = new WorkOrderLog();
        log.setWorkOrderId(workOrderId);
        log.setFromStatus(from);
        log.setToStatus(to);
        log.setOperatorName(operator);
        log.setRemark(remark);
        workOrderLogService.save(log);
    }

    private void recalcTotals(WorkOrder order) {
        List<WorkOrderItem> items = workOrderItemService.list(
                new LambdaQueryWrapper<WorkOrderItem>().eq(WorkOrderItem::getWorkOrderId, order.getId()));
        BigDecimal labor = items.stream()
                .filter(i -> "服务".equals(i.getItemType()))
                .map(i -> i.getAmount() == null ? BigDecimal.ZERO : i.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal parts = items.stream()
                .filter(i -> "配件".equals(i.getItemType()))
                .map(i -> i.getAmount() == null ? BigDecimal.ZERO : i.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (labor.compareTo(BigDecimal.ZERO) == 0 && order.getLaborAmount() != null) {
            labor = order.getLaborAmount();
        }
        order.setLaborAmount(labor);
        order.setPartsAmount(parts);
        BigDecimal discount = order.getDiscountAmount() == null ? BigDecimal.ZERO : order.getDiscountAmount();
        order.setTotalAmount(labor.add(parts).subtract(discount).max(BigDecimal.ZERO));
        workOrderService.updateById(order);
    }

    private void saveBalanceLog(Long memberId, BigDecimal change, BigDecimal balance,
                                String type, String bizNo, String remark) {
        MemberBalanceLog logEntity = new MemberBalanceLog();
        logEntity.setMemberId(memberId);
        logEntity.setChangeAmount(change);
        logEntity.setBalanceAmount(balance);
        logEntity.setType(type);
        logEntity.setBizNo(bizNo);
        logEntity.setRemark(remark);
        memberBalanceLogService.save(logEntity);
    }

    private WorkOrder require(Long id) {
        WorkOrder order = workOrderService.getById(id);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        return order;
    }
}
