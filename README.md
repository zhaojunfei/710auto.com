# 710号汽车之家 · 数字化多端平台

> 汽车维修保养行业的一体化数字平台：**PC 统一后台 + PC 品牌官网 + 微信小程序 + H5 移动端 + 接车技师端 + 微信公众号**，
> 覆盖「预约 → 接车开单 → 派工 → 施工 → 质检 → 结算 → 评价 → 存胎寄存 → 库存/会员/营销」全链路，
> 内置**多语言（中/英）+ 多端菜单可视化 + 多驱动对象存储 + 富文本 + RBAC/审计日志**。

---

## 一、技术栈

| 层 | 技术 |
| --- | --- |
| 后端 | Java 17 · Spring Boot 3.3 · MyBatis-Plus 3.5 · MySQL 8 · Redis 7 · RabbitMQ 3.13 |
| 鉴权 | JJWT 0.12 · BCrypt · 自定义 RBAC（角色/菜单/权限） |
| 对象存储 | 本地磁盘 / AWS S3 / 阿里云 OSS / 腾讯云 COS / MinIO（统一抽象，可切换） |
| 后台前端 | Vue 3 · Vite 5 · TypeScript · Element Plus · WangEditor 5 |
| 官网前端 | Vue 3 · Vite 5 · 中英双语 · SEO / hreflang |
| 小程序 | 微信原生小程序（中英双语） |
| H5 | Vue 3 · Vite 5（客户预约 + 接车技师移动端 + 供应商移动端） |
| 部署 | Docker Compose · Nginx · 定时备份 |

> 说明：按需求**不生成独立 App**，移动端由微信小程序 + H5 承载。

---

## 二、目录结构

```
710auto.com/
├── backend/            # Spring Boot 后端（单模块 Maven）
│   └── src/main/java/com/auto710/
│       ├── common/     # 统一返回、异常、JWT、加密/脱敏/敏感词、注解与切面
│       ├── config/     # MyBatis-Plus、Redis、RabbitMQ、OpenAPI、全局配置服务
│       ├── security/   # JWT 拦截器、RBAC 服务
│       ├── storage/    # 多驱动对象存储抽象与实现
│       ├── i18n/       # 多语言服务与前台语言接口
│       ├── wechat/     # 公众号：菜单、模板消息、关键词回复
│       ├── biz/        # 业务域服务（预约/工单/库存/存胎/会员/优惠券/统计）
│       ├── controller/ # admin(后台) 与 api(前台/会员/技师) 控制器
│       ├── entity/mapper/service  # 由生成器产出
│       └── task/msg/mq # 定时任务、消息、MQ 监听
├── web-admin/          # PC 统一后台（Vue3 + Element Plus）
├── web-www/            # PC 品牌官网（中英双语 SEO）
├── miniprogram/        # 微信原生小程序
├── h5/                 # H5 移动端（客户 + 技师端 + 供应商端）
├── sql/                # schema.sql（建表） + data.sql（种子数据）
├── tools/              # 代码生成器（tables.js + generate.js）
├── deploy/             # 部署脚本（环境变量示例、数据库备份）
├── docs/               # 部署 / 配置 / 多语言 / 菜单 / 存储 / 微信 / 业务差异文档
├── docker-compose.yml  # 一键编排
├── PRO.md / tasks.md / checklist.md   # 需求 / 任务 / 验收清单
└── README.md
```

---

## 三、快速开始

### 1. 后端本地运行

```bash
# 1) 初始化数据库（MySQL 8）
mysql -uroot -p < sql/schema.sql
mysql -uroot -p auto_repair < sql/data.sql

# 2) 启动依赖（Redis / RabbitMQ / MinIO 等，也可只起 backend 依赖）
docker compose up -d mysql8 redis7 rabbitmq3 minio

# 3) 编译并运行
cd backend
mvn spring-boot:run
# 或者
mvn -DskipTests clean package && java -jar target/auto710-backend.jar
```

默认端口 `8080`，接口文档：`http://localhost:8080/swagger-ui.html`。

> **本机使用 `auto710` 库/账号时**：仓库已忽略 `backend/src/main/resources/application-local.yml`
> （含本机数据库账号密码，不入库）。用 local profile 启动即可：
> ```bash
> java -jar target/auto710-backend.jar --spring.profiles.active=local
> ```

### 2. 前端本地运行

```bash
# 后台
cd web-admin && npm install && npm run dev     # http://localhost:5173

# 官网
cd web-www && npm install && npm run dev       # http://localhost:5174

# H5（客户预约 + 接车技师端 + 供应商端 三合一）
cd h5 && npm install && npm run dev            # http://localhost:5175
#   客户预约   http://localhost:5175/
#   技师端     http://localhost:5175/tech/login      （tech01 / admin123）
#   供应商端   http://localhost:5175/supplier/login  （supplier01 / admin123）
```

> 完整、逐步的启动步骤见 **[docs/startup.md](docs/startup.md)**。

### 3. 小程序

