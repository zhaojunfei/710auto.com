package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.SysUserRole;
import com.auto710.service.SysUserRoleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】用户角色关联 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "用户角色关联管理")
@RestController
@RequestMapping("/api/admin/sys-user-role")
public class SysUserRoleController extends BaseController<SysUserRoleService, SysUserRole> {

    public SysUserRoleController(SysUserRoleService service) {
        super(service);
    }
}
