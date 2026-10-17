package com.auto710.controller.admin;

import com.auto710.biz.MemberBizService;
import com.auto710.common.annotation.AuditLog;
import com.auto710.common.api.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 【后台】会员储值充值（#10）
 * <p>按手机号（或会员ID）定位会员 → 选金额 → 选充值方式 → 按充赠规则自动算到账；
 * 充值/赠送流水写入 member_balance_log，审计日志由 {@link AuditLog} 切面留痕；
 * 余额落库后小程序 / H5 实时同步。</p>
 */
@Tag(name = "会员充值")
@RestController
@RequestMapping("/api/admin/member-recharge")
@RequiredArgsConstructor
public class AdminMemberRechargeController {

    private final MemberBizService memberBizService;

    @Operation(summary = "后台会员充值（按手机号或会员ID，自动套用充赠规则）")
    @PostMapping("/recharge")
    @AuditLog(module = "会员", operation = "后台充值")
    public Result<Map<String, Object>> recharge(@RequestParam(required = false) Long memberId,
                                                @RequestParam(required = false) String phone,
                                                @RequestParam BigDecimal amount,
                                                @RequestParam(required = false) String payType,
                                                @RequestParam(required = false) Long ruleId,
                                                @RequestParam(required = false) String remark) {
        return Result.ok(memberBizService.rechargeByAdmin(memberId, phone, amount, payType, ruleId, remark));
    }
}
