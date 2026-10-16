# 微信公众号对接 | 710号汽车之家

## 一、配置

后台 → 系统管理 → 系统参数 → 公众号：

| 参数 | 说明 |
| --- | --- |
| `wechat.mp.appid` | 公众号 AppID |
| `wechat.mp.secret` | AppSecret |
| `wechat.mp.token` | 服务器配置 Token |
| `wechat.mp.aesKey` | 消息加解密密钥 |

未配置时，相关能力进入 **安全空跑模式**（生成结构、记录日志，不实际调用微信）。

---

## 二、能力与接口

| 能力 | 接口 / 实现 |
| --- | --- |
| 关注欢迎语 | `WechatService.subscribeReply()`；规则表 `wechat_reply_rule`（rule_type=关注） |
| 关键词自动回复 | `WechatService.matchReply(keyword)`（全匹配/半匹配）；`GET /api/admin/wechat/reply?keyword=` 测试 |
| 自定义菜单构建 | `WechatService.buildMenuJson()`；`GET /api/admin/wechat/menu-json` |
| 菜单同步微信 | `WechatService.syncMenu()`；`POST /api/admin/wechat/sync-menu`；亦可通过菜单可视化「发布」触发 |
| 素材图文管理 | `wechat_material` 表（后台 CRUD） |
| 粉丝标签 | `wechat_fans` 表（后台 CRUD） |
| 模板消息 | `WechatService.sendTemplateMessage(openid, msgType, data)`；模板配置表 `wechat_template_msg` |
| 优惠券领取 | `POST /api/admin/wechat/coupon/use`、`POST /api/admin/wechat/coupon/expire-scan` |
| UnionID 打通 | `MemberBizService.wxLogin()` 按 `unionid → openid` 匹配同一会员 |

---

## 三、模板消息（工单全流程推送）

模板类型（`wechat_template_msg.msg_type`）覆盖：

- 预约确认 / 接车通知
- 施工进度
- 完工待取车
- 结算通知
- 存胎到期提醒
- 退款通知

业务侧通过 `MessageService` / `WechatService.sendTemplateMessage` 触发；
RabbitMQ 异步解耦（`msg`/`mq` 包），支持失败重试。

---

## 四、UnionID 账号统一（T33）

`member` 表同时保存 `unionid`、`openid`、`miniapp_openid`、`h5_openid`：

1. 小程序 / 公众号 / H5 登录时传入各自 code/unionid。
2. `wxLogin()` 优先按 `unionid` 匹配，其次按 `openid` 匹配，命中即复用同一会员账号。
3. 实现「一处注册、多端通用」的会员/车辆/工单/储值数据互通。

---

## 五、真实对接 TODO（生产接入点）

`WechatService` 中保留了调用微信开放接口的接入点：

1. `access_token`：调用 `cgi-bin/token` 并缓存 7000s（建议 Redis）。
2. 自定义菜单：`cgi-bin/menu/create`。
3. 模板消息：`cgi-bin/message/template/send`。
4. code2Session：小程序 `sns/jscode2session` / 公众号网页授权 `sns/oauth2/access_token`（取 openid/unionid）。
5. 服务器消息回调：实现 `GET/POST /wechat/callback`（验签 + 解密 + 分发关键词/关注事件）。

---

## 六、验收自测（对应 T30～T34）

- [ ] 配置 AppID/Secret 后 `sync-menu` 成功；未配置时返回空跑提示。
- [ ] 关键词命中返回配置的回复内容。
- [ ] 模板消息按 `msg_type` 取模板并发送（空跑可见日志）。
- [ ] 同一 unionid 在小程序与公众号登录后为同一会员。
- [ ] 优惠券领取同步到会员账户（`member_coupon`）。
