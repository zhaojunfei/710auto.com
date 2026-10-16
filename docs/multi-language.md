# 多语言（i18n）方案 | 710号汽车之家

## 一、目标

- 官网 / 小程序 / H5 / 后台全套支持 **简体中文（zh-CN）** 与 **英文（en-US）**，并可扩展更多语言。
- 语言覆盖：**静态界面文案** + **动态业务内容**（服务项目、文章、菜单、Banner、门店）。
- 双语 SEO：`hreflang`、按语言渲染的标题/描述。

---

## 二、数据模型

| 表 | 作用 |
| --- | --- |
| `sys_language` | 语言列表：编码、名称、国旗、是否默认、是否启用、排序 |
| `sys_i18n_content` | 内容字段级多语言：`biz_type + biz_id + lang_code + field_key → field_value` |

此外，主要业务实体自带 `*_en` 字段（如 `service_item.item_name / item_name_en`、`cms_article.title / title_en`、`cms_menu.menu_name / menu_name_en`、`store_info.address / address_en`），
满足「一主一译」常见场景；`sys_i18n_content` 用于需扩展多语种或长文本字段的场景。

---

## 三、后端接口

| 接口 | 说明 |
| --- | --- |
| `GET /api/public/languages` | 启用语言列表（前台语言切换器数据源） |
| `GET /api/admin/language/list-all` | 后台语言列表 |
| `PUT /api/admin/language/{id}/status?status=1` | 启用/停用 |
| `PUT /api/admin/language/{id}/default` | 设为默认 |
| `GET /api/admin/i18n/fields?bizType&bizId&langCode` | 读取某业务某语言字段 |
| `POST /api/admin/i18n/fields?bizType&bizId&langCode` | 保存字段（覆盖） |

`I18nService.machineTranslate(text, from, to)` 为机器翻译初翻占位，返回原文供人工校对（可替换为第三方翻译 API）。

---

## 四、前端实现

### 官网 / H5

- `lang` 持久化：`localStorage.auto710_www_lang` + Cookie `auto710_lang`（服务端/SEO 可读）。
- 静态文案：本地字典 `t(key)`。
- 动态内容：`pick(obj, '中文字段', 'enField')` 按当前语言取值。
- 请求头携带 `Accept-Language`。
- 切换语言后刷新页面，重取接口数据。

### 小程序

- `utils/i18n.js`：字典 + `t()` + `pick()`，`wx.setStorageSync('auto710_locale')` 持久化。
- 切换语言调用 `app.applyTabBar(locale)` 刷新 tabBar 文案，并 `setNavigationBarTitle`。
- 「我的」页面提供语言切换入口。

### SEO / hreflang

`web-www/index.html` 内已声明：

```html
<link rel="alternate" hreflang="zh-CN" href="/zh-CN/" />
<link rel="alternate" hreflang="en-US" href="/en-US/" />
<link rel="alternate" hreflang="x-default" href="/" />
```

生产环境可结合 Nginx 按 `/zh-CN/`、`/en-US/` 路径预渲染或 SSR，以提升搜索引擎收录效果。

---

## 五、后台多语言录入流程

1. 系统管理 → 多语言配置 → 新增语言并启用。
2. 在服务项目 / 文章 / Banner 编辑页录入 `*_en` 字段。
3. 需扩展字段时，在「内容多语言字段」区选择 `业务类型 + 业务ID + 语言`，逐字段录入并保存。
4. 前台切换语言即可查看对应语言内容。

---

## 六、验收自测（对应 T49）

- [ ] 切换中/英，官网导航、服务项目、文章、门店信息全部按语言渲染。
- [ ] 小程序 tabBar、页面标题、业务内容随语言切换。
- [ ] 后台语言列表可增删改、启用/停用、设默认。
- [ ] 内容字段按 `bizType/bizId/langCode` 正确存取。
- [ ] 机器翻译初翻结果可人工校对后保存。
