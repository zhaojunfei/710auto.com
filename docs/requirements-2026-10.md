# 缺陷修复 + 功能调整（2026-10 需求落地说明）

本文对应 2026-10 提交的「缺陷修复 + 功能调整」需求单，逐条记录实现方式与验收方法。

---

## 一、全局规则

### (a) 手机号即会员
任何录入手机号的入口，统一走「输号 → 自动匹配会员 → 不存在则自动创建」。

* 后端统一入口：`MemberBizService#findOrCreateByPhone(phone, realName)`
  * 手机号以 AES 加密后精确匹配 `member.phone`（ECB 定长加密，同一明文密文一致）；
  * 命中则复用，未命中则自动建档：`用户名 = 手机号`、`初始密码 = 手机号后 6 位`（BCrypt）、`need_change_pwd = 1`（首次登录引导改密）。
* 已接入的开单/预约链路：
  * 后台手动开单 `POST /api/admin/work-order/manual`
  * 预约创建 `AppointmentBizService#create`
  * 预约到店接车 `AppointmentBizService#receive`（历史预约单兜底补建）
* 后台实时查号：`GET /api/admin/member-lookup/find?phone=`（只读）
  后台提交前建档：`POST /api/admin/member-lookup/ensure`（find-or-create）
* 会员端手机号登录：`POST /api/member/login`（用户名=手机号，密码=初始手机号后 6 位）
  修改密码：`POST /api/member/change-password`
* 表结构新增：`member.password`、`member.need_change_pwd`（见 `tools/tables.js` → 已生成 `sql/schema.sql`）。

> 升级已有库：`ALTER TABLE member ADD COLUMN password varchar(128), ADD COLUMN need_change_pwd tinyint;`

### (b) 业务界面一律用名称，不用 ID
`CrudTable` 新增 `lookup`（列展示）/ `source`（表单下拉）能力，把 `storeId / technicianId / memberId / userId / categoryId` 等
统一渲染为名称：门店名称、技师姓名、会员昵称、系统用户姓名、分类名称等。ID 仅内部主键。

### (c) 下拉全部字典化
下拉一律来自**数据字典**（`sys_dict_type` + `sys_dict_data`），禁止硬编码。
`CrudTable` 支持在列/表单/筛选中写 `dict: 'tech_type'`，前端统一从 `/admin/sys-dict-data` 读取（按 `dictSort` 排序、仅取启用项）。

已内置字典（22 类 / 97 项，见 `sql/data.sql`）：

| dictType | 名称 | dictType | 名称 |
| --- | --- | --- | --- |
| `tech_type` | 工种 | `tech_level` | 技师等级 |
| `station_type` | 工位类型 | `shift` | 排班班次 |
| `pay_type` | 结算方式 | `gender` | 性别 |
| `work_order_status` | 工单状态 | `appointment_status` | 预约状态 |
| `member_source` | 会员来源 | `coupon_type` | 优惠券类型 |
| `balance_type` | 储值流水类型 | `points_type` | 积分流水类型 |
| `tire_type` | 轮胎类型 | `item_type` | 工单明细类型 |
| `refund_status` | 退款状态 | `check_item` | 检查项 |
| `member_coupon_status` | 会员券状态 | `activity_type` | 活动类型 |
| `coupon_valid_type` | 券有效期类型 | `tire_storage_status` | 存胎状态 |
| `tire_position` | 轮胎位置 | `stock_status` | 库存状态 |

维护入口：后台「系统管理 → 数据字典」（字典类型 / 字典数据，支持增删改、启停、排序），实时生效。

### (d) 日期统一控件 + Label 规范
* `CrudTable` 新增字段类型 `date` / `datetime`，渲染为 `el-date-picker`（`value-format="yyyy-MM-dd"`），禁止手输；
* Label 自动规范：`date` → `名称(yyyy-MM-dd)`，`datetime` → `名称(yyyy-MM-dd HH:mm:ss)`；
* 删除/核销确认文案中出现的日期统一为 `(yyyy-MM-dd)`。

---

## 二、逐条实现

### 1. 手动开单：手机号自动关联 / 创建会员；施工中锁定检查项
* 手动开单弹窗：手机号失焦即时查号，命中提示会员信息（姓名/积分/储值），未命中提示「提交时自动创建」；提交前自动建档并回填 `memberId`。
* 检查项仅在 **待接车 / 待派工** 阶段可维护：
  * 后端 `WorkOrderBizService#addChecks` 校验，其他状态抛业务错误；
  * 前端进入施工环节后检查项表格只读，并显示「已锁定」提示。
* 检查项名称下拉来自字典 `check_item`，支持手工输入新增。

