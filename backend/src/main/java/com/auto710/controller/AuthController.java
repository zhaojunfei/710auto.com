package com.auto710.controller;

import cn.hutool.crypto.digest.BCrypt;
import com.auto710.common.annotation.AuditLog;
import com.auto710.common.api.Result;
import com.auto710.common.context.LoginUser;
import com.auto710.common.context.UserContext;
import com.auto710.common.exception.BusinessException;
import com.auto710.common.util.JwtUtil;
import com.auto710.dto.LoginRequest;
import com.auto710.dto.LoginResponse;
import com.auto710.entity.SysMenu;
import com.auto710.entity.SysRole;
import com.auto710.entity.SysUser;
import com.auto710.security.RbacService;
import com.auto710.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 后台 / 技师端 登录认证
 */
@Tag(name = "登录认证")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService userService;
    private final RbacService rbacService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "账号密码登录")
    @PostMapping("/login")
    @AuditLog(module = "认证", operation = "登录")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        SysUser user = userService.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, request.getUsername()));
        if (user == null || !BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }
        rbacService.assertEnabled(user);

        List<SysRole> roles = rbacService.getRoles(user.getId());
        boolean superAdmin = rbacService.isSuperAdmin(roles);
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setRealName(user.getRealName());
        loginUser.setUserType("ADMIN");
        loginUser.setStoreId(user.getStoreId());
        loginUser.setSuperAdmin(superAdmin);
        loginUser.setRoles(rbacService.roleCodes(roles));
        loginUser.setPermissions(rbacService.getPermissions(user.getId(), superAdmin));

        String token = jwtUtil.createToken(loginUser);

        user.setLastLoginTime(LocalDateTime.now());
        userService.updateById(user);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setAvatar(user.getAvatar());
        response.setUserType("ADMIN");
        response.setStoreId(user.getStoreId());
        response.setSuperAdmin(superAdmin);
        response.setRoles(loginUser.getRoles());
        response.setPermissions(loginUser.getPermissions());
        return Result.ok(response);
    }

    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/info")
    public Result<LoginResponse> info() {
        LoginUser current = UserContext.require();
        SysUser user = userService.getById(current.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        List<SysRole> roles = rbacService.getRoles(user.getId());
        boolean superAdmin = rbacService.isSuperAdmin(roles);
        List<SysMenu> menus = rbacService.getMenus(user.getId(), superAdmin);

        LoginResponse response = new LoginResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setAvatar(user.getAvatar());
        response.setUserType("ADMIN");
        response.setStoreId(user.getStoreId());
        response.setSuperAdmin(superAdmin);
        response.setRoles(rbacService.roleCodes(roles));
        response.setPermissions(rbacService.getPermissions(user.getId(), superAdmin));
        response.setMenus(rbacService.buildMenuTree(menus));
        return Result.ok(response);
    }

    @Operation(summary = "获取当前用户菜单")
    @GetMapping("/menus")
    public Result<List<Map<String, Object>>> menus() {
        LoginUser current = UserContext.require();
        List<SysMenu> menus = rbacService.getMenus(current.getUserId(), current.isSuperAdmin());
        return Result.ok(rbacService.buildMenuTree(menus));
    }

    @Operation(summary = "修改密码")
    @PostMapping("/password")
    @AuditLog(module = "认证", operation = "修改密码")
    public Result<Boolean> changePassword(@RequestBody Map<String, String> body) {
        LoginUser current = UserContext.require();
        SysUser user = userService.getById(current.getUserId());
        if (user == null || !BCrypt.checkpw(body.get("oldPassword"), user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        user.setPassword(BCrypt.hashpw(body.get("newPassword")));
        return Result.ok(userService.updateById(user));
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        UserContext.clear();
        return Result.ok();
    }
}
