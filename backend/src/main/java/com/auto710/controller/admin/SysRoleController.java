package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.SysRole;
import com.auto710.service.SysRoleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】系统角色 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "系统角色管理")
@RestController
@RequestMapping("/api/admin/sys-role")
public class SysRoleController extends BaseController<SysRoleService, SysRole> {

    public SysRoleController(SysRoleService service) {
        super(service);
    }
}
