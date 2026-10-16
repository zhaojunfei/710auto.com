package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.SysRoleMenu;
import com.auto710.service.SysRoleMenuService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】角色菜单关联 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "角色菜单关联管理")
@RestController
@RequestMapping("/api/admin/sys-role-menu")
public class SysRoleMenuController extends BaseController<SysRoleMenuService, SysRoleMenu> {

    public SysRoleMenuController(SysRoleMenuService service) {
        super(service);
    }
}
