package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.SysMenu;
import com.auto710.service.SysMenuService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】系统菜单权限 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "系统菜单权限管理")
@RestController
@RequestMapping("/api/admin/sys-menu")
public class SysMenuController extends BaseController<SysMenuService, SysMenu> {

    public SysMenuController(SysMenuService service) {
        super(service);
    }
}
