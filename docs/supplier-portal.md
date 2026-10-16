# 供应商协同（H5 供应商端）

> 供应商作为**配件货源方**，拥有独立移动端（H5），可维护自有配件报价、提交送货单、查看对账单；
> 门店在后台「配件库存 → 供应商送货单」确认入库，自动增加库存并写入出入库流水。

---

## 一、角色与身份绑定

| 项 | 说明 |
| --- | --- |
| 角色 | `SUPPLIER`（`sys_role.role_code = 'SUPPLIER'`，id=8） |
| 账号 | `sys_user`（种子账号 `supplier01 / admin123`） |
| 绑定 | `part_supplier.user_id` → `sys_user.id`，登录后据此识别身份，只能看自己的数据 |
| 端口 | **H5 供应商端**（`/supplier/login`），不进入 PC 后台 |

新增一个供应商的步骤：

1. 「系统管理 → 用户角色」新建用户（如 `supplier02`）；
2. 「配件库存 → 供应商」新建供应商档案，把 **绑定登录账号** 填为该用户的 `sys_user.id`；
3. 该供应商用账号登录 H5 `/supplier/login` 即可。

> ⚠️ 供应商账号**不会**出现在 PC 后台侧边栏（未授予任何后台菜单），避免越权进入门店后台。

---

## 二、业务流程

```
供应商提交送货单（待确认）
        │
        ▼
门店后台「确认入库」 ──▶ 状态=已入库，逐条 stockIn：配件库存 += 数量
        │                 并写入 part_stock_record（类型=入库，备注=送货单号）
        └─「拒绝」    ──▶ 状态=已拒绝（不动库存）
```

- 供应商**只能撤销**自己「待确认」的单据；已入库/已拒绝不可再改。
- 对账单按**确认入库时间**所在月份汇总，只统计「已入库」单据。

---

## 三、接口清单

### 供应商端（`/api/supplier/**`，需携带供应商 Token）

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/supplier/login` | 登录（**白名单**，无需 Token） |
| GET | `/api/supplier/profile` | 供应商档案 |
| GET | `/api/supplier/parts` | 我的配件/报价（按供应商名匹配 `part_info.supplier`） |
| POST | `/api/supplier/parts` | 新增或修改我的配件报价（越权改他人配件会被拒绝） |
| GET | `/api/supplier/deliveries` | 我的送货单（可按 `status` 过滤） |
| GET | `/api/supplier/deliveries/{id}` | 送货单详情（含明细） |
| POST | `/api/supplier/deliveries` | 提交送货单 |
| POST | `/api/supplier/deliveries/{id}/cancel` | 撤销（仅待确认） |
| GET | `/api/supplier/stats` | 工作台统计 |
| GET | `/api/supplier/statements?month=yyyy-MM` | 对账单 |

### 门店后台（`/api/admin/supplier-delivery/**`）

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/admin/supplier-delivery/page` | 送货单分页（生成器产出） |
| POST | `/api/admin/supplier-delivery/{id}/confirm?pass=true&remark=` | 确认入库 / 拒绝（`AdminSupplierDeliveryFlowController`） |
| GET | `/api/admin/supplier-delivery-item/page?deliveryId=` | 送货明细分页 |

---

## 四、H5 页面路由

| 路由 | 页面 |
| --- | --- |
| `/supplier/login` | 供应商登录 |
| `/supplier/home` | 工作台（统计 + 入口） |
| `/supplier/deliveries` | 我的送货单 |
| `/supplier/deliveries/new` | 提交送货单 |
| `/supplier/deliveries/:id` | 送货单详情 / 撤销 |
| `/supplier/parts` | 配件与报价维护 |
| `/supplier/statements` | 对账单 |

---

## 五、数据表

| 表 | 说明 |
| --- | --- |
| `part_supplier` | 供应商档案（新增 `user_id` 绑定登录账号） |
| `supplier_delivery` | 送货单主表（单号 `SH` 前缀、状态机、确认信息） |
| `supplier_delivery_item` | 送货明细（配件、数量、供货单价、金额） |

> 表结构由 `tools/tables.js` 定义并由 `node tools/generate.js` 生成，请勿手工修改生成文件。

---

## 六、Token 说明

- H5 请求拦截器按路径前缀选择 Token：
  `/tech*` → `auto710_tech_token`；`/supplier*` → `auto710_supplier_token`；其余 → `auto710_h5_token`。
- 供应商 Token 由 `JwtUtil.createToken()` 签发，`userType = SUPPLIER`；供应商接口通过
  `UserContext.require().getUserId()` 反查 `part_supplier` 完成数据隔离。
