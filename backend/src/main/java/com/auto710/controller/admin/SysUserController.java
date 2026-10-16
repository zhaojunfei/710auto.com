package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.SysUser;
import com.auto710.service.SysUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】系统用户 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "系统用户管理")
@RestController
@RequestMapping("/api/admin/sys-user")
public class SysUserController extends BaseController<SysUserService, SysUser> {

    public SysUserController(SysUserService service) {
        super(service);
    }
}
