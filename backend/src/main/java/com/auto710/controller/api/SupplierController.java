package com.auto710.controller.api;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.auto710.biz.SupplierBizService;
import com.auto710.common.api.Result;
import com.auto710.common.context.LoginUser;
import com.auto710.common.context.UserContext;
import com.auto710.common.exception.BusinessException;
import com.auto710.common.util.JwtUtil;
import com.auto710.dto.SupplierDeliveryForm;
import com.auto710.entity.PartInfo;
import com.auto710.entity.PartSupplier;
import com.auto710.entity.SupplierDelivery;
import com.auto710.entity.SupplierDeliveryItem;
import com.auto710.entity.SysUser;
import com.auto710.service.PartInfoService;
import com.auto710.service.PartSupplierService;
import com.auto710.service.SupplierDeliveryItemService;
import com.auto710.service.SupplierDeliveryService;
import com.auto710.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 【供应商端】登录、配件报价维护、送货单提交、对账单
 * <p>权限：仅能查看/维护与自己供应商档案绑定的数据。</p>
 */
@Tag(name = "供应商端")
@RestController("supplierApiController")
@RequestMapping("/api/supplier")
@RequiredArgsConstructor
public class SupplierController {

    private final SysUserService userService;
    private final PartSupplierService supplierService;
    private final SupplierDeliveryService deliveryService;
    private final SupplierDeliveryItemService deliveryItemService;
    private final PartInfoService partInfoService;
    private final SupplierBizService supplierBizService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "供应商登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        SysUser user = userService.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, body.get("username")).last("limit 1"));
        if (user == null || !BCrypt.checkpw(body.get("password"), user.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }
        PartSupplier supplier = supplierService.getOne(new LambdaQueryWrapper<PartSupplier>()
                .eq(PartSupplier::getUserId, user.getId()).last("limit 1"));
        if (supplier == null) {
            throw new BusinessException("当前账号未绑定供应商档案");
        }
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setRealName(supplier.getSupplierName());
        loginUser.setUserType("SUPPLIER");
        Map<String, Object> result = new HashMap<>();
        result.put("token", jwtUtil.createToken(loginUser));
        result.put("supplier", supplier);
        return Result.ok(result);
    }

    @Operation(summary = "供应商信息")
    @GetMapping("/profile")
    public Result<PartSupplier> profile() {
        return Result.ok(currentSupplier());
    }

    @Operation(summary = "我的配件（供货目录 / 报价）")
    @GetMapping("/parts")
    public Result<List<PartInfo>> parts(@RequestParam(required = false) String keyword) {
        PartSupplier supplier = currentSupplier();
        return Result.ok(partInfoService.list(new LambdaQueryWrapper<PartInfo>()
                .eq(PartInfo::getSupplier, supplier.getSupplierName())
                .like(StrUtil.isNotBlank(keyword), PartInfo::getPartName, keyword)
                .orderByDesc(PartInfo::getId)));
    }

    @Operation(summary = "新增或修改我的配件报价")
    @PostMapping("/parts")
    public Result<Long> savePart(@RequestBody PartInfo part) {
        PartSupplier supplier = currentSupplier();
        if (part.getId() == null) {
            part.setSupplier(supplier.getSupplierName());
            part.setStock(part.getStock() == null ? 0 : part.getStock());
            if (part.getStatus() == null) {
                part.setStatus(1);
            }
            partInfoService.save(part);
        } else {
            PartInfo exist = partInfoService.getById(part.getId());
            if (exist == null || !supplier.getSupplierName().equals(exist.getSupplier())) {
                throw new BusinessException("无权修改该配件");
            }
            part.setSupplier(supplier.getSupplierName());
            partInfoService.updateById(part);
        }
        return Result.ok(part.getId());
    }

    @Operation(summary = "我的送货单")
    @GetMapping("/deliveries")
    public Result<List<SupplierDelivery>> deliveries(@RequestParam(required = false) String status) {
        PartSupplier supplier = currentSupplier();
        return Result.ok(deliveryService.list(new LambdaQueryWrapper<SupplierDelivery>()
                .eq(SupplierDelivery::getSupplierId, supplier.getId())
                .eq(StrUtil.isNotBlank(status), SupplierDelivery::getStatus, status)
                .orderByDesc(SupplierDelivery::getId)));
    }

