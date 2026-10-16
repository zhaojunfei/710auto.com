package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.SysConfig;
import com.auto710.service.SysConfigService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】系统全局配置 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "系统全局配置管理")
@RestController
@RequestMapping("/api/admin/sys-config")
public class SysConfigController extends BaseController<SysConfigService, SysConfig> {

    public SysConfigController(SysConfigService service) {
        super(service);
    }
}
