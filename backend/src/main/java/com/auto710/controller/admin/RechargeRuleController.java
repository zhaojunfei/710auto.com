package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.RechargeRule;
import com.auto710.service.RechargeRuleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】储值充赠规则 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "储值充赠规则管理")
@RestController
@RequestMapping("/api/admin/recharge-rule")
public class RechargeRuleController extends BaseController<RechargeRuleService, RechargeRule> {

    public RechargeRuleController(RechargeRuleService service) {
        super(service);
    }
}
