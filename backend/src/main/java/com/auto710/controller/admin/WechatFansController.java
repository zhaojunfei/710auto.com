package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.WechatFans;
import com.auto710.service.WechatFansService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】公众号粉丝 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "公众号粉丝管理")
@RestController
@RequestMapping("/api/admin/wechat-fans")
public class WechatFansController extends BaseController<WechatFansService, WechatFans> {

    public WechatFansController(WechatFansService service) {
        super(service);
    }
}