    @Operation(summary = "送货单详情")
    @GetMapping("/deliveries/{id}")
    public Result<Map<String, Object>> delivery(@PathVariable Long id) {
        PartSupplier supplier = currentSupplier();
        SupplierDelivery delivery = deliveryService.getById(id);
        if (delivery == null || !supplier.getId().equals(delivery.getSupplierId())) {
            throw new BusinessException("无权查看该送货单");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("delivery", delivery);
        data.put("items", deliveryItemService.list(new LambdaQueryWrapper<SupplierDeliveryItem>()
                .eq(SupplierDeliveryItem::getDeliveryId, id)));
        return Result.ok(data);
    }

    @Operation(summary = "提交送货单")
    @PostMapping("/deliveries")
    public Result<Long> submit(@RequestBody SupplierDeliveryForm form) {
        PartSupplier supplier = currentSupplier();
        return Result.ok(supplierBizService.submit(supplier.getId(), supplier.getSupplierName(), form));
    }

    @Operation(summary = "撤销送货单（仅待确认）")
    @PostMapping("/deliveries/{id}/cancel")
    public Result<Boolean> cancel(@PathVariable Long id) {
        PartSupplier supplier = currentSupplier();
        return Result.ok(supplierBizService.cancel(supplier.getId(), id));
    }

    @Operation(summary = "首页统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        PartSupplier supplier = currentSupplier();
        List<SupplierDelivery> all = deliveryService.list(new LambdaQueryWrapper<SupplierDelivery>()
                .eq(SupplierDelivery::getSupplierId, supplier.getId()));
        long pending = all.stream().filter(d -> SupplierBizService.STATUS_PENDING.equals(d.getStatus())).count();
        long stored = all.stream().filter(d -> SupplierBizService.STATUS_STORED.equals(d.getStatus())).count();
        BigDecimal storedAmount = all.stream()
                .filter(d -> SupplierBizService.STATUS_STORED.equals(d.getStatus()))
                .map(d -> d.getTotalAmount() == null ? BigDecimal.ZERO : d.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, Object> data = new HashMap<>();
        data.put("total", all.size());
        data.put("pending", pending);
        data.put("stored", stored);
        data.put("storedAmount", storedAmount);
        data.put("partCount", partInfoService.count(new LambdaQueryWrapper<PartInfo>()
                .eq(PartInfo::getSupplier, supplier.getSupplierName())));
        return Result.ok(data);
    }

    @Operation(summary = "对账单（按月汇总已入库送货单）")
    @GetMapping("/statements")
    public Result<Map<String, Object>> statements(@RequestParam(required = false) String month) {
        PartSupplier supplier = currentSupplier();
        String target = StrUtil.isBlank(month) ? DateUtil.format(new Date(), "yyyy-MM") : month;
        LocalDateTime begin = LocalDate.parse(target + "-01").atStartOfDay();
        LocalDateTime end = begin.plusMonths(1);
        List<SupplierDelivery> list = deliveryService.list(new LambdaQueryWrapper<SupplierDelivery>()
                .eq(SupplierDelivery::getSupplierId, supplier.getId())
                .eq(SupplierDelivery::getStatus, SupplierBizService.STATUS_STORED)
                .ge(SupplierDelivery::getConfirmTime, begin)
                .lt(SupplierDelivery::getConfirmTime, end)
                .orderByAsc(SupplierDelivery::getConfirmTime));
        BigDecimal total = list.stream()
                .map(d -> d.getTotalAmount() == null ? BigDecimal.ZERO : d.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, Object> data = new HashMap<>();
        data.put("month", target);
        data.put("count", list.size());
        data.put("total", total);
        data.put("list", list);
        return Result.ok(data);
    }

    private PartSupplier currentSupplier() {
        PartSupplier supplier = supplierService.getOne(new LambdaQueryWrapper<PartSupplier>()
                .eq(PartSupplier::getUserId, UserContext.require().getUserId()).last("limit 1"));
        if (supplier == null) {
            throw new BusinessException("当前账号未绑定供应商档案");
        }
        return supplier;
    }
}
