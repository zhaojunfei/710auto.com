package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.WechatReplyRule;
import com.auto710.service.WechatReplyRuleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】公众号回复规则 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "公众号回复规则管理")
@RestController
@RequestMapping("/api/admin/wechat-reply-rule")
public class WechatReplyRuleController extends BaseController<WechatReplyRuleService, WechatReplyRule> {

    public WechatReplyRuleController(WechatReplyRuleService service) {
        super(service);
    }
}
