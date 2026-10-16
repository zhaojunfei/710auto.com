package com.auto710.biz;

import cn.hutool.core.util.StrUtil;
import com.auto710.common.context.LoginUser;
import com.auto710.common.exception.BusinessException;
import com.auto710.common.util.EncryptUtil;
import com.auto710.common.util.JwtUtil;
import com.auto710.dto.WxLoginRequest;
import com.auto710.entity.*;
import com.auto710.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员业务服务：微信一键授权登录、UnionID 跨端互通、车辆管理、积分/储值
 */
@Service
@RequiredArgsConstructor
public class MemberBizService {

    private final MemberService memberService;
    private final MemberVehicleService vehicleService;
    private final MemberBalanceLogService balanceLogService;
    private final MemberPointsLogService pointsLogService;
    private final RechargeRuleService rechargeRuleService;
    private final JwtUtil jwtUtil;

    /**
     * 微信登录（公众号 / 小程序 / H5 共用 UnionID 会员体系）
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> wxLogin(WxLoginRequest request) {
        Member member = null;
        if (StrUtil.isNotBlank(request.getUnionid())) {
            member = memberService.getOne(new LambdaQueryWrapper<Member>()
                    .eq(Member::getUnionid, request.getUnionid()).last("limit 1"));
        }
        if (member == null && StrUtil.isNotBlank(request.getOpenid())) {
            member = memberService.getOne(new LambdaQueryWrapper<Member>()
                    .eq(Member::getOpenid, request.getOpenid()).last("limit 1"));
        }
        if (member == null) {
            member = new Member();
            member.setOpenid(request.getOpenid());
            member.setUnionid(request.getUnionid());
            member.setNickname(StrUtil.blankToDefault(request.getNickname(), "微信用户"));
            member.setAvatar(request.getAvatar());
            member.setSource(StrUtil.blankToDefault(request.getSource(), "miniapp"));
            member.setPoints(0);
            member.setBalance(BigDecimal.ZERO);
            member.setStatus(1);
            memberService.save(member);
        } else {
            // 补全 UnionID / 头像昵称
            if (StrUtil.isBlank(member.getUnionid()) && StrUtil.isNotBlank(request.getUnionid())) {
                member.setUnionid(request.getUnionid());
            }
            if (StrUtil.isNotBlank(request.getNickname())) {
                member.setNickname(request.getNickname());
            }
            if (StrUtil.isNotBlank(request.getAvatar())) {
                member.setAvatar(request.getAvatar());
            }
            memberService.updateById(member);
        }
        if (member.getStatus() != null && member.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(member.getId());
        loginUser.setUsername(member.getNickname());
        loginUser.setUserType("MEMBER");
        String token = jwtUtil.createMemberToken(loginUser);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("member", member);
        return result;
    }

    public Member profile(Long memberId) {
        Member member = memberService.getById(memberId);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        return member;
    }

    public Member updateProfile(Long memberId, Member update) {
        Member member = profile(memberId);
        member.setNickname(update.getNickname());
        member.setAvatar(update.getAvatar());
        member.setRealName(update.getRealName());
        member.setGender(update.getGender());
        member.setBirthday(update.getBirthday());
        if (StrUtil.isNotBlank(update.getPhone())) {
            member.setPhone(EncryptUtil.encrypt(update.getPhone()));
        }
        memberService.updateById(member);
        return member;
    }

    /**
     * 车辆管理（多车绑定，车牌 / VIN 加密存储）
     */
    public MemberVehicle addVehicle(Long memberId, MemberVehicle vehicle) {
        vehicle.setId(null);
        vehicle.setMemberId(memberId);
        vehicle.setPlateNo(EncryptUtil.encrypt(vehicle.getPlateNo()));
        vehicle.setVin(EncryptUtil.encrypt(vehicle.getVin()));
        if (vehicle.getIsDefault() == null) {
            vehicle.setIsDefault(0);
        }
        if (vehicle.getIsDefault() == 1) {
            clearDefaultVehicle(memberId);
        }
        vehicleService.save(vehicle);
        return vehicle;
    }

    public List<MemberVehicle> listVehicles(Long memberId) {
        List<MemberVehicle> vehicles = vehicleService.list(new LambdaQueryWrapper<MemberVehicle>()
                .eq(MemberVehicle::getMemberId, memberId)
                .orderByDesc(MemberVehicle::getIsDefault));
        vehicles.forEach(v -> {
            v.setPlateNo(EncryptUtil.decrypt(v.getPlateNo()));
            v.setVin(EncryptUtil.decrypt(v.getVin()));
        });
        return vehicles;
    }

    public boolean deleteVehicle(Long memberId, Long vehicleId) {
        MemberVehicle vehicle = vehicleService.getById(vehicleId);
        if (vehicle == null || !vehicle.getMemberId().equals(memberId)) {
            throw new BusinessException("车辆不存在");
        }
        return vehicleService.removeById(vehicleId);
    }

    /**
     * 储值充值（支持充赠规则）
     */
    @Transactional(rollbackFor = Exception.class)
    public Member recharge(Long memberId, BigDecimal amount, Long ruleId) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("充值金额不正确");
        }
        Member member = profile(memberId);
        BigDecimal gift = BigDecimal.ZERO;
        int giftPoints = 0;
        if (ruleId != null) {
            RechargeRule rule = rechargeRuleService.getById(ruleId);
            if (rule != null) {
                gift = rule.getGiftAmount() == null ? BigDecimal.ZERO : rule.getGiftAmount();
                giftPoints = rule.getGiftPoints() == null ? 0 : rule.getGiftPoints();
            }
        }
        BigDecimal total = amount.add(gift);
        BigDecimal balance = (member.getBalance() == null ? BigDecimal.ZERO : member.getBalance()).add(total);
        member.setBalance(balance);
        member.setPoints((member.getPoints() == null ? 0 : member.getPoints()) + giftPoints);
        memberService.updateById(member);

        saveBalanceLog(memberId, amount, balance, "充值", null, "会员储值充值");
        if (gift.compareTo(BigDecimal.ZERO) > 0) {
            saveBalanceLog(memberId, gift, balance, "赠送", null, "充值赠送");
        }
        if (giftPoints > 0) {
            MemberPointsLog pointsLog = new MemberPointsLog();
            pointsLog.setMemberId(memberId);
            pointsLog.setChangePoints(giftPoints);
            pointsLog.setBalancePoints(member.getPoints());
            pointsLog.setType("获取");
            pointsLog.setSource("充值赠送积分");
            pointsLogService.save(pointsLog);
        }
        return member;
    }

    private void clearDefaultVehicle(Long memberId) {
        List<MemberVehicle> vehicles = vehicleService.list(new LambdaQueryWrapper<MemberVehicle>()
                .eq(MemberVehicle::getMemberId, memberId).eq(MemberVehicle::getIsDefault, 1));
        vehicles.forEach(v -> v.setIsDefault(0));
        if (!vehicles.isEmpty()) {
            vehicleService.updateBatchById(vehicles);
        }
    }

    private void saveBalanceLog(Long memberId, BigDecimal change, BigDecimal balance,
                                String type, String bizNo, String remark) {
        MemberBalanceLog log = new MemberBalanceLog();
        log.setMemberId(memberId);
        log.setChangeAmount(change);
        log.setBalanceAmount(balance);
        log.setType(type);
        log.setBizNo(bizNo);
        log.setRemark(remark);
        balanceLogService.save(log);
    }
}
