package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.WechatTemplateMsg;
import com.auto710.service.WechatTemplateMsgService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】模板消息配置 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "模板消息配置管理")
@RestController
@RequestMapping("/api/admin/wechat-template-msg")
public class WechatTemplateMsgController extends BaseController<WechatTemplateMsgService, WechatTemplateMsg> {

    public WechatTemplateMsgController(WechatTemplateMsgService service) {
        super(service);
    }
}
