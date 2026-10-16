# 启动与本地运行指南

> 面向「拿到源码后第一次跑起来」的人。按顺序执行即可。
> 需要：**JDK 17**、**Maven 3.8+**、**Node 18+**、**MySQL 8**、**Redis 7**（可选 RabbitMQ）。

---

## 一键启动（Windows，推荐）

项目根目录提供了三个双击即用的脚本：

| 脚本 | 作用 |
| --- | --- |
| `start.bat` | 一键启动 **后端 + 后台 + 官网 + H5**（各开一个窗口），并自动打开浏览器 |
| `stop.bat` | 停止以上服务（按端口 8080/5173/5174/5175 结束进程） |
| `start-docker.bat` | 用 Docker Compose 一键部署（需先装 Docker Desktop） |

说明：

- 首次运行 `start.bat`：若 `backend/target/auto710-backend.jar` 不存在会自动 `mvn package`；
  某个前端缺 `node_modules` 会自动 `npm install`（较慢，请耐心等待）。
- 想强制重建后端：`start.bat build`。
- 已启动的服务（端口被占用）会自动跳过，不会重复启动。
- 脚本内已设定控制台编码为 GBK，中文提示可正常显示。
- 需要本机已装好 **MySQL 8** 并已导入 `sql/schema.sql` + `sql/data.sql`。
  Redis / RabbitMQ 未启动时后端仍可运行（消息、缓存相关功能降级）。

---

## 0. 一共有几个端？

| 端 | 目录 | 本地端口 | 入口 |
| --- | --- | --- | --- |
| 后端 API | `backend/` | 8080 | `http://localhost:8080/swagger-ui.html` |
| PC 统一后台 | `web-admin/` | 5173 | `http://localhost:5173/` |
| PC 品牌官网 | `web-www/` | 5174 | `http://localhost:5174/` |
| H5 移动端（客户 + 技师 + **供应商**） | `h5/` | 5175 | 见下表 |
| 微信小程序 | `miniprogram/` | — | 微信开发者工具导入 |
| 微信公众号 | — | — | 后台「公众号」菜单配置 |

**H5 同一个应用里有三套入口：**

| 入口 | 地址 | 账号 |
| --- | --- | --- |
| 客户预约 | `http://localhost:5175/` | 无需登录 |
| 接车技师端 | `http://localhost:5175/tech/login` | `tech01 / admin123` |
| **供应商端** | `http://localhost:5175/supplier/login` | `supplier01 / admin123` |

> 供应商端**只在 H5 里**，不在 PC 后台侧边栏（供应商账号没有任何后台菜单权限）。

---

## 1. 初始化数据库（MySQL 8）

```bash
# 建库建表
mysql -uroot -p < sql/schema.sql
# 导入种子数据（角色/用户/菜单/门店/服务/配件/供应商等）
mysql -uroot -p auto_repair < sql/data.sql
```

> `sql/schema.sql` 里写死了 `CREATE DATABASE auto_repair`，`data.sql` 里写死了 `USE auto_repair`。
> 如果你本机库名/账号不同（例如本机用 `auto710`），请用下面的「local profile」方式启动后端，
> 并在 `auto710` 库里手动执行这两个脚本（把 `USE auto_repair` 改成你的库名，或直接 `mysql auto710 < sql/data.sql`）。

---

## 2. 启动后端

```bash
cd backend
mvn -DskipTests package
java -jar target/auto710-backend.jar
```

- 默认读取 `backend/src/main/resources/application.yml`（连接的库名/密码以该文件为准）。
- 想用不提交到 git 的本机配置，把本机数据库账号密码放到
  `backend/src/main/resources/application-local.yml`（已被 `.gitignore` 忽略），然后：

```bash
java -jar target/auto710-backend.jar --spring.profiles.active=local
```

启动成功后：`http://localhost:8080/swagger-ui.html` 能打开即正常。

> **Redis / RabbitMQ 没装也能跑**（消息相关功能降级）；MySQL 必装。

---

## 3. 启动前端（各开一个终端）

```bash
# PC 统一后台
cd web-admin && npm install && npm run dev      # http://localhost:5173

# PC 品牌官网
cd web-www   && npm install && npm run dev      # http://localhost:5174

# H5 移动端（含供应商端）
cd h5        && npm install && npm run dev      # http://localhost:5175
```

> 前端都已配置 `/api` 反向代理到 `http://127.0.0.1:8080`，无需额外改地址。

---

## 4. 预置账号（密码都是 `admin123`）

| 账号 | 角色 | 说明 |
| --- | --- | --- |
| `admin` | 超级管理员 | PC 后台全部功能 |
| `manager` | 门店店长 | PC 后台经营功能 |
| `finance` | 财务 | PC 后台结算/退款/报表 |
| `tech01` | 接车技师 | **H5 技师端** |
| `supplier01` | 供应商 | **H5 供应商端** |

---

## 5. 验证供应商端是否正常（3 步）

1. 浏览器打开 `http://localhost:5175/supplier/login`，用 `supplier01 / admin123` 登录；
2. 进入「配件与报价」可维护自有配件报价，进入「提交送货单」可下单；
3. 回到 PC 后台 `http://localhost:5173/` →「配件库存 → 供应商送货单」页签，
   对刚提交的单据点「确认入库」，配件库存自动 +数量，并写入出入库流水。

---

## 6. 一键 Docker 部署（可选）

```bash
cp deploy/.env.example .env     # 修改密码/密钥
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

## 7. 常见问题

- **后台所有「维护」列表都没有「新增/编辑/删除」按钮？**
  前端 `CrudTable.vue` 的 `canAdd/canEdit/canDelete` 必须用 `withDefaults` 显式设为 `true`
  （Vue 会把未传入的 Boolean prop 强制转换为 `false`）。当前代码已修复；若仍不可见请硬刷新（Ctrl+F5）。
- **接口 401**：先登录；Token 存在浏览器 `localStorage`。
- **供应商登录报 401**：确认 `sys_user` 里有 `supplier01`，且 `part_supplier.user_id` 指向该用户 id。
