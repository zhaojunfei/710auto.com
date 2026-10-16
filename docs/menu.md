# 菜单可视化管理 | 710号汽车之家

## 一、目标

一套后台界面，统一维护 **公众号自定义菜单 / PC 官网导航 / 小程序 tabBar / H5 底部导航** 四端菜单，
支持拖拽排序与层级、实时预览、整树保存，并将公众号菜单一键同步到微信。

---

## 二、数据模型 `cms_menu`

| 字段 | 说明 |
| --- | --- |
| `platform` | 端：`mp` 公众号 / `public` 官网 / `miniapp` 小程序 / `h5` |
| `parent_id` | 父级 ID（0 为顶级） |
| `menu_name` / `menu_name_en` | 中/英文名称 |
| `menu_type` | `view` 网页 / `miniprogram` 小程序 / `click` 点击 / `keyword` 关键词 / `article` 图文 / `link` 内部路由 |
| `url` | 跳转地址 |
| `app_id` / `page_path` | 小程序 AppID 与页面路径 |
| `key_value` | click/keyword 关键字 |
| `icon` / `sort` / `visible` / `status` | 图标 / 排序 / 显示 / 状态 |

---

## 三、接口

| 接口 | 说明 |
| --- | --- |
| `GET /api/admin/cms-menu/tree?platform=mp` | 取某端菜单树 |
| `POST /api/admin/cms-menu/save` | 保存单个节点 |
| `POST /api/admin/cms-menu/save-tree?platform=mp` | **整树覆盖保存**（拖拽排序/增删后整体提交） |
| `POST /api/admin/cms-menu/publish?platform=mp` | 发布；公众号端同步微信 |
| `GET /api/admin/cms-menu/{id}` | 详情（生成器提供） |
| `DELETE /api/admin/cms-menu/{id}` | 删除（生成器提供） |
| `GET /api/public/menu?platform=public` | 前台读取菜单 |

> 实现说明：`CmsMenuController`（生成器产出，通用 CRUD）与 `CmsMenuTreeController`（手工编写，树/发布）
> 共用基础路径 `/api/admin/cms-menu`，子路径不冲突（Spring 字面量路径优先于 `/{id}`）。

---

## 四、界面交互（后台 → 系统管理 → 菜单可视化）

1. 顶部按端切换（公众号 / 官网 / 小程序 / H5）。
2. **左栏**：菜单树，支持拖拽调整顺序与层级；节点上「+子」「删除」。
3. **中栏**：属性面板，随节点类型动态显示字段（如 `miniprogram` 才显示 AppID/页面路径）。
4. **右栏**：实时预览外壳（官网为 PC 外壳、其余为手机外壳）。
5. 「保存并发布」：整树落库 + 发布事件。
6. 公众号端额外提供「预览到微信」，调用 `WechatService.buildMenuJson()` 生成三级菜单结构。
7. 未保存离开会弹确认。

---

## 五、前台生效

- 官网 `Header`：优先读取 `/api/public/menu?platform=public`；无配置时回退默认导航。
- 小程序：读取 `platform=miniapp` 渲染 shadow tabBar 或自定义导航；`app.json` tabBar 为兜底。
- H5：读取 `platform=h5` 渲染底部导航。

---

## 六、公众号菜单同步

`POST /api/admin/cms-menu/publish?platform=mp` 内部调用 `WechatService.syncMenu()`：

- 未配置 `AppID/AppSecret`：返回菜单结构 JSON（空跑），`synced=false`。
- 已配置：调用微信 `menu/create` 接口提交（真实调用位置在 `WechatService` 中，按需接入）。

---

## 七、验收自测（对应 T50）

- [ ] 后台修改四端菜单并保存，前台刷新后实时生效。
- [ ] 拖拽改变顺序/层级后保存，前台顺序一致。
- [ ] 公众号端「预览到微信」返回结构正确的三级菜单 JSON。
- [ ] 「保存并发布」后公众号菜单同步接口调用成功（或空跑提示）。
- [ ] 未保存离开有确认提示。