用**微信开发者工具**导入 `miniprogram/` 目录，修改 `app.js` 中的 `globalData.baseUrl` 指向后端地址；
`project.config.json` 的 `appid` 替换为你自己的小程序 AppID。

### 4. 一键 Docker 部署

```bash
cp deploy/.env.example .env        # 按需修改密码/密钥
docker compose up -d --build
```

| 服务 | 地址 |
| --- | --- |
| 后台管理 | http://localhost:8081 |
| PC 官网 | http://localhost:8082 |
| H5 移动端 | http://localhost:8083 |
| 后端 API | http://localhost:8080 |
| RabbitMQ 控制台 | http://localhost:15672 |
| MinIO 控制台 | http://localhost:9001 |

---

## 四、预置账号（种子数据）

| 账号 | 密码 | 角色 |
| --- | --- | --- |
| admin | admin123 | 超级管理员 |
| manager | admin123 | 门店店长 |
| tech01 | admin123 | 接车技师 |
| finance | admin123 | 财务 |
| supplier01 | admin123 | 供应商（**H5 供应商端** `/supplier/login` 登录，非 PC 后台） |

> 首次登录后请立即在「系统管理 → 用户角色」中修改密码。
> 供应商账号绑定关系：`part_supplier.user_id` → `sys_user.id`。

---

## 五、核心能力一览

- **多语言**：语言列表维护 + 内容字段级多语言（`sys_i18n_content`）+ 前台语言切换器；中英双语 SEO / hreflang。
- **RBAC（用户→角色→权限→菜单）**：系统管理 → 用户角色（分配角色）/ 菜单管理（维护后台菜单树，支持目录/菜单/按钮三种类型与权限标识）。按钮级权限按 `perms`（如 `store-info:add`）控制，超级管理员默认全权限、只读账号无操作按钮；关键写接口后端强校验。
- **菜单可视化**：公众号 / PC 官网 / 小程序 tabBar / H5 底部导航四端统一维护，支持拖拽排序、PC/手机实时预览、公众号「预览到微信 + 发布」两步。
- **对象存储**：5 张驱动卡片单选 → 动态表单 → 连接测试通过后方可保存应用，支持本地/S3/OSS/COS/MinIO。
- **富文本**：WangEditor 5 统一组件，图片自动走对象存储，多语言 tab 分别保存，粘贴自动清洗。
- **上传组件**：独占整行卡片块，拖拽 + 多图并行 + 缩略图网格 + 排序 + 预览 + 批量删除 + 进度与重试。
- **工单看板**：按状态分列卡片式，超时高亮/红闪，拖拽调优先级，全流程状态机（接车→派工→施工→质检→结算→离店→评价）。
- **存胎寄存**：在线下单、寄存库存、入库/取胎凭证（留证图）、到期提醒、逾期费用计算。
- **配件库存**：进销存、出库关联工单、库存预警、毛利统计。
- **供应商协同**：供应商端（H5）维护自有配件报价、提交送货单；门店后台确认入库自动加库存并写流水；按月对账单。
- **后台维护全覆盖**：预约管理（确认/取消/到店接车/剩余产能）、会员车辆/已领券/积分流水/储值流水/已购套餐、退款单审核、存胎货位库存/明细/凭证、工单评价、派工记录、文章分类、数据字典、文件管理、经营日报；账本/日志类只读。
- **会员营销**：会员/多车绑定、积分、储值、优惠券、养护套餐、充值赠礼。
- **公众号**：关注欢迎语、关键词回复、自定义菜单同步、模板消息（全流程推送）、UnionID 打通。
- **安全**：防重提交、限流、敏感词检测、手机号/车牌脱敏、密钥加密存储、RBAC 数据隔离、操作审计日志。

---

## 六、代码生成说明

`entity / mapper / service / serviceImpl / controller(后台 CRUD) / sql/schema.sql` 均由
`tools/generate.js` 依据 `tools/tables.js` 生成。

```bash
node tools/generate.js
```

> ⚠️ 生成文件**不要手工修改**（会被覆盖）。自定义逻辑请：
> 1) 在 `tools/tables.js` 对应表用 `implExtra` 注入 Service 实现；
> 2) 或另建独立类（如 `controller/admin/CmsMenuTreeController`、`biz/*BizService`）。

---

## 七、文档

- [启动与本地运行指南](docs/startup.md)
- [部署手册](docs/deployment.md)
- [系统配置说明（存储 / 微信 / 支付 / 多语言 / 菜单）](docs/configuration.md)
- [多语言（i18n）方案](docs/multi-language.md)
- [菜单可视化](docs/menu.md)
- [对象存储对接](docs/storage.md)
- [微信公众号对接](docs/wechat.md)
- [小程序与 H5](docs/miniprogram-h5.md)
- [供应商协同（H5 供应商端）](docs/supplier-portal.md)
- [业务差异文档（对标中医馆 PRD）](docs/business-difference.md)
- [上线检查清单](docs/checklist-deploy.md)

---

## 八、许可

本项目为「710号汽车之家」定制交付源码，著作权归委托方所有。
