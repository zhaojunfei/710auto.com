package com.auto710.controller.admin;

import com.auto710.common.annotation.AuditLog;
import com.auto710.common.annotation.RequiresPermission;
import com.auto710.common.api.Result;
import com.auto710.common.exception.BusinessException;
import com.auto710.entity.SysMenu;
import com.auto710.entity.SysRoleMenu;
import com.auto710.service.SysMenuService;
import com.auto710.service.SysRoleMenuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 【后台】后台菜单（权限）管理
 * <p>维护 sys_menu 菜单树：目录(DIR) / 菜单(MENU) / 按钮(BUTTON)，含权限标识(perms)。
 * 与生成的 SysMenuController(通用CRUD) 并存：本控制器额外提供树形查询与级联删除。</p>
 */
@Tag(name = "后台菜单管理")
@RestController
@RequestMapping("/api/admin/menu")
public class MenuManageController {

    private static final Set<String> TYPES = Set.of("DIR", "MENU", "BUTTON");

    private final SysMenuService menuService;
    private final SysRoleMenuService roleMenuService;

    public MenuManageController(SysMenuService menuService, SysRoleMenuService roleMenuService) {
        this.menuService = menuService;
        this.roleMenuService = roleMenuService;
    }

    @Operation(summary = "菜单树（含按钮权限）")
    @GetMapping("/tree")
    public Result<List<Map<String, Object>>> tree() {
        List<SysMenu> all = menuService.list(new LambdaQueryWrapper<SysMenu>()
                .orderByAsc(SysMenu::getSort)
                .orderByAsc(SysMenu::getId));
        return Result.ok(buildTree(all));
    }

    @Operation(summary = "新增菜单")
    @PostMapping
    @RequiresPermission("system:menu:manage")
    @AuditLog(module = "系统菜单", operation = "新增菜单")
    public Result<Long> save(@RequestBody SysMenu menu) {
        validate(menu);
        menu.setId(null);
        if (menu.getParentId() == null) {
            menu.setParentId(0L);
        }
        menuService.save(menu);
        return Result.ok(menu.getId());
    }

    @Operation(summary = "修改菜单")
    @PutMapping
    @RequiresPermission("system:menu:manage")
    @AuditLog(module = "系统菜单", operation = "修改菜单")
    public Result<Boolean> update(@RequestBody SysMenu menu) {
        if (menu.getId() == null) {
            throw new BusinessException("菜单ID不能为空");
        }
        validate(menu);
        if (Objects.equals(menu.getId(), menu.getParentId())) {
            throw new BusinessException("上级菜单不能是自己");
        }
        if (menu.getParentId() != null && menu.getParentId() != 0
                && collectSelfAndDescendants(menu.getId()).contains(menu.getParentId())) {
            throw new BusinessException("上级菜单不能是自己的子级");
        }
        return Result.ok(menuService.updateById(menu));
    }

    @Operation(summary = "删除菜单（级联子菜单与角色授权）")
    @DeleteMapping("/{id}")
    @RequiresPermission("system:menu:manage")
    @AuditLog(module = "系统菜单", operation = "删除菜单")
    public Result<Boolean> remove(@PathVariable Long id) {
        List<Long> ids = collectSelfAndDescendants(id);
        roleMenuService.remove(new LambdaQueryWrapper<SysRoleMenu>().in(SysRoleMenu::getMenuId, ids));
        menuService.removeByIds(ids);
        return Result.ok(true);
    }

    /* ---------------- 内部方法 ---------------- */

    private void validate(SysMenu menu) {
        if (menu == null || !StringUtils.hasText(menu.getMenuName())) {
            throw new BusinessException("菜单名称不能为空");
        }
        String type = menu.getMenuType();
        if (!StringUtils.hasText(type)) {
            menu.setMenuType("MENU");
        } else if (!TYPES.contains(type)) {
            throw new BusinessException("菜单类型只能是 DIR / MENU / BUTTON");
        }
        if ("MENU".equals(menu.getMenuType()) && !StringUtils.hasText(menu.getPath())) {
            throw new BusinessException("菜单(页面)必须填写路由地址");
        }
        if (menu.getSort() == null) {
            menu.setSort(0);
        }
        if (menu.getVisible() == null) {
            menu.setVisible(1);
        }
        if (menu.getStatus() == null) {
            menu.setStatus(1);
        }
        menu.setMenuName(menu.getMenuName().trim());
    }

    private List<Long> collectSelfAndDescendants(Long id) {
        List<SysMenu> all = menuService.list(new LambdaQueryWrapper<SysMenu>()
                .select(SysMenu::getId, SysMenu::getParentId));
        List<Long> result = new ArrayList<>();
        result.add(id);
        boolean changed = true;
        while (changed) {
            changed = false;
            for (SysMenu m : all) {
                if (m.getParentId() != null && result.contains(m.getParentId())
                        && !result.contains(m.getId())) {
                    result.add(m.getId());
                    changed = true;
                }
            }
        }
        return result;
    }

    private List<Map<String, Object>> buildTree(List<SysMenu> menus) {
        Map<Long, Map<String, Object>> nodes = new LinkedHashMap<>();
        for (SysMenu m : menus) {
            nodes.put(m.getId(), toNode(m));
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
        Comparator<Map<String, Object>> bySort = Comparator
                .comparingInt((Map<String, Object> n) -> n.get("sort") == null ? 0 : (Integer) n.get("sort"))
                .thenComparingLong(n -> (Long) n.get("id"));
        sortRecursive(roots, bySort);
        return roots;
    }

    private void sortRecursive(List<Map<String, Object>> nodes, Comparator<Map<String, Object>> cmp) {
        nodes.sort(cmp);
        for (Map<String, Object> n : nodes) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> children = (List<Map<String, Object>>) n.get("children");
            if (children != null && !children.isEmpty()) {
                sortRecursive(children, cmp);
            }
        }
    }

    private Map<String, Object> toNode(SysMenu m) {
        Map<String, Object> node = new LinkedHashMap<>();
        node.put("id", m.getId());
        node.put("parentId", m.getParentId());
        node.put("menuName", m.getMenuName());
        node.put("menuType", m.getMenuType());
        node.put("path", m.getPath());
        node.put("component", m.getComponent());
        node.put("perms", m.getPerms());
        node.put("icon", m.getIcon());
        node.put("sort", m.getSort());
        node.put("visible", m.getVisible());
        node.put("status", m.getStatus());
        node.put("children", new ArrayList<Map<String, Object>>());
        return node;
    }
}
