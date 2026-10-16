package com.auto710.controller.admin;

import com.auto710.common.annotation.AuditLog;
import com.auto710.common.api.Result;
import com.auto710.entity.CmsMenu;
import com.auto710.service.CmsMenuService;
import com.auto710.wechat.WechatService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 多端可视化菜单 —— 树结构 / 整树保存 / 发布
 * <p>覆盖公众号 / PC 官网 / 小程序 tabBar / H5 底部导航四端菜单的可视化编辑。</p>
 * <p>本类为手工编写，与生成的 CmsMenuController（CRUD）共用同一基础路径，
 * 子路径不冲突（Spring 字面量路径优先于 /{id} 变量路径）。</p>
 */
@Tag(name = "多端可视化菜单-可视化编辑")
@RestController
@RequestMapping("/api/admin/cms-menu")
@RequiredArgsConstructor
public class CmsMenuTreeController {

    private final CmsMenuService menuService;
    private final WechatService wechatService;

    @Operation(summary = "按端获取菜单树")
    @GetMapping("/tree")
    public Result<List<Map<String, Object>>> tree(@RequestParam(defaultValue = "public") String platform) {
        List<CmsMenu> menus = menuService.list(new LambdaQueryWrapper<CmsMenu>()
                .eq(CmsMenu::getPlatform, platform)
                .orderByAsc(CmsMenu::getSort)
                .orderByAsc(CmsMenu::getId));
        return Result.ok(buildTree(menus, 0L));
    }

    @Operation(summary = "保存单个菜单节点")
    @PostMapping("/save")
    @AuditLog(module = "菜单可视化", operation = "保存菜单节点")
    public Result<Long> save(@RequestBody CmsMenu menu) {
        if (menu.getParentId() == null) {
            menu.setParentId(0L);
        }
        if (menu.getId() == null) {
            menuService.save(menu);
        } else {
            menuService.updateById(menu);
        }
        return Result.ok(menu.getId());
    }

    @Operation(summary = "整树保存（拖拽排序 / 增删节点后整体提交）")
    @PostMapping("/save-tree")
    @AuditLog(module = "菜单可视化", operation = "保存菜单树")
    public Result<Boolean> saveTree(@RequestParam(defaultValue = "public") String platform,
                                    @RequestBody List<Map<String, Object>> nodes) {
        if (nodes == null) {
            return Result.ok(false);
        }
        // 整端覆盖：先清空该端旧菜单，再按提交的树结构重建（父子关系逐级落库）
        menuService.remove(new LambdaQueryWrapper<CmsMenu>().eq(CmsMenu::getPlatform, platform));
        persist(nodes, 0L, platform);
        return Result.ok(true);
    }

    @Operation(summary = "发布菜单（公众号端同步到微信）")
    @PostMapping("/publish")
    @AuditLog(module = "菜单可视化", operation = "发布菜单")
    public Result<Map<String, Object>> publish(@RequestParam(defaultValue = "public") String platform) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("platform", platform);
        result.put("publishedAt", LocalDateTime.now().toString());
        if ("mp".equals(platform)) {
            result.put("wechat", wechatService.syncMenu());
        }
        return Result.ok(result);
    }

    /** 递归落库：先保存父节点拿到自增主键，再保存其子节点 */
    @SuppressWarnings("unchecked")
    private void persist(List<Map<String, Object>> nodes, Long parentId, String platform) {
        int sort = 0;
        for (Map<String, Object> node : nodes) {
            CmsMenu menu = new CmsMenu();
            menu.setPlatform(platform);
            menu.setParentId(parentId);
            menu.setMenuName(str(node.get("menuName")));
            menu.setMenuNameEn(str(node.get("menuNameEn")));
            menu.setMenuType(strDefault(node.get("menuType"), "view"));
            menu.setUrl(str(node.get("url")));
            menu.setAppId(str(node.get("appId")));
            menu.setPagePath(str(node.get("pagePath")));
            menu.setKeyValue(str(node.get("keyValue")));
            menu.setIcon(str(node.get("icon")));
            menu.setSort(sort++);
            menu.setVisible(intDefault(node.get("visible"), 1));
            menu.setStatus(intDefault(node.get("status"), 1));
            menuService.save(menu);

            Object children = node.get("children");
            if (children instanceof List<?> childList && !childList.isEmpty()) {
                persist((List<Map<String, Object>>) childList, menu.getId(), platform);
            }
        }
    }

    private List<Map<String, Object>> buildTree(List<CmsMenu> menus, Long parentId) {
        List<Map<String, Object>> tree = new ArrayList<>();
        for (CmsMenu menu : menus) {
            if (!parentId.equals(menu.getParentId())) {
                continue;
            }
            Map<String, Object> node = toMap(menu);
            node.put("children", buildTree(menus, menu.getId()));
            tree.add(node);
        }
        return tree;
    }

    private Map<String, Object> toMap(CmsMenu menu) {
        Map<String, Object> node = new LinkedHashMap<>();
        node.put("id", menu.getId());
        node.put("platform", menu.getPlatform());
        node.put("parentId", menu.getParentId());
        node.put("menuName", menu.getMenuName());
        node.put("menuNameEn", menu.getMenuNameEn());
        node.put("menuType", menu.getMenuType());
        node.put("url", menu.getUrl());
        node.put("appId", menu.getAppId());
        node.put("pagePath", menu.getPagePath());
        node.put("keyValue", menu.getKeyValue());
        node.put("icon", menu.getIcon());
        node.put("sort", menu.getSort());
        node.put("visible", menu.getVisible());
        node.put("status", menu.getStatus());
        return node;
    }

    private String str(Object o) {
        return o == null ? null : String.valueOf(o);
    }

    private String strDefault(Object o, String def) {
        String s = str(o);
        return s == null || s.isBlank() ? def : s;
    }

    private Integer intDefault(Object o, int def) {
        if (o == null) {
            return def;
        }
        try {
            return Integer.valueOf(String.valueOf(o));
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