### 2. 工单状态流转 Bug（施工中卡死 / 非法流转）
* 状态机（后端 `WorkOrderStatus`，唯一真源）：

  `待接车 → 待派工 → 施工中 → 待质检 → 待结算 → 已离店 → 已评价`

* 新增 `POST /api/admin/work-order/{id}/finish`（施工中 → 待质检，即「完工提交」），补上原先缺失的环节。
* `settle` 增加状态守卫：仅允许在 **待结算** 发起；历史数据允许 **待质检** 直接结算（自动补一次质检通过）。
  其余状态返回明确业务错误，不再出现「非法状态流转：施工中 → 已离店」。
* 前端「状态操作」按钮严格按当前状态显示：待接车→接车完成；待派工→开工；施工中→完工提交；待质检→质检通过/不通过；待结算→结算。施工中**不出现结算入口**。

### 3. 技师管理：关联系统用户 / 门店改为选择；明确账号绑定
* `关联系统用户ID` → 下拉选择「系统管理-用户」中已有账号（显示姓名）；`门店ID` → 门店名称下拉。
* **账号绑定规则：只绑定，不自动创建**（`TechnicianServiceImpl#bindSysUser`，见 `tools/tables.js` 的 `t.implExtra`）：
  * 表单已选择「关联系统用户」→ 直接使用所选账号；
  * 未选择但填写了手机号 → 自动匹配**已存在**的系统账号（用户名或手机号 = 该手机号）并绑定；
  * 匹配不到 → 保持未绑定，由管理员先到「系统管理 - 用户」创建账号，再回来选择；
  * 该逻辑**不会**向 `sys_user` / `sys_user_role` 写入任何数据。表单已提示该规则。
* 约束：`technician.user_id` 为唯一键，一个系统账号只能绑定一个技师；重复绑定会被数据库拒绝。

### 4. 门店字段全局显示名称
列表 / 表单 / 筛选 / 报表统一使用门店名称：工位、技师、排班、会员、服务、配件、送货单、系统用户、剩余产能查询、技师产值排行等均已改造，无裸漏门店 ID。

### 5. 工种维护入口
* 新增字典类型 `tech_type`（工种）：机修 / 钣喷 / 美容 / 轮胎 / 电工 / 通用，支持增删改、启停、排序。
* 入口：后台「系统管理 → 数据字典 → 字典数据」，筛选 `tech_type`。
* 技师表单「工种」下拉读取该字典；技师等级读取 `tech_level`。

### 6. 日期控件与 Label 规范
见「全局规则 (d)」。已改造：技师排班日期、会员生日、车辆购车日期、已购套餐购买/到期时间、营销活动起止、存胎开始/到期/实际取胎日期、送货单预计到货、剩余产能查询日期等。

### 7. 下拉字典化
见「全局规则 (c)」。服务分类、检查项、工种、门店、状态、结算方式、优惠券类型、轮胎类型等均由数据字典 / 业务下拉提供，实时生效。

---

## 三、功能完整性核查（自查清单）

| 链路 | 状态 | 说明 |
| --- | --- | --- |
| 开单 → 派工 → 施工 → 质检 → 结算 → 离店 | ✅ | 全链路已实测通过（含施工中禁止结算、检查项锁定） |
| 评价 | ✅ | 已有 `work-order-evaluate`，离店后可评价，状态 → 已评价 |
| 存胎寄存：下单 / 入库 / 取胎 / 到期提醒 / 超期保管费 | ✅ | 寄存单 CRUD + 货位库存 + 取胎核销 + 换季批量提醒 + 超期扫描结算 |
| 储值结算 / 优惠券抵扣 / 退款 | ✅ | 结算支持储值/积分抵扣，退款单审核流程完整 |
| 配件耗材库存出库关联工单 | ✅ | `POST /admin/work-order/{id}/items?outStock=true` 出库并写库存流水 |
| 会员手机号全入口一致性 | ✅ | 手动开单、预约（H5/后台）、到店接车统一走 find-or-create |

---

## 四、验收脚本（本地）

```bash
# 1) 建后端
cd backend && mvn -DskipTests package
# 2) 起后端
java -jar target/auto710-backend.jar
# 3) 起后台
cd web-admin && npm run dev
```

数据库升级（已有库）：

```sql
ALTER TABLE `member`
  ADD COLUMN `password` varchar(128) DEFAULT NULL COMMENT '登录密码(BCrypt，初始为手机号后6位)',
  ADD COLUMN `need_change_pwd` tinyint DEFAULT NULL COMMENT '首次登录是否需改密 1是 0否';
```

字典初始化：`sql/data.sql` 末尾的字典段落为幂等脚本（先 DELETE 后 INSERT），可直接对已有库执行。

> 注：`backend/src/main/resources/application.yml` 含本地凭据，未纳入版本库。
