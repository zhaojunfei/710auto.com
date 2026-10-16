package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.SysI18nContent;
import com.auto710.service.SysI18nContentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】内容多语言字段 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "内容多语言字段管理")
@RestController
@RequestMapping("/api/admin/sys-i18n-content")
public class SysI18nContentController extends BaseController<SysI18nContentService, SysI18nContent> {

    public SysI18nContentController(SysI18nContentService service) {
        super(service);
    }
}
