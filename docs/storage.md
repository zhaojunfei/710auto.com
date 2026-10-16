# 对象存储对接 | 710号汽车之家

## 一、设计目标

统一存储抽象，支持 **本地磁盘 / AWS S3 / 阿里云 OSS / 腾讯云 COS / MinIO** 五种驱动，
业务代码只依赖 `StorageService` 接口，切换驱动无需改动业务。

上传、预览（签名 URL）、删除、版本控制（可选）均通过统一接口完成；
**富文本编辑器** 与 **图片/附件上传组件** 的图片自动走对象存储。

---

## 二、配置页交互（重点 UI）

后台 → 系统管理 → 对象存储：

1. **5 张驱动卡片** 单选（本地 / S3 / OSS / COS / MinIO）。
2. 选中后 **动态滑出** 该驱动参数表单（未选中驱动不渲染字段）。
3. AK/SK 为密码框，带 **眼睛切换** 明文/密文。
4. 点击 **「连接测试」** 成功后才点亮 **「保存并应用」**。
5. 切换驱动时 **二次确认**。

对应接口：

| 接口 | 说明 |
| --- | --- |
| `GET /api/admin/storage/drivers` | 驱动列表与字段定义 |
| `GET /api/admin/storage/config` | 当前配置（密钥掩码） |
| `POST /api/admin/storage/test` | 连接测试 |
| `POST /api/admin/storage/save` | 保存并应用（密钥 AES 加密落库） |

---

## 三、各驱动参数

### 本地磁盘
- `basePath`：存储根目录（默认 `/app/data/upload`）
- `urlPrefix`：访问前缀（Nginx 映射）

### MinIO
- `endpoint`（如 `http://minio:9000`）、`accessKey`、`secretKey`、`bucket`

### AWS S3
- `region`、`endpoint`（可空）、`accessKey`、`secretKey`、`bucket`

### 阿里云 OSS
- `endpoint`（如 `oss-cn-hangzhou.aliyuncs.com`）、`accessKeyId`、`accessKeySecret`、`bucket`

### 腾讯云 COS
- `region`（如 `ap-guangzhou`）、`secretId`、`secretKey`、`bucket`

> 统一约定：`bucket` 需可公开读或通过 **签名 URL** 访问；`urlPrefix/CDN` 用于返回可访问地址。

---

## 四、上传接口

| 接口 | 说明 |
| --- | --- |
| `POST /api/admin/file/upload` | 后台单文件上传（默认目录 `images`） |
| `POST /api/admin/file/uploads` | 多文件上传 |
| `DELETE /api/admin/file?key=xxx` | 删除对象 |
| `POST /api/miniapp/file/upload` | 小程序上传 |

返回：`{ url, key, name, size, contentType }`（`key` 用于删除/替换）。

---

## 五、驱动实现

代码位置 `com.auto710.storage`：

- `StorageService`：统一接口（upload / delete / getUrl / testConnection）。
- `LocalStorageService` / `S3StorageService` / `OssStorageService` / `CosStorageService` / `MinioStorageService`。
- `StorageDriverFactory`：按当前配置动态选择驱动。
- `StorageConfigService`：读取/保存配置（密钥加密），提供连接测试。

---

## 六、依赖

| 驱动 | SDK |
| --- | --- |
| AWS S3 | `software.amazon.awssdk:s3` (v2) |
| 阿里云 OSS | `com.aliyun.oss:aliyun-sdk-oss` |
| 腾讯云 COS | `com.qcloud:cos_api` |
| MinIO | S3 兼容（复用 AWS S3 SDK） |

---

## 七、验收自测（对应 T52）

- [ ] 依次选择 5 种驱动，参数表单随选择动态渲染。
- [ ] AK/SK 眼睛切换正常，未测试通过时「保存并应用」不可用。
- [ ] 连接测试对错误参数给出明确失败提示。
- [ ] 切换驱动二次确认；保存后密钥以密文落库。
- [ ] 富文本与上传组件的图片自动上传到当前驱动并可预览/删除。
