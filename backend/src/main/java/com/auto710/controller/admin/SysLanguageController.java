package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.SysLanguage;
import com.auto710.service.SysLanguageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】语言配置 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "语言配置管理")
@RestController
@RequestMapping("/api/admin/sys-language")
public class SysLanguageController extends BaseController<SysLanguageService, SysLanguage> {

    public SysLanguageController(SysLanguageService service) {
        super(service);
    }
}
