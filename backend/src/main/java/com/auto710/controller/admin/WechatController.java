package com.auto710.controller.admin;

import com.auto710.biz.CouponBizService;
import com.auto710.common.api.Result;
import com.auto710.entity.MemberCoupon;
import com.auto710.wechat.WechatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 【后台】公众号相关操作 + 优惠券辅助
 */
@Tag(name = "公众号管理")
@RestController
@RequestMapping("/api/admin/wechat")
@RequiredArgsConstructor
public class WechatController {

    private final WechatService wechatService;
    private final CouponBizService couponBizService;

    @Operation(summary = "构建微信自定义菜单结构")
    @GetMapping("/menu-json")
    public Result<Map<String, Object>> menuJson() {
        return Result.ok(wechatService.buildMenuJson());
    }

    @Operation(summary = "同步菜单到微信")
    @PostMapping("/sync-menu")
    public Result<Map<String, Object>> syncMenu() {
        return Result.ok(wechatService.syncMenu());
    }

    @Operation(summary = "关键词自动回复匹配测试")
    @GetMapping("/reply")
    public Result<Object> reply(@RequestParam String keyword) {
        return Result.ok(wechatService.matchReply(keyword));
    }

    @Operation(summary = "优惠券过期扫描")
    @PostMapping("/coupon/expire-scan")
    public Result<Integer> expireScan() {
        return Result.ok(couponBizService.expireScan());
    }

    @Operation(summary = "优惠券使用")
    @PostMapping("/coupon/use")
    public Result<MemberCoupon> useCoupon(@RequestParam Long memberCouponId,
                                          @RequestParam(required = false) String orderNo) {
        return Result.ok(couponBizService.use(memberCouponId, orderNo));
    }
}
