# 小程序与 H5 | 710号汽车之家

## 一、微信小程序（`miniprogram/`，微信原生）

### 目录

```
miniprogram/
├── app.js / app.json / app.wxss      # 全局（baseUrl、语言、站点信息）
├── project.config.json / sitemap.json
├── utils/
│   ├── request.js                    # 统一请求（token、语言头、401 处理、上传）
│   ├── auth.js                       # wx.login → /member/wx-login
│   ├── i18n.js                       # 中英字典 + t() / pick()
│   └── util.js                       # 金额/日期/状态工具
└── pages/
    ├── index/index                   # 首页（Banner/分类/热门/存胎入口/案例/知识/门店）
    ├── service/list & service/detail # 服务列表与详情
    ├── appointment/create & list     # 在线预约、我的预约
    ├── workorder/list & detail       # 我的工单、明细、确认报价、结算、评价
    ├── tire/index & create           # 存胎寄存列表、在线下单、寄存单详情与凭证
    ├── member/vehicles & coupons     # 车辆管理、优惠券
    ├── article/list & detail         # 养护知识
    ├── contact/index                 # 客服、导航、留言
    └── mine/index                    # 会员中心 + 语言切换
```

### 接入步骤

1. 微信开发者工具导入 `miniprogram/`。
2. 修改 `app.js` → `globalData.baseUrl` 为后端地址（如 `https://api.example.com/api`）。
3. 修改 `project.config.json` → `appid` 为你的小程序 AppID。
4. 小程序后台配置 **request 合法域名** 指向后端 HTTPS 域名。

### 对接接口

| 场景 | 接口 |
| --- | --- |
| 首页聚合 | `GET /api/miniapp/home` |
| 服务分类/列表/详情 | `/api/miniapp/service-categories`、`/service-items`、`/service-items/{id}` |
| 文章 | `/api/miniapp/articles`、`/articles/{id}` |
| 门店/站点 | `/api/miniapp/store`、`/site` |
| 留言 | `POST /api/miniapp/message` |
| 登录 | `POST /api/member/wx-login`（白名单） |
| 会员/车辆/优惠券 | `/api/member/profile`、`/vehicles`、`/coupons`、`/coupons/available` |
| 预约 | `GET/POST /api/member/appointments`、`/{id}/cancel` |
| 工单 | `/api/member/work-orders`、`/{id}`、`/{id}/items`、`/{id}/confirm-quote`、`/{id}/settle`、`/{id}/evaluate` |
| 存胎 | `GET/POST /api/member/tire-storage`、`/{id}/items`、`/{id}/proofs` |
| 上传 | `POST /api/miniapp/file/upload` |

---

## 二、H5 移动端（`h5/`，Vue3 + Vite）

### 客户侧

- `/services` 服务列表、`/service/:id` 服务详情（含微信分享卡片配置）
- `/booking` 在线预约（与小程序共用会员/车辆数据，UnionID 打通）
- `/orders` 我的工单与明细

### 接车技师端（T45）

- `/tech/login` 技师登录（口径 `/api/tech/login`，与后台共用技师账号）
- `/tech/orders` 我的工单（仅本技师）+ 个人接单/工时统计 + 状态筛选
- `/tech/orders/:id` 接车开单（检查项）、用料登记（自动出库）、一键开工、完工提交、异常卡单上报、施工日志

### 微信分享卡片（T44）

`src/utils/share.ts` 的 `applyShareCard({title, desc, link, imgUrl})`：

- 写入标准 OG meta（`og:title/description/url/image`）。
- 若页面注入公众号 JS-SDK（`window.wx`），在 `wx.ready` 中调用
  `updateAppMessageShareData` / `updateTimelineShareData`。

### 运行

```bash
cd h5 && npm install && npm run dev     # http://localhost:5175
cd h5 && npm run build                  # 产物 dist/
```

---

## 三、技师端权限与隔离（T46 / T47）

- `TechnicianController` 所有工单接口均经 `requireOwn(orderId)` 校验：仅当工单 `technician_id` 等于当前技师档案 ID 时放行。
- 改价需店长：技师端接口不提供改价能力，改价在后台由店长角色操作。
- 完工自动流转：`finish()` 触发状态机 → 待质检 / 待结算。
- 异常卡单：`/tech/orders/{id}/abnormal` 写入工单日志并置异常标记。
- 用料登记：`/tech/orders/{id}/items?outStock=true` 自动扣减配件库存并生成出库记录。
