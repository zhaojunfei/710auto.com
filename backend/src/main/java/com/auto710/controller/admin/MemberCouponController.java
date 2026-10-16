package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.MemberCoupon;
import com.auto710.service.MemberCouponService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】会员优惠券 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "会员优惠券管理")
@RestController
@RequestMapping("/api/admin/member-coupon")
public class MemberCouponController extends BaseController<MemberCouponService, MemberCoupon> {

    public MemberCouponController(MemberCouponService service) {
        super(service);
    }
}
