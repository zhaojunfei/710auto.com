# 部署手册 | 710号汽车之家 数字化多端平台

## 一、环境要求

| 组件 | 版本 | 说明 |
| --- | --- | --- |
| JDK | 17+ | 后端运行 |
| Maven | 3.9+ | 后端构建 |
| Node.js | 18 / 20 LTS | 前端构建 |
| MySQL | 8.0 | 主数据库（utf8mb4） |
| Redis | 7.x | 缓存 / 验证码 / 限流 |
| RabbitMQ | 3.13 | 异步消息（模板消息、提醒） |
| Nginx | 1.27 | 静态站点 + 反向代理 |
| Docker / Compose | 24+ | 推荐部署方式 |

---

## 二、Docker Compose 部署（推荐）

### 1. 准备环境变量

```bash
cp deploy/.env.example .env
```

编辑 `.env`：

```ini
MYSQL_ROOT_PASSWORD=请修改为强密码
MYSQL_USER=auto_repair_app
MYSQL_PASSWORD=请修改为强密码
REDIS_PASSWORD=请修改为强密码
RABBITMQ_USER=auto710
RABBITMQ_PASSWORD=请修改为强密码
MINIO_ROOT_USER=auto710
MINIO_ROOT_PASSWORD=请修改为强密码
JWT_SECRET=至少32位随机字符串
AES_KEY=16/24/32位密钥
```

### 2. 构建并启动

```bash
docker compose up -d --build
docker compose ps
```

### 3. 初始化数据库

首次启动时 MySQL 会自动执行 `sql/*.sql`（挂载到 `/docker-entrypoint-initdb.d`）。
若数据卷已存在，需手动初始化：

```bash
docker compose exec -T mysql8 mysql -uroot -p"$MYSQL_ROOT_PASSWORD" < sql/schema.sql
docker compose exec -T mysql8 mysql -uroot -p"$MYSQL_ROOT_PASSWORD" auto_repair < sql/data.sql
```

### 4. 访问

| 服务 | 地址 |
| --- | --- |
| 后台管理 | http://<host>:8081 |
| PC 官网 | http://<host>:8082 |
| H5 移动端 | http://<host>:8083 |
| 后端 API | http://<host>:8080 |
| Swagger | http://<host>:8080/swagger-ui.html |
| RabbitMQ 控制台 | http://<host>:15672 |
| MinIO 控制台 | http://<host>:9001 |

---

## 三、传统（非容器）部署

### 后端

```bash
cd backend
mvn -DskipTests clean package
# 生成 target/auto710-backend.jar
nohup java -Xms512m -Xmx1024m -Dfile.encoding=UTF-8 \
  --spring.profiles.active=prod \
  -jar target/auto710-backend.jar > app.log 2>&1 &
```

关键环境变量（可用 `application-prod.yml` 或环境变量覆盖）：

```ini
SPRING_DATASOURCE_URL=jdbc:mysql://127.0.0.1:3306/auto_repair?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false
SPRING_DATASOURCE_USERNAME=auto_repair_app
SPRING_DATASOURCE_PASSWORD=******
SPRING_DATA_REDIS_HOST=127.0.0.1
SPRING_DATA_REDIS_PASSWORD=******
SPRING_RABBITMQ_HOST=127.0.0.1
SPRING_RABBITMQ_USERNAME=auto710
SPRING_RABBITMQ_PASSWORD=******
AUTO710_JWT_SECRET=******
AUTO710_SECURITY_AES_KEY=******
```

### 前端

```bash
# 后台
cd web-admin && npm ci && npm run build        # 产物 dist/
# 官网
cd web-www  && npm ci && npm run build
# H5
cd h5       && npm ci && npm run build
```

将各 `dist/` 交由 Nginx 托管，参考各项目内的 `nginx.conf`（已含 `/api/` 反向代理与 SPA history 回退）。

---

## 四、HTTPS 与域名

1. 申请证书（Let's Encrypt / 云厂商），配置 `listen 443 ssl;`。
2. HTTP 80 强制跳转 HTTPS。
3. 官网、H5 建议在 `<head>` 与 `nginx.conf` 中补充 `hreflang`、`sitemap.xml`、`robots.txt`。
4. 微信相关：公众号/小程序后台配置 **JS 接口安全域名**、**业务域名**、**服务器域名**（request 合法域名指向后端 HTTPS 域名）。

---

## 五、数据备份

`deploy/backup/backup.sh` 随 `mysql-backup` 服务运行，默认保留 7 天：

```bash
docker compose logs -f mysql-backup
```

手动备份：

```bash
docker compose exec mysql8 mysqldump -uroot -p"$MYSQL_ROOT_PASSWORD" auto_repair | gzip > backup_$(date +%F).sql.gz
```

恢复：

```bash
gunzip -c backup_2026-01-01.sql.gz | docker compose exec -T mysql8 mysql -uroot -p"$MYSQL_ROOT_PASSWORD" auto_repair
```

---

## 六、升级流程

```bash
git pull
docker compose build backend-api web-admin web-www web-h5
docker compose up -d
docker compose exec -T mysql8 mysql -uroot -p"$MYSQL_ROOT_PASSWORD" < sql/schema.sql   # 仅在表结构变更时
```

> 表结构变更会 **DROP + CREATE**，请先备份或手工执行增量 DDL。生产环境建议单独维护增量迁移脚本。
