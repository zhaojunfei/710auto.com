package com.auto710.controller.api;

import cn.hutool.core.util.StrUtil;
import com.auto710.biz.*;
import com.auto710.common.api.PageResult;
import com.auto710.common.api.Result;
import com.auto710.common.context.UserContext;
import com.auto710.common.util.EncryptUtil;
import com.auto710.dto.WxLoginRequest;
import com.auto710.entity.*;
import com.auto710.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 【小程序 / H5】会员中心接口（UnionID 与公众号互通）
 */
@Tag(name = "会员中心")
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberBizService memberBizService;
    private final CouponBizService couponBizService;
    private final AppointmentBizService appointmentBizService;
    private final WorkOrderBizService workOrderBizService;
    private final TireStorageBizService tireStorageBizService;
    private final MemberService memberService;
    private final MemberPointsLogService pointsLogService;
    private final MemberBalanceLogService balanceLogService;
    private final MemberCouponService memberCouponService;
    private final CouponService couponService;
    private final WorkOrderService workOrderService;
    private final WorkOrderItemService workOrderItemService;
    private final WorkOrderLogService workOrderLogService;
    private final AppointmentService appointmentService;
    private final TireStorageOrderService tireStorageOrderService;
    private final TireStorageItemService tireStorageItemService;
    private final TireStorageProofService tireStorageProofService;
    private final MaintenancePackageService packageService;
    private final MemberPackageService memberPackageService;
    private final CmsMessageService messageService;

    @Operation(summary = "微信一键授权登录（公众号/小程序/H5 互通）")
    @PostMapping("/wx-login")
    public Result<Map<String, Object>> wxLogin(@RequestBody WxLoginRequest request) {
        return Result.ok(memberBizService.wxLogin(request));
    }

    @Operation(summary = "会员信息")
    @GetMapping("/profile")
    public Result<Member> profile() {
        return Result.ok(memberBizService.profile(UserContext.userId()));
    }

    @Operation(summary = "更新会员信息")
    @PutMapping("/profile")
    public Result<Member> updateProfile(@RequestBody Member member) {
        return Result.ok(memberBizService.updateProfile(UserContext.userId(), member));
    }

    @Operation(summary = "我的车辆")
    @GetMapping("/vehicles")
    public Result<List<MemberVehicle>> vehicles() {
        return Result.ok(memberBizService.listVehicles(UserContext.userId()));
    }

    @Operation(summary = "绑定车辆（多车）")
    @PostMapping("/vehicles")
    public Result<MemberVehicle> addVehicle(@RequestBody MemberVehicle vehicle) {
        return Result.ok(memberBizService.addVehicle(UserContext.userId(), vehicle));
    }

    @Operation(summary = "解绑车辆")
    @DeleteMapping("/vehicles/{id}")
    public Result<Boolean> deleteVehicle(@PathVariable Long id) {
        return Result.ok(memberBizService.deleteVehicle(UserContext.userId(), id));
    }

    @Operation(summary = "储值充值")
    @PostMapping("/recharge")
    public Result<Member> recharge(@RequestParam BigDecimal amount,
                                   @RequestParam(required = false) Long ruleId) {
        return Result.ok(memberBizService.recharge(UserContext.userId(), amount, ruleId));
    }

    @Operation(summary = "积分流水")
    @GetMapping("/points-logs")
    public Result<List<MemberPointsLog>> pointsLogs() {
        return Result.ok(pointsLogService.list(new LambdaQueryWrapper<MemberPointsLog>()
                .eq(MemberPointsLog::getMemberId, UserContext.userId())
                .orderByDesc(MemberPointsLog::getCreateTime)));
    }

    @Operation(summary = "储值流水")
    @GetMapping("/balance-logs")
    public Result<List<MemberBalanceLog>> balanceLogs() {
        return Result.ok(balanceLogService.list(new LambdaQueryWrapper<MemberBalanceLog>()
                .eq(MemberBalanceLog::getMemberId, UserContext.userId())
                .orderByDesc(MemberBalanceLog::getCreateTime)));
    }

    @Operation(summary = "可领取优惠券")
    @GetMapping("/coupons/available")
    public Result<List<Coupon>> availableCoupons() {
        return Result.ok(couponService.list(new LambdaQueryWrapper<Coupon>()
                .eq(Coupon::getStatus, 1)
                .orderByDesc(Coupon::getCreateTime)));
    }

    @Operation(summary = "领取优惠券")
    @PostMapping("/coupons/{couponId}/receive")
    public Result<MemberCoupon> receiveCoupon(@PathVariable Long couponId) {
        return Result.ok(couponBizService.receive(UserContext.userId(), couponId));
    }

    @Operation(summary = "我的优惠券")
    @GetMapping("/coupons")
    public Result<List<MemberCoupon>> myCoupons(@RequestParam(required = false) String status) {
        return Result.ok(couponBizService.myCoupons(UserContext.userId(), status));
    }

    @Operation(summary = "提交预约")
    @PostMapping("/appointments")
    public Result<Appointment> createAppointment(@RequestBody Appointment appointment) {
        appointment.setMemberId(UserContext.userId());
        return Result.ok(appointmentBizService.create(appointment));
    }

    @Operation(summary = "我的预约")
    @GetMapping("/appointments")
    public Result<PageResult<Appointment>> myAppointments(@RequestParam(defaultValue = "1") long page,
                                                          @RequestParam(defaultValue = "10") long size) {
        Page<Appointment> p = new Page<>(page, size);
        return Result.ok(PageResult.of(appointmentService.page(p,
                new LambdaQueryWrapper<Appointment>()
                        .eq(Appointment::getMemberId, UserContext.userId())
                        .orderByDesc(Appointment::getCreateTime))));
    }

    @Operation(summary = "取消预约")
    @PostMapping("/appointments/{id}/cancel")
    public Result<Boolean> cancelAppointment(@PathVariable Long id, @RequestParam(required = false) String reason) {
        return Result.ok(appointmentBizService.cancel(id, reason));
    }

    @Operation(summary = "我的工单")
    @GetMapping("/work-orders")
    public Result<List<WorkOrder>> myWorkOrders() {
        List<WorkOrder> orders = workOrderService.list(new LambdaQueryWrapper<WorkOrder>()
                .eq(WorkOrder::getMemberId, UserContext.userId())
                .orderByDesc(WorkOrder::getCreateTime));
        orders.forEach(o -> o.setPlateNo(EncryptUtil.decrypt(o.getPlateNo())));
        return Result.ok(orders);
    }

    @Operation(summary = "工单详情")
    @GetMapping("/work-orders/{id}")
    public Result<WorkOrder> workOrder(@PathVariable Long id) {
        WorkOrder order = workOrderService.getById(id);
        if (order != null && UserContext.userId().equals(order.getMemberId())) {
            order.setPlateNo(EncryptUtil.decrypt(order.getPlateNo()));
        }
        return Result.ok(order);
    }

    @Operation(summary = "工单明细（用料 / 项目）")
    @GetMapping("/work-orders/{id}/items")
    public Result<List<WorkOrderItem>> workOrderItems(@PathVariable Long id) {
        assertMemberOrder(id);
        return Result.ok(workOrderItemService.list(new LambdaQueryWrapper<WorkOrderItem>()
                .eq(WorkOrderItem::getWorkOrderId, id)));
    }

    @Operation(summary = "工单流转日志（施工进度）")
    @GetMapping("/work-orders/{id}/logs")
    public Result<List<WorkOrderLog>> workOrderLogs(@PathVariable Long id) {
        assertMemberOrder(id);
        return Result.ok(workOrderLogService.list(new LambdaQueryWrapper<WorkOrderLog>()
                .eq(WorkOrderLog::getWorkOrderId, id)
                .orderByAsc(WorkOrderLog::getCreateTime)));
    }

    @Operation(summary = "在线结算（微信支付 / 储值抵扣 / 积分）")
    @PostMapping("/work-orders/{id}/settle")
    public Result<WorkOrder> settle(@PathVariable Long id,
                                    @RequestParam(defaultValue = "WECHAT") String payType,
                                    @RequestParam(required = false) BigDecimal useBalance,
                                    @RequestParam(required = false) Integer usePoints) {
        assertMemberOrder(id);
        return Result.ok(workOrderBizService.settle(id, payType, useBalance, usePoints));
    }

    /** 校验工单归属当前会员，避免越权访问他人单据 */
    private void assertMemberOrder(Long workOrderId) {
        WorkOrder order = workOrderService.getById(workOrderId);
        if (order == null) {
            throw new com.auto710.common.exception.BusinessException("工单不存在");
        }
        if (!UserContext.userId().equals(order.getMemberId())) {
            throw new com.auto710.common.exception.BusinessException("无权访问该工单");
        }
    }

    @Operation(summary = "确认报价")
    @PostMapping("/work-orders/{id}/confirm-quote")
    public Result<Boolean> confirmQuote(@PathVariable Long id) {
        workOrderBizService.confirmQuote(id);
        return Result.ok(true);
    }

    @Operation(summary = "工单评价")
    @PostMapping("/work-orders/{id}/evaluate")
    public Result<Boolean> evaluate(@PathVariable Long id, @RequestBody WorkOrderEvaluate evaluate) {
        return Result.ok(workOrderBizService.evaluate(id, evaluate));
    }

    @Operation(summary = "申请退款")
    @PostMapping("/work-orders/{id}/refund")
    public Result<RefundRecord> refund(@PathVariable Long id,
                                       @RequestParam BigDecimal amount,
                                       @RequestParam(required = false) String reason,
                                       @RequestParam(required = false) String images) {
        return Result.ok(workOrderBizService.applyRefund(id, amount, reason, images));
    }

    @Operation(summary = "在线下单存胎")
    @PostMapping("/tire-storage")
    public Result<TireStorageOrder> createTireStorage(@RequestBody TireStorageOrder order,
                                                      @RequestParam(required = false) String itemsJson) {
        order.setMemberId(UserContext.userId());
        List<TireStorageItem> items = StrUtil.isBlank(itemsJson)
                ? null : cn.hutool.json.JSONUtil.toList(itemsJson, TireStorageItem.class);
        return Result.ok(tireStorageBizService.create(order, items));
    }

    @Operation(summary = "我的存胎记录")
    @GetMapping("/tire-storage")
    public Result<List<TireStorageOrder>> myTireStorage() {
        List<TireStorageOrder> list = tireStorageOrderService.list(new LambdaQueryWrapper<TireStorageOrder>()
                .eq(TireStorageOrder::getMemberId, UserContext.userId())
                .orderByDesc(TireStorageOrder::getCreateTime));
        list.forEach(o -> o.setPlateNo(EncryptUtil.decrypt(o.getPlateNo())));
        return Result.ok(list);
    }

    @Operation(summary = "存胎明细（轮胎清单）")
    @GetMapping("/tire-storage/{id}/items")
    public Result<List<TireStorageItem>> tireItems(@PathVariable Long id) {
        return Result.ok(tireStorageItemService.list(new LambdaQueryWrapper<TireStorageItem>()
                .eq(TireStorageItem::getStorageOrderId, id)));
    }

    @Operation(summary = "存胎凭证（入库 / 取胎留证）")
    @GetMapping("/tire-storage/{id}/proofs")
    public Result<List<TireStorageProof>> tireProofs(@PathVariable Long id) {
        return Result.ok(tireStorageProofService.list(new LambdaQueryWrapper<TireStorageProof>()
                .eq(TireStorageProof::getStorageOrderId, id)
                .orderByDesc(TireStorageProof::getProofTime)));
    }

    @Operation(summary = "养护套餐列表")
    @GetMapping("/packages")
    public Result<List<MaintenancePackage>> packages() {
        return Result.ok(packageService.list(new LambdaQueryWrapper<MaintenancePackage>()
                .eq(MaintenancePackage::getStatus, 1)));
    }

    @Operation(summary = "我的套餐")
    @GetMapping("/my-packages")
    public Result<List<MemberPackage>> myPackages() {
        return Result.ok(memberPackageService.list(new LambdaQueryWrapper<MemberPackage>()
                .eq(MemberPackage::getMemberId, UserContext.userId())));
    }

    @Operation(summary = "提交留言/咨询")
    @PostMapping("/message")
    public Result<Boolean> message(@RequestBody CmsMessage message) {
        message.setId(null);
        message.setMemberId(UserContext.userId());
        message.setSource(StrUtil.blankToDefault(message.getSource(), "miniapp"));
        message.setStatus(0);
        return Result.ok(messageService.save(message));
    }
}
