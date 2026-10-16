package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.WechatMaterial;
import com.auto710.service.WechatMaterialService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】公众号素材 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "公众号素材管理")
@RestController
@RequestMapping("/api/admin/wechat-material")
public class WechatMaterialController extends BaseController<WechatMaterialService, WechatMaterial> {

    public WechatMaterialController(WechatMaterialService service) {
        super(service);
    }
}
