package com.auto710.controller.api;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.auto710.biz.WorkOrderBizService;
import com.auto710.common.api.Result;
import com.auto710.common.context.LoginUser;
import com.auto710.common.context.UserContext;
import com.auto710.common.exception.BusinessException;
import com.auto710.common.util.EncryptUtil;
import com.auto710.common.util.JwtUtil;
import com.auto710.entity.*;
import com.auto710.security.RbacService;
import com.auto710.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 【接车技师端】登录、我的工单、接车开单、施工进度上报、用料登记、完工提交、质检状态
 * <p>权限：仅看本技师被派工单，不可改价（改价需店长）。</p>
 */
@Tag(name = "接车技师端")
@RestController("technicianApiController")
@RequestMapping("/api/tech")
@RequiredArgsConstructor
public class TechnicianController {

    private final SysUserService userService;
    private final TechnicianService technicianService;
    private final WorkOrderService workOrderService;
    private final WorkOrderItemService itemService;
    private final WorkOrderCheckService checkService;
    private final WorkOrderLogService logService;
    private final WorkOrderBizService workOrderBizService;
    private final RbacService rbacService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "技师登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        SysUser user = userService.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, body.get("username")).last("limit 1"));
        if (user == null || !BCrypt.checkpw(body.get("password"), user.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }
        Technician technician = technicianService.getOne(new LambdaQueryWrapper<Technician>()
                .eq(Technician::getUserId, user.getId()).last("limit 1"));
        if (technician == null) {
            throw new BusinessException("当前账号未绑定技师档案");
        }
        List<SysRole> roles = rbacService.getRoles(user.getId());
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setRealName(technician.getTechName());
        loginUser.setUserType("TECHNICIAN");
        loginUser.setStoreId(technician.getStoreId());
        loginUser.setSuperAdmin(rbacService.isSuperAdmin(roles));
        loginUser.setRoles(rbacService.roleCodes(roles));

        Map<String, Object> result = new HashMap<>();
        result.put("token", jwtUtil.createToken(loginUser));
        result.put("technician", technician);
        return Result.ok(result);
    }

    @Operation(summary = "技师信息")
    @GetMapping("/profile")
    public Result<Technician> profile() {
        return Result.ok(currentTechnician());
    }

    @Operation(summary = "我的工单（仅本技师）")
    @GetMapping("/orders")
    public Result<List<WorkOrder>> myOrders(@RequestParam(required = false) String status) {
        Technician technician = currentTechnician();
        List<WorkOrder> orders = workOrderService.list(new LambdaQueryWrapper<WorkOrder>()
                .eq(WorkOrder::getTechnicianId, technician.getId())
                .eq(StrUtil.isNotBlank(status), WorkOrder::getStatus, status)
                .orderByDesc(WorkOrder::getIsUrgent)
                .orderByAsc(WorkOrder::getCreateTime));
        orders.forEach(o -> o.setPlateNo(EncryptUtil.decrypt(o.getPlateNo())));
        return Result.ok(orders);
    }

    @Operation(summary = "工单详情（含检查项/明细）")
    @GetMapping("/orders/{id}")
    public Result<Map<String, Object>> orderDetail(@PathVariable Long id) {
        WorkOrder order = requireOwn(id);
        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        data.put("checks", checkService.list(new LambdaQueryWrapper<WorkOrderCheck>()
                .eq(WorkOrderCheck::getWorkOrderId, id)));
        data.put("items", itemService.list(new LambdaQueryWrapper<WorkOrderItem>()
                .eq(WorkOrderItem::getWorkOrderId, id)));
        data.put("logs", logService.list(new LambdaQueryWrapper<WorkOrderLog>()
                .eq(WorkOrderLog::getWorkOrderId, id).orderByDesc(WorkOrderLog::getCreateTime)));
        return Result.ok(data);
    }

    @Operation(summary = "接车开单：录入检查项/报价")
    @PostMapping("/orders/{id}/checks")
    public Result<Boolean> addChecks(@PathVariable Long id, @RequestBody List<WorkOrderCheck> checks) {
        requireOwn(id);
        workOrderBizService.addChecks(id, checks);
        return Result.ok(true);
    }

    @Operation(summary = "一键开工")
    @PostMapping("/orders/{id}/start")
    public Result<Boolean> start(@PathVariable Long id) {
        requireOwn(id);
        return Result.ok(workOrderBizService.start(id));
    }

    @Operation(summary = "用料登记（关联库存出库）")
    @PostMapping("/orders/{id}/items")
    public Result<Boolean> addItem(@PathVariable Long id,
                                   @RequestBody WorkOrderItem item,
                                   @RequestParam(defaultValue = "true") boolean outStock) {
        requireOwn(id);
        workOrderBizService.addItem(id, item, outStock);
        return Result.ok(true);
    }

    @Operation(summary = "完工提交")
    @PostMapping("/orders/{id}/finish")
    public Result<Boolean> finish(@PathVariable Long id) {
        requireOwn(id);
        return Result.ok(workOrderBizService.finish(id));
    }

    @Operation(summary = "异常卡单上报")
    @PostMapping("/orders/{id}/abnormal")
    public Result<Boolean> abnormal(@PathVariable Long id, @RequestParam String reason) {
        requireOwn(id);
        WorkOrderLog log = new WorkOrderLog();
        log.setWorkOrderId(id);
        log.setOperatorName(UserContext.username());
        log.setRemark("异常上报：" + reason);
        logService.save(log);
        return Result.ok(true);
    }

    @Operation(summary = "个人日接单量 / 工时统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Technician technician = currentTechnician();
        List<WorkOrder> orders = workOrderService.list(new LambdaQueryWrapper<WorkOrder>()
                .eq(WorkOrder::getTechnicianId, technician.getId()));
        long finished = orders.stream().filter(o -> "已离店".equals(o.getStatus()) || "已评价".equals(o.getStatus())).count();
        Map<String, Object> data = new HashMap<>();
        data.put("totalOrders", orders.size());
        data.put("finishedOrders", finished);
        data.put("workingOrders", orders.stream().filter(o -> "施工中".equals(o.getStatus())).count());
        return Result.ok(data);
    }

    private Technician currentTechnician() {
        Technician technician = technicianService.getOne(new LambdaQueryWrapper<Technician>()
                .eq(Technician::getUserId, UserContext.require().getUserId()).last("limit 1"));
        if (technician == null) {
            throw new BusinessException("当前账号未绑定技师档案");
        }
        return technician;
    }

    private WorkOrder requireOwn(Long orderId) {
        WorkOrder order = workOrderService.getById(orderId);
        Technician technician = currentTechnician();
        if (order == null || !technician.getId().equals(order.getTechnicianId())) {
            throw new BusinessException("无权访问该工单");
        }
        return order;
    }
}
