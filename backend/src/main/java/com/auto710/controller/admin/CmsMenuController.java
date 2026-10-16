package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.CmsMenu;
import com.auto710.service.CmsMenuService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】多端可视化菜单 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "多端可视化菜单管理")
@RestController
@RequestMapping("/api/admin/cms-menu")
public class CmsMenuController extends BaseController<CmsMenuService, CmsMenu> {

    public CmsMenuController(CmsMenuService service) {
        super(service);
    }
}
