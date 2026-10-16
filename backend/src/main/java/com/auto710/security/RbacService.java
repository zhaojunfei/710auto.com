package com.auto710.security;

import com.auto710.entity.SysMenu;
import com.auto710.entity.SysRole;
import com.auto710.entity.SysRoleMenu;
import com.auto710.entity.SysUser;
import com.auto710.entity.SysUserRole;
import com.auto710.service.SysMenuService;
import com.auto710.service.SysRoleMenuService;
import com.auto710.service.SysRoleService;
import com.auto710.service.SysUserRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RBAC 权限服务：加载角色、权限、菜单
 */
@Service
@RequiredArgsConstructor
public class RbacService {

    public static final String SUPER_ADMIN = "SUPER_ADMIN";

    private final SysRoleService roleService;
    private final SysUserRoleService userRoleService;
    private final SysRoleMenuService roleMenuService;
    private final SysMenuService menuService;

    /**
     * 查询用户的角色
     */
    public List<SysRole> getRoles(Long userId) {
        List<SysUserRole> relations = userRoleService.list(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        if (relations.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> roleIds = relations.stream().map(SysUserRole::getRoleId).toList();
        return roleService.listByIds(roleIds);
    }

    /**
     * 查询用户可见菜单
     */
    public List<SysMenu> getMenus(Long userId, boolean superAdmin) {
        if (superAdmin) {
            return menuService.list(new LambdaQueryWrapper<SysMenu>()
                    .in(SysMenu::getMenuType, List.of("DIR", "MENU"))
                    .eq(SysMenu::getStatus, 1)
                    .orderByAsc(SysMenu::getSort));
        }
        List<SysRole> roles = getRoles(userId);
        if (roles.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> roleIds = roles.stream().map(SysRole::getId).toList();
        List<Long> menuIds = roleMenuService.list(
                        new LambdaQueryWrapper<SysRoleMenu>().in(SysRoleMenu::getRoleId, roleIds))
                .stream().map(SysRoleMenu::getMenuId).distinct().toList();
        if (menuIds.isEmpty()) {
            return Collections.emptyList();
        }
        return menuService.list(new LambdaQueryWrapper<SysMenu>()
                .in(SysMenu::getId, menuIds)
                .in(SysMenu::getMenuType, List.of("DIR", "MENU"))
                .eq(SysMenu::getStatus, 1)
                .orderByAsc(SysMenu::getSort));
    }

    /**
     * 查询用户权限标识
     */
    public List<String> getPermissions(Long userId, boolean superAdmin) {
        List<SysMenu> menus;
        if (superAdmin) {
            menus = menuService.list(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getStatus, 1));
        } else {
            List<SysRole> roles = getRoles(userId);
            if (roles.isEmpty()) {
                return Collections.emptyList();
            }
            List<Long> roleIds = roles.stream().map(SysRole::getId).toList();
            List<Long> menuIds = roleMenuService.list(
                            new LambdaQueryWrapper<SysRoleMenu>().in(SysRoleMenu::getRoleId, roleIds))
                    .stream().map(SysRoleMenu::getMenuId).distinct().toList();
            if (menuIds.isEmpty()) {
                return Collections.emptyList();
            }
            menus = menuService.listByIds(menuIds);
        }
        return menus.stream()
                .map(SysMenu::getPerms)
                .filter(p -> p != null && !p.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 组装前端菜单树
     */
    public List<Map<String, Object>> buildMenuTree(List<SysMenu> menus) {
        Map<Long, Map<String, Object>> nodes = new LinkedHashMap<>();
        for (SysMenu m : menus) {
            Map<String, Object> node = new LinkedHashMap<>();
            node.put("id", m.getId());
            node.put("parentId", m.getParentId());
            node.put("name", m.getMenuName());
            node.put("path", m.getPath());
            node.put("component", m.getComponent());
            node.put("icon", m.getIcon());
            node.put("perms", m.getPerms());
            node.put("type", m.getMenuType());
            node.put("sort", m.getSort());
            node.put("children", new ArrayList<Map<String, Object>>());
            nodes.put(m.getId(), node);
        }
        List<Map<String, Object>> roots = new ArrayList<>();
        for (Map<String, Object> node : nodes.values()) {
            Long parentId = (Long) node.get("parentId");
            Map<String, Object> parent = parentId == null ? null : nodes.get(parentId);
            if (parent != null) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> children = (List<Map<String, Object>>) parent.get("children");
                children.add(node);
            } else {
                roots.add(node);
            }
        }
        return roots;
    }

    public boolean isSuperAdmin(List<SysRole> roles) {
        return roles.stream().anyMatch(r -> SUPER_ADMIN.equalsIgnoreCase(r.getRoleCode()));
    }

    public List<String> roleCodes(List<SysRole> roles) {
        return roles.stream().map(SysRole::getRoleCode).collect(Collectors.toList());
    }

    /**
     * 校验系统用户是否停用
     */
    public void assertEnabled(SysUser user) {
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            throw new com.auto710.common.exception.BusinessException("账号不存在或已被禁用");
        }
    }
}
