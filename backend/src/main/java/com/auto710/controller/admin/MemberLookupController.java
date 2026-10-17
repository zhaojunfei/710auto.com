package com.auto710.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.auto710.biz.MemberBizService;
import com.auto710.common.api.Result;
import com.auto710.common.util.DesensitizeUtil;
import com.auto710.common.util.EncryptUtil;
import com.auto710.entity.Member;
import com.auto710.service.MemberService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 【后台】手机号即会员 —— 手机号查询 / 自动建档
 * <p>所有后台录入手机号的入口（开单/预约/存胎/会员）统一走这里，
 * 保证「输号 → 自动匹配会员 → 不存在则自动创建」。</p>
 */
@Tag(name = "手机号会员查询")
@RestController
@RequestMapping("/api/admin/member-lookup")
@RequiredArgsConstructor
public class MemberLookupController {

    private final MemberService memberService;
    private final MemberBizService memberBizService;

    @Operation(summary = "按手机号查询会员（只读，不创建）")
    @GetMapping("/find")
    public Result<Map<String, Object>> find(@RequestParam String phone) {
        if (StrUtil.isBlank(phone)) {
            return Result.ok(null);
        }
        Member member = memberService.getOne(new LambdaQueryWrapper<Member>()
                .eq(Member::getPhone, EncryptUtil.encrypt(phone.trim())).last("limit 1"));
        return Result.ok(member == null ? null : brief(member));
    }

    @Operation(summary = "按手机号查询会员，不存在则自动创建")
    @PostMapping("/ensure")
    public Result<Map<String, Object>> ensure(@RequestBody Map<String, String> body) {
        Member member = memberBizService.findOrCreateByPhone(body.get("phone"), body.get("realName"));
        return Result.ok(member == null ? null : brief(member));
    }

    private Map<String, Object> brief(Member member) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", member.getId());
        m.put("nickname", member.getNickname());
        m.put("realName", member.getRealName());
        m.put("phone", DesensitizeUtil.phone(member.getPhone()));
        m.put("points", member.getPoints());
        m.put("balance", member.getBalance());
        m.put("levelId", member.getLevelId());
        m.put("status", member.getStatus());
        return m;
    }
}
