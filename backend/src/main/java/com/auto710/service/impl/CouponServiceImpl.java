package com.auto710.service.impl;

import com.auto710.entity.Coupon;
import com.auto710.mapper.CouponMapper;
import com.auto710.service.CouponService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 优惠券 Service 实现
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {
}
