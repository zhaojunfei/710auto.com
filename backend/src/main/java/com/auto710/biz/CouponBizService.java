package com.auto710.biz;

import cn.hutool.core.util.IdUtil;
import com.auto710.common.exception.BusinessException;
import com.auto710.entity.Coupon;
import com.auto710.entity.MemberCoupon;
import com.auto710.service.CouponService;
import com.auto710.service.MemberCouponService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 优惠券业务服务：创建、领取、使用、过期
 */
@Service
@RequiredArgsConstructor
public class CouponBizService {

    private final CouponService couponService;
    private final MemberCouponService memberCouponService;

    /**
     * 领取优惠券
     */
    @Transactional(rollbackFor = Exception.class)
    public MemberCoupon receive(Long memberId, Long couponId) {
        Coupon coupon = couponService.getById(couponId);
        if (coupon == null || coupon.getStatus() == null || coupon.getStatus() != 1) {
            throw new BusinessException("优惠券不存在或已停发");
        }
        if (coupon.getTotalCount() != null && coupon.getTotalCount() > 0
                && coupon.getReceivedCount() != null && coupon.getReceivedCount() >= coupon.getTotalCount()) {
            throw new BusinessException("优惠券已被领完");
        }
        long owned = memberCouponService.count(new LambdaQueryWrapper<MemberCoupon>()
                .eq(MemberCoupon::getMemberId, memberId)
                .eq(MemberCoupon::getCouponId, couponId));
        if (owned > 0) {
            throw new BusinessException("您已领取过该优惠券");
        }

        MemberCoupon memberCoupon = new MemberCoupon();
        memberCoupon.setMemberId(memberId);
        memberCoupon.setCouponId(couponId);
        memberCoupon.setCouponCode(IdUtil.fastSimpleUUID().toUpperCase());
        memberCoupon.setStatus("未使用");
        memberCoupon.setReceiveTime(LocalDateTime.now());
        memberCoupon.setExpireTime(resolveExpireTime(coupon));
        memberCouponService.save(memberCoupon);

        coupon.setReceivedCount((coupon.getReceivedCount() == null ? 0 : coupon.getReceivedCount()) + 1);
        couponService.updateById(coupon);
        return memberCoupon;
    }

    /**
     * 使用优惠券
     */
    public MemberCoupon use(Long memberCouponId, String orderNo) {
        MemberCoupon memberCoupon = memberCouponService.getById(memberCouponId);
        if (memberCoupon == null) {
            throw new BusinessException("优惠券不存在");
        }
        if (!"未使用".equals(memberCoupon.getStatus())) {
            throw new BusinessException("优惠券不可用");
        }
        if (memberCoupon.getExpireTime() != null && memberCoupon.getExpireTime().isBefore(LocalDateTime.now())) {
            memberCoupon.setStatus("已过期");
            memberCouponService.updateById(memberCoupon);
            throw new BusinessException("优惠券已过期");
        }
        memberCoupon.setStatus("已使用");
        memberCoupon.setBizNo(orderNo);
        memberCoupon.setUseTime(LocalDateTime.now());
        memberCouponService.updateById(memberCoupon);

        Coupon coupon = couponService.getById(memberCoupon.getCouponId());
        if (coupon != null) {
            coupon.setUsedCount((coupon.getUsedCount() == null ? 0 : coupon.getUsedCount()) + 1);
            couponService.updateById(coupon);
        }
        return memberCoupon;
    }

    public List<MemberCoupon> myCoupons(Long memberId, String status) {
        return memberCouponService.list(new LambdaQueryWrapper<MemberCoupon>()
                .eq(MemberCoupon::getMemberId, memberId)
                .eq(status != null, MemberCoupon::getStatus, status)
                .orderByDesc(MemberCoupon::getReceiveTime));
    }

    /**
     * 过期扫描（定时任务调用）
     */
    public int expireScan() {
        List<MemberCoupon> expired = memberCouponService.list(new LambdaQueryWrapper<MemberCoupon>()
                .eq(MemberCoupon::getStatus, "未使用")
                .lt(MemberCoupon::getExpireTime, LocalDateTime.now()));
        expired.forEach(c -> c.setStatus("已过期"));
        if (!expired.isEmpty()) {
            memberCouponService.updateBatchById(expired);
        }
        return expired.size();
    }

    private LocalDateTime resolveExpireTime(Coupon coupon) {
        if ("领取后N天".equals(coupon.getValidType()) && coupon.getValidDays() != null && coupon.getValidDays() > 0) {
            return LocalDateTime.now().plusDays(coupon.getValidDays());
        }
        return coupon.getEndTime();
    }
}
