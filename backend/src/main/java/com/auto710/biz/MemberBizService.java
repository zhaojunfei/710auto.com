package com.auto710.biz;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.auto710.common.context.LoginUser;
import com.auto710.common.exception.BusinessException;
import com.auto710.common.util.DesensitizeUtil;
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

    /**
     * 手机号即会员：按手机号查找会员，不存在则自动创建。
     * <p>新会员：用户名=手机号，初始密码=手机号后 6 位（BCrypt），首次登录引导改密。</p>
     *
     * @param phone    明文手机号
     * @param realName 姓名（可空）
     * @return 已存在或新建的会员；手机号为空时返回 null
     */
    @Transactional(rollbackFor = Exception.class)
    public Member findOrCreateByPhone(String phone, String realName) {
        if (StrUtil.isBlank(phone)) {
            return null;
        }
        String plain = phone.trim();
        String enc = EncryptUtil.isEncrypted(plain) ? plain : EncryptUtil.encrypt(plain);
        Member member = memberService.getOne(new LambdaQueryWrapper<Member>()
                .eq(Member::getPhone, enc).last("limit 1"));
        if (member != null) {
            if (StrUtil.isNotBlank(realName) && StrUtil.isBlank(member.getRealName())) {
                member.setRealName(realName);
                memberService.updateById(member);
            }
            return member;
        }
        member = new Member();
        member.setNickname(StrUtil.isBlank(realName) ? plain : realName);
        member.setRealName(realName);
        member.setPhone(enc);
        member.setPassword(BCrypt.hashpw(last6(plain)));
        member.setNeedChangePwd(1);
        member.setPoints(0);
        member.setBalance(BigDecimal.ZERO);
        member.setSource("admin");
        member.setStatus(1);
        member.setRemark("系统按手机号自动创建，初始密码为手机号后6位");
        memberService.save(member);
        return member;
    }

    /**
     * 会员端手机号 + 密码登录（用户名=手机号，初始密码=手机号后 6 位）
     */
    public Map<String, Object> loginByPhone(String phone, String password) {
        if (StrUtil.isBlank(phone) || StrUtil.isBlank(password)) {
            throw new BusinessException("请输入手机号和密码");
        }
        Member member = memberService.getOne(new LambdaQueryWrapper<Member>()
                .eq(Member::getPhone, EncryptUtil.encrypt(phone.trim())).last("limit 1"));
        if (member == null || StrUtil.isBlank(member.getPassword())
                || !BCrypt.checkpw(password, member.getPassword())) {
            throw new BusinessException("手机号或密码错误");
        }
        if (member.getStatus() != null && member.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(member.getId());
        loginUser.setUsername(StrUtil.blankToDefault(member.getNickname(), phone));
        loginUser.setUserType("MEMBER");
        Map<String, Object> result = new HashMap<>();
        result.put("token", jwtUtil.createMemberToken(loginUser));
        result.put("member", member);
        result.put("needChangePwd", member.getNeedChangePwd() != null && member.getNeedChangePwd() == 1);
        return result;
    }

    /**
     * 会员修改密码（改密后清除「首次登录需改密」标记）
     */
    public boolean changePassword(Long memberId, String oldPassword, String newPassword) {
        Member member = profile(memberId);
        if (StrUtil.isNotBlank(member.getPassword())
                && !BCrypt.checkpw(oldPassword == null ? "" : oldPassword, member.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        if (StrUtil.isBlank(newPassword) || newPassword.length() < 6) {
            throw new BusinessException("新密码至少 6 位");
        }
        member.setPassword(BCrypt.hashpw(newPassword));
        member.setNeedChangePwd(0);
        memberService.updateById(member);
        return true;
    }

    private String last6(String phone) {
        return phone.length() <= 6 ? phone : phone.substring(phone.length() - 6);
    }

    public Member profile(Long memberId) {        Member member = memberService.getById(memberId);
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

    /**
     * 后台会员充值（#10）：按手机号/会员ID 定位会员 → 自动套用充赠规则 → 写充值/赠送流水与审计留痕。
     * 余额直接落库，小程序 / H5 读取同一 member 记录，天然跨端同步。
     *
     * @param memberId 会员ID（与 phone 二选一）
     * @param phone    手机号（与 memberId 二选一）
     * @param amount   充值金额
     * @param payType  充值方式（现金 / 微信 / 支付宝 / 记账 …，来自数据字典 pay_type）
     * @param ruleId   充赠规则ID（可空：按金额自动匹配最优规则）
     * @param remark   备注
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> rechargeByAdmin(Long memberId, String phone, BigDecimal amount,
                                               String payType, Long ruleId, String remark) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("充值金额不正确");
        }
        Member member = null;
        if (memberId != null) {
            member = memberService.getById(memberId);
        } else if (StrUtil.isNotBlank(phone)) {
            String plain = phone.trim();
            String enc = EncryptUtil.isEncrypted(plain) ? plain : EncryptUtil.encrypt(plain);
            member = memberService.getOne(new LambdaQueryWrapper<Member>()
                    .eq(Member::getPhone, enc).last("limit 1"));
        }
        if (member == null) {
            throw new BusinessException("该手机号暂无会员，请先在「会员管理」建档后充值");
        }
        if (member.getStatus() != null && member.getStatus() == 0) {
            throw new BusinessException("会员已被禁用，无法充值");
        }

        RechargeRule rule;
        if (ruleId != null) {
            rule = rechargeRuleService.getById(ruleId);
        } else {
            // 自动匹配：启用中、充值门槛 ≤ 本次金额、门槛最高的一条
            rule = rechargeRuleService.getOne(new LambdaQueryWrapper<RechargeRule>()
                    .eq(RechargeRule::getStatus, 1)
                    .le(RechargeRule::getRechargeAmount, amount)
                    .orderByDesc(RechargeRule::getRechargeAmount)
                    .last("limit 1"));
        }
        BigDecimal gift = (rule != null && rule.getGiftAmount() != null) ? rule.getGiftAmount() : BigDecimal.ZERO;
        int giftPoints = (rule != null && rule.getGiftPoints() != null) ? rule.getGiftPoints() : 0;

        BigDecimal total = amount.add(gift);
        BigDecimal balance = (member.getBalance() == null ? BigDecimal.ZERO : member.getBalance()).add(total);
        member.setBalance(balance);
        member.setPoints((member.getPoints() == null ? 0 : member.getPoints()) + giftPoints);
        memberService.updateById(member);

        String way = StrUtil.blankToDefault(payType, "现金");
        String base = "后台充值（" + way + "）" + (StrUtil.isNotBlank(remark) ? "：" + remark : "");
        saveBalanceLog(member.getId(), amount, balance, "充值", null, base);
        if (gift.compareTo(BigDecimal.ZERO) > 0) {
            saveBalanceLog(member.getId(), gift, balance, "赠送", null,
                    "充值赠送" + (rule != null ? "（" + rule.getRuleName() + "）" : ""));
        }
        if (giftPoints > 0) {
            MemberPointsLog pointsLog = new MemberPointsLog();
            pointsLog.setMemberId(member.getId());
            pointsLog.setChangePoints(giftPoints);
            pointsLog.setBalancePoints(member.getPoints());
            pointsLog.setType("获取");
            pointsLog.setSource("后台充值赠送积分");
            pointsLogService.save(pointsLog);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("memberId", member.getId());
        result.put("realName", member.getRealName());
        result.put("nickname", member.getNickname());
        result.put("phone", DesensitizeUtil.phone(member.getPhone()));
        result.put("amount", amount);
        result.put("giftAmount", gift);
        result.put("giftPoints", giftPoints);
        result.put("rechargeRuleId", rule == null ? null : rule.getId());
        result.put("rechargeRuleName", rule == null ? null : rule.getRuleName());
        result.put("payType", way);
        result.put("balance", member.getBalance());
        result.put("points", member.getPoints());
        return result;
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
