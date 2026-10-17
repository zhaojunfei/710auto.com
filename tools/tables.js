/**
 * 710号汽车之家 —— 数据库表定义（代码生成器数据源）
 * 字段格式：[ 列名, SQL类型, 中文注释 ]
 * 公共列（id / create_time / update_time / deleted）由生成器自动追加。
 */
const TABLES = [
  /* ==================== 一、系统 & RBAC ==================== */
  {
    t: 'sys_user', c: '系统用户', uk: ['username'], idx: ['store_id'],
    f: [
      ['username', 'varchar(64)', '登录账号'],
      ['password', 'varchar(128)', '密码(BCrypt)'],
      ['real_name', 'varchar(64)', '真实姓名'],
      ['phone', 'varchar(128)', '手机号(加密)'],
      ['avatar', 'varchar(255)', '头像'],
      ['email', 'varchar(128)', '邮箱'],
      ['store_id', 'bigint', '所属门店'],
      ['status', 'tinyint', '状态 1启用 0禁用'],
      ['last_login_time', 'datetime', '最后登录时间'],
      ['remark', 'varchar(255)', '备注'],
    ],
    implExtra: `
    @Override
    public boolean save(SysUser entity) {
        encodeInsert(entity);
        return super.save(entity);
    }

    @Override
    public boolean updateById(SysUser entity) {
        encodeUpdate(entity);
        return super.updateById(entity);
    }

    /** 新增：默认密码 123456，明文自动 BCrypt */
    private void encodeInsert(SysUser entity) {
        String pwd = entity.getPassword();
        entity.setPassword(pwd == null || pwd.isBlank()
                ? cn.hutool.crypto.digest.BCrypt.hashpw("123456")
                : encode(pwd));
    }

    /** 修改：密码留空则不修改 */
    private void encodeUpdate(SysUser entity) {
        String pwd = entity.getPassword();
        entity.setPassword(pwd == null || pwd.isBlank() ? null : encode(pwd));
    }

    private String encode(String pwd) {
        return pwd.startsWith("$2a$") || pwd.startsWith("$2b$")
                ? pwd : cn.hutool.crypto.digest.BCrypt.hashpw(pwd);
    }
`,
  },
  {
    t: 'sys_role', c: '系统角色', uk: ['role_code'],
    f: [
      ['role_name', 'varchar(64)', '角色名称'],
      ['role_code', 'varchar(64)', '角色编码'],
      ['data_scope', 'tinyint', '数据范围 1全部 2本店 3本人'],
      ['sort', 'int', '排序'],
      ['status', 'tinyint', '状态 1启用 0禁用'],
      ['remark', 'varchar(255)', '备注'],
    ],
  },
  {
    t: 'sys_user_role', c: '用户角色关联', idx: ['user_id', 'role_id'],
    f: [['user_id', 'bigint', '用户ID'], ['role_id', 'bigint', '角色ID']],
  },
  {
    t: 'sys_menu', c: '系统菜单权限', idx: ['parent_id'],
    f: [
      ['parent_id', 'bigint', '父级ID'],
      ['menu_name', 'varchar(64)', '菜单名称'],
      ['menu_type', 'varchar(16)', '类型 DIR/MENU/BUTTON'],
      ['path', 'varchar(255)', '路由地址'],
      ['component', 'varchar(255)', '组件路径'],
      ['perms', 'varchar(128)', '权限标识'],
      ['icon', 'varchar(64)', '图标'],
      ['sort', 'int', '排序'],
      ['visible', 'tinyint', '是否显示 1是 0否'],
      ['status', 'tinyint', '状态 1启用 0禁用'],
    ],
  },
  {
    t: 'sys_role_menu', c: '角色菜单关联', idx: ['role_id', 'menu_id'],
    f: [['role_id', 'bigint', '角色ID'], ['menu_id', 'bigint', '菜单ID']],
  },
  {
    t: 'sys_audit_log', c: '操作审计日志', idx: ['user_id', 'create_time'],
    f: [
      ['user_id', 'bigint', '操作人ID'],
      ['username', 'varchar(64)', '操作人账号'],
      ['module', 'varchar(64)', '模块'],
      ['operation', 'varchar(64)', '操作类型'],
      ['method', 'varchar(255)', '请求方法'],
      ['request_url', 'varchar(255)', '请求地址'],
      ['request_method', 'varchar(16)', '请求方式'],
      ['request_param', 'text', '请求参数'],
      ['response_result', 'text', '返回结果'],
      ['ip', 'varchar(64)', '操作IP'],
      ['user_agent', 'varchar(512)', '浏览器UA'],
      ['cost_time', 'bigint', '耗时(ms)'],
      ['status', 'tinyint', '状态 1成功 0失败'],
      ['error_msg', 'text', '异常信息'],
    ],
  },
  {
    t: 'sys_config', c: '系统全局配置', uk: ['config_key'],
    f: [
      ['config_key', 'varchar(128)', '配置键'],
      ['config_value', 'text', '配置值'],
      ['config_name', 'varchar(128)', '配置名称'],
      ['config_group', 'varchar(64)', '配置分组'],
      ['config_type', 'varchar(32)', '值类型 string/int/bool/json/secret'],
      ['encrypted', 'tinyint', '是否加密 1是 0否'],
      ['remark', 'varchar(255)', '备注'],
    ],
  },
  {
    t: 'sys_dict_type', c: '字典类型', uk: ['dict_type'],
    f: [
      ['dict_type', 'varchar(64)', '字典类型编码'],
      ['dict_name', 'varchar(64)', '字典名称'],
      ['status', 'tinyint', '状态 1启用 0禁用'],
      ['remark', 'varchar(255)', '备注'],
    ],
  },
  {
    t: 'sys_dict_data', c: '字典数据', idx: ['dict_type'],
    f: [
      ['dict_type', 'varchar(64)', '字典类型编码'],
      ['dict_label', 'varchar(64)', '字典标签'],
      ['dict_value', 'varchar(64)', '字典键值'],
      ['dict_sort', 'int', '排序'],
      ['css_class', 'varchar(64)', '样式类'],
      ['status', 'tinyint', '状态 1启用 0禁用'],
      ['remark', 'varchar(255)', '备注'],
    ],
  },
  {
    t: 'sys_file', c: '文件上传记录', idx: ['biz_type', 'member_id'],
    f: [
      ['file_name', 'varchar(255)', '存储文件名'],
      ['original_name', 'varchar(255)', '原始文件名'],
      ['file_url', 'varchar(512)', '访问地址'],
      ['file_type', 'varchar(64)', '文件类型'],
      ['file_size', 'bigint', '文件大小(字节)'],
      ['storage_driver', 'varchar(32)', '存储驱动 local/s3/oss/cos/minio'],
      ['bucket', 'varchar(128)', '存储桶'],
      ['md5', 'varchar(64)', '文件MD5'],
      ['biz_type', 'varchar(64)', '业务类型'],
      ['member_id', 'bigint', '上传会员ID'],
    ],
  },

  /* ==================== 二、多语言 i18n ==================== */
  {
    t: 'sys_language', c: '语言配置', uk: ['lang_code'],
    f: [
      ['lang_code', 'varchar(16)', '语言编码 zh-CN/en-US'],
      ['lang_name', 'varchar(64)', '语言名称'],
      ['lang_name_en', 'varchar(64)', '语言英文名称'],
      ['flag_icon', 'varchar(255)', '国旗图标'],
      ['is_default', 'tinyint', '是否默认 1是 0否'],
      ['status', 'tinyint', '是否启用 1是 0否'],
      ['sort', 'int', '排序'],
    ],
  },
  {
    t: 'sys_i18n_content', c: '内容多语言字段', idx: ['biz_type', 'biz_id', 'lang_code'],
    f: [
      ['biz_type', 'varchar(64)', '业务类型 service/article/menu/banner/config'],
      ['biz_id', 'bigint', '业务主键'],
      ['lang_code', 'varchar(16)', '语言编码'],
      ['field_key', 'varchar(64)', '字段名'],
      ['field_value', 'longtext', '该语言下的字段内容'],
    ],
  },

  /* ==================== 三、菜单可视化（多端） ==================== */
  {
    t: 'cms_menu', c: '多端可视化菜单', idx: ['platform', 'parent_id'],
    f: [
      ['platform', 'varchar(16)', '端 mp/public/miniapp/h5'],
      ['parent_id', 'bigint', '父级ID'],
      ['menu_name', 'varchar(64)', '菜单名称'],
      ['menu_name_en', 'varchar(64)', '菜单英文名称'],
      ['menu_type', 'varchar(32)', 'view/miniprogram/click/keyword/article/link'],
      ['url', 'varchar(512)', '跳转地址'],
      ['app_id', 'varchar(128)', '小程序AppID'],
      ['page_path', 'varchar(255)', '小程序页面路径'],
      ['key_value', 'varchar(64)', 'click/keyword 关键字'],
      ['icon', 'varchar(255)', '图标'],
      ['sort', 'int', '排序'],
      ['visible', 'tinyint', '是否显示 1是 0否'],
      ['status', 'tinyint', '状态 1启用 0禁用'],
    ],
  },

  /* ==================== 四、门店 / 工位 / 技师 ==================== */
  {
    t: 'store_info', c: '门店信息',
    f: [
      ['store_name', 'varchar(128)', '门店名称'],
      ['store_name_en', 'varchar(128)', '门店英文名称'],
      ['logo', 'varchar(512)', '门店Logo'],
      ['phone', 'varchar(64)', '客服电话'],
      ['address', 'varchar(255)', '门店地址'],
      ['address_en', 'varchar(255)', '门店英文地址'],
      ['longitude', 'varchar(32)', '经度'],
      ['latitude', 'varchar(32)', '纬度'],
      ['business_hours', 'varchar(64)', '营业时间'],
      ['intro', 'text', '门店简介'],
      ['intro_en', 'text', '门店英文简介'],
      ['business_license', 'varchar(512)', '营业执照'],
      ['repair_cert', 'varchar(512)', '机动车维修经营备案'],
      ['status', 'tinyint', '状态 1启用 0禁用'],
    ],
  },
  {
    t: 'work_station', c: '工位', idx: ['store_id'],
    f: [
      ['store_id', 'bigint', '门店ID'],
      ['station_name', 'varchar(64)', '工位名称'],
      ['station_type', 'varchar(32)', '工位类型 机修/保养/轮胎/美容'],
      ['capacity', 'int', '同时开工上限'],
      ['status', 'tinyint', '状态 1启用 0禁用'],
      ['sort', 'int', '排序'],
    ],
  },
  {
    t: 'technician', c: '技师', uk: ['user_id'], idx: ['store_id', 'tech_type'],
    f: [
      ['user_id', 'bigint', '关联系统用户ID'],
      ['store_id', 'bigint', '门店ID'],
      ['tech_name', 'varchar(64)', '技师姓名'],
      ['phone', 'varchar(128)', '手机号(加密)'],
      ['avatar', 'varchar(512)', '头像'],
      ['tech_type', 'varchar(32)', '工种 机修/钣喷/美容/轮胎/通用'],
      ['level', 'varchar(32)', '技师等级 初级/中级/高级/专家'],
      ['cert_no', 'varchar(64)', '资质证书编号'],
      ['qualification', 'varchar(512)', '资质证明图片'],
      ['intro', 'text', '技师简介'],
      ['status', 'tinyint', '状态 1在职 0离职'],
      ['sort', 'int', '排序'],
    ],
    implExtra: `
    @org.springframework.beans.factory.annotation.Autowired
    private com.auto710.service.SysUserService sysUserService;

    @Override
    public boolean save(Technician entity) {
        bindSysUser(entity);
        return super.save(entity);
    }

    @Override
    public boolean updateById(Technician entity) {
        bindSysUser(entity);
        return super.updateById(entity);
    }

    /**
     * 关联系统用户：<b>只绑定，不自动创建。</b>
     * <p>规则：
     * <ol>
     *   <li>表单已选择「关联系统用户」时，直接使用所选账号；</li>
     *   <li>未选择但填写了手机号时，尝试匹配<b>已存在</b>的系统账号（用户名或手机号 = 该手机号）并绑定；</li>
     *   <li>匹配不到则保持未绑定，由管理员先到「系统管理 - 用户」创建账号，再回到本表单选择关联。</li>
     * </ol>
     * 本方法不会向 sys_user / sys_user_role 写入任何数据。</p>
     */
    private void bindSysUser(Technician entity) {
        if (entity.getUserId() != null || cn.hutool.core.util.StrUtil.isBlank(entity.getPhone())) {
            return;
        }
        String phone = entity.getPhone().trim();
        com.auto710.entity.SysUser user = sysUserService.getOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.auto710.entity.SysUser>()
                        .and(w -> w.eq(com.auto710.entity.SysUser::getUsername, phone)
                                .or().eq(com.auto710.entity.SysUser::getPhone, phone))
                        .last("limit 1"));
        if (user != null) {
            entity.setUserId(user.getId());
        }
    }
`,
  },
  {
    t: 'technician_schedule', c: '技师排班', idx: ['technician_id', 'work_date'],
    f: [
      ['technician_id', 'bigint', '技师ID'],
      ['work_date', 'varchar(16)', '排班日期 yyyy-MM-dd'],
      ['shift', 'varchar(16)', '班次 早班/中班/晚班/休息'],
      ['start_time', 'varchar(16)', '开始时间'],
      ['end_time', 'varchar(16)', '结束时间'],
      ['status', 'tinyint', '状态 1有效 0无效'],
    ],
  },

  /* ==================== 五、服务项目 & 价格 ==================== */
  {
    t: 'service_category', c: '服务分类', idx: ['parent_id'],
    f: [
      ['parent_id', 'bigint', '父级ID'],
      ['category_name', 'varchar(64)', '分类名称'],
      ['category_name_en', 'varchar(64)', '分类英文名称'],
      ['icon', 'varchar(512)', '分类图标'],
      ['cover', 'varchar(512)', '分类封面'],
      ['description', 'text', '分类描述'],
      ['sort', 'int', '排序'],
      ['status', 'tinyint', '状态 1上架 0下架'],
    ],
  },
  {
    t: 'service_item', c: '服务项目', idx: ['category_id', 'status'],
    f: [
      ['category_id', 'bigint', '分类ID'],
      ['item_name', 'varchar(128)', '项目名称'],
      ['item_name_en', 'varchar(128)', '项目英文名称'],
      ['item_code', 'varchar(64)', '项目编码'],
      ['cover', 'varchar(512)', '封面图'],
      ['images', 'text', '图集(JSON数组)'],
      ['description', 'longtext', '项目详情(富文本)'],
      ['description_en', 'longtext', '项目详情英文(富文本)'],
      ['labor_fee', 'decimal(10,2)', '工时费'],
      ['price', 'decimal(10,2)', '参考价格'],
      ['price_desc', 'varchar(255)', '价格说明'],
      ['duration', 'int', '所需时长(分钟)'],
      ['support_home', 'tinyint', '是否支持上门 1是 0否'],
      ['bookable', 'tinyint', '是否可预约 1是 0否'],
      ['station_type', 'varchar(32)', '所需工位类型'],
      ['tech_require', 'varchar(255)', '技师资质要求'],
      ['case_images', 'text', '施工案例图(JSON数组)'],
      ['audit_status', 'tinyint', '审核状态 0待审 1通过 2驳回'],
      ['view_count', 'int', '浏览量'],
      ['sort', 'int', '排序'],
      ['status', 'tinyint', '状态 1上架 0下架'],
    ],
  },
  {
    t: 'service_price', c: '价格配置', idx: ['item_id'],
    f: [
      ['item_id', 'bigint', '服务项目ID'],
      ['price_name', 'varchar(64)', '价格名称'],
      ['price_type', 'varchar(32)', '类型 基础/车型/时段'],
      ['price', 'decimal(10,2)', '价格'],
      ['description', 'varchar(255)', '说明'],
      ['status', 'tinyint', '状态 1启用 0禁用'],
    ],
  },

  /* ==================== 六、会员 / 车辆 / 卡券 ==================== */
  {
    t: 'member', c: '会员', idx: ['unionid', 'openid', 'phone'],
    f: [
      ['unionid', 'varchar(128)', '微信UnionID'],
      ['openid', 'varchar(128)', '微信公众号OpenID'],
      ['miniapp_openid', 'varchar(128)', '小程序OpenID'],
      ['h5_openid', 'varchar(128)', 'H5 OpenID'],
      ['nickname', 'varchar(128)', '昵称'],
      ['avatar', 'varchar(512)', '头像'],
      ['real_name', 'varchar(64)', '真实姓名'],
      ['phone', 'varchar(128)', '手机号(加密)'],
      ['password', 'varchar(128)', '登录密码(BCrypt，初始为手机号后6位)'],
      ['need_change_pwd', 'tinyint', '首次登录是否需改密 1是 0否'],
      ['gender', 'tinyint', '性别 0未知 1男 2女'],
      ['birthday', 'varchar(16)', '生日'],
      ['level_id', 'bigint', '会员等级ID'],
      ['points', 'int', '积分'],
      ['balance', 'decimal(12,2)', '储值余额'],
      ['source', 'varchar(16)', '注册来源 mp/miniapp/h5'],
      ['status', 'tinyint', '状态 1正常 0禁用'],
      ['remark', 'varchar(255)', '备注'],
    ],
  },
  {
    t: 'member_vehicle', c: '会员车辆', idx: ['member_id', 'plate_no'],
    f: [
      ['member_id', 'bigint', '会员ID'],
      ['plate_no', 'varchar(128)', '车牌号(加密)'],
      ['plate_color', 'varchar(16)', '车牌颜色'],
      ['brand', 'varchar(64)', '品牌'],
      ['model', 'varchar(128)', '车型'],
      ['vin', 'varchar(128)', '车架号VIN(加密)'],
      ['engine_no', 'varchar(64)', '发动机号'],
      ['mileage', 'int', '当前里程(公里)'],
      ['buy_date', 'varchar(16)', '购车日期'],
      ['is_default', 'tinyint', '是否默认 1是 0否'],
      ['remark', 'varchar(255)', '备注'],
    ],
  },
  {
    t: 'member_level', c: '会员等级',
    f: [
      ['level_name', 'varchar(64)', '等级名称'],
      ['level_code', 'varchar(32)', '等级编码'],
      ['min_points', 'int', '所需最低积分'],
      ['discount', 'decimal(4,2)', '折扣率 如0.95'],
      ['icon', 'varchar(512)', '等级图标'],
      ['status', 'tinyint', '状态 1启用 0禁用'],
      ['sort', 'int', '排序'],
    ],
  },
  {
    t: 'member_points_log', c: '积分流水', idx: ['member_id'],
    f: [
      ['member_id', 'bigint', '会员ID'],
      ['change_points', 'int', '变动积分(正负)'],
      ['balance_points', 'int', '变动后积分'],
      ['type', 'varchar(16)', '类型 获取/消耗'],
      ['source', 'varchar(64)', '来源'],
      ['biz_no', 'varchar(64)', '关联单号'],
      ['remark', 'varchar(255)', '备注'],
    ],
  },
  {
    t: 'member_balance_log', c: '储值流水', idx: ['member_id'],
    f: [
      ['member_id', 'bigint', '会员ID'],
      ['change_amount', 'decimal(12,2)', '变动金额(正负)'],
      ['balance_amount', 'decimal(12,2)', '变动后余额'],
      ['type', 'varchar(16)', '类型 充值/消费/退款/赠送'],
      ['biz_no', 'varchar(64)', '关联单号'],
      ['remark', 'varchar(255)', '备注'],
    ],
  },
  {
    t: 'coupon', c: '优惠券',
    f: [
      ['coupon_name', 'varchar(128)', '优惠券名称'],
      ['coupon_type', 'varchar(32)', '类型 满减/折扣/工时抵扣/洗车券'],
      ['face_value', 'decimal(10,2)', '面额'],
      ['min_amount', 'decimal(10,2)', '使用门槛金额'],
      ['discount', 'decimal(4,2)', '折扣率'],
      ['total_count', 'int', '发行总量 0不限'],
      ['received_count', 'int', '已领取数量'],
      ['used_count', 'int', '已使用数量'],
      ['valid_type', 'varchar(16)', '有效期类型 固定/领取后N天'],
      ['start_time', 'datetime', '生效时间'],
      ['end_time', 'datetime', '失效时间'],
      ['valid_days', 'int', '领取后有效天数'],
      ['scope', 'varchar(255)', '适用范围'],
      ['description', 'varchar(255)', '使用说明'],
      ['status', 'tinyint', '状态 1启用 0停用'],
    ],
  },
  {
    t: 'member_coupon', c: '会员优惠券', idx: ['member_id', 'coupon_id'],
    f: [
      ['member_id', 'bigint', '会员ID'],
      ['coupon_id', 'bigint', '优惠券ID'],
      ['coupon_code', 'varchar(64)', '券码'],
      ['status', 'varchar(16)', '状态 未使用/已使用/已过期'],
      ['biz_no', 'varchar(64)', '使用订单号'],
      ['receive_time', 'datetime', '领取时间'],
      ['use_time', 'datetime', '使用时间'],
      ['expire_time', 'datetime', '过期时间'],
    ],
  },
  {
    t: 'maintenance_package', c: '养护套餐',
    f: [
      ['package_name', 'varchar(128)', '套餐名称'],
      ['package_name_en', 'varchar(128)', '套餐英文名称'],
      ['cover', 'varchar(512)', '封面'],
      ['description', 'text', '套餐说明'],
      ['price', 'decimal(10,2)', '套餐价'],
      ['original_price', 'decimal(10,2)', '原价'],
      ['times', 'int', '包含次数'],
      ['valid_days', 'int', '有效天数'],
      ['items', 'text', '包含项目(JSON)'],
      ['status', 'tinyint', '状态 1上架 0下架'],
      ['sort', 'int', '排序'],
    ],
  },
  {
    t: 'member_package', c: '会员套餐', idx: ['member_id'],
    f: [
      ['member_id', 'bigint', '会员ID'],
      ['package_id', 'bigint', '套餐ID'],
      ['package_name', 'varchar(128)', '套餐名称'],
      ['total_times', 'int', '总次数'],
      ['used_times', 'int', '已用次数'],
      ['remain_times', 'int', '剩余次数'],
      ['buy_time', 'datetime', '购买时间'],
      ['expire_time', 'datetime', '到期时间'],
      ['status', 'tinyint', '状态 1有效 0失效'],
    ],
  },

  /* ==================== 七、营销活动 ==================== */
  {
    t: 'marketing_activity', c: '营销活动',
    f: [
      ['activity_name', 'varchar(128)', '活动名称'],
      ['activity_type', 'varchar(32)', '类型 拼团/节日/充值赠/新人'],
      ['cover', 'varchar(512)', '活动封面'],
      ['description', 'text', '活动说明'],
      ['start_time', 'datetime', '开始时间'],
      ['end_time', 'datetime', '结束时间'],
      ['rules', 'text', '活动规则(JSON)'],
      ['status', 'tinyint', '状态 1进行中 0已结束'],
    ],
  },
  {
    t: 'recharge_rule', c: '储值充赠规则',
    f: [
      ['rule_name', 'varchar(64)', '规则名称'],
      ['recharge_amount', 'decimal(10,2)', '充值金额'],
      ['gift_amount', 'decimal(10,2)', '赠送金额'],
      ['gift_points', 'int', '赠送积分'],
      ['status', 'tinyint', '状态 1启用 0禁用'],
      ['sort', 'int', '排序'],
    ],
  },

  /* ==================== 八、预约 / 工单 ==================== */
  {
    t: 'appointment', c: '预约单', uk: ['appointment_no'], idx: ['member_id', 'status'],
    f: [
      ['appointment_no', 'varchar(32)', '预约单号'],
      ['member_id', 'bigint', '会员ID'],
      ['store_id', 'bigint', '门店ID'],
      ['item_id', 'bigint', '服务项目ID'],
      ['item_name', 'varchar(128)', '服务项目名称'],
      ['vehicle_id', 'bigint', '车辆ID'],
      ['plate_no', 'varchar(128)', '车牌号(加密)'],
      ['brand_model', 'varchar(128)', '品牌车型'],
      ['contact_name', 'varchar(64)', '联系人'],
      ['contact_phone', 'varchar(128)', '联系电话(加密)'],
      ['appoint_time', 'datetime', '预约到店时间'],
      ['remark', 'text', '客户备注'],
      ['status', 'varchar(16)', '状态 待确认/已确认/待接车/已完成/已取消'],
      ['source', 'varchar(16)', '来源 miniapp/h5/mp/admin'],
      ['cancel_reason', 'varchar(255)', '取消原因'],
      ['confirm_time', 'datetime', '确认时间'],
    ],
    implExtra: `
    @Override
    public boolean save(Appointment entity) {
        encode(entity, true);
        return super.save(entity);
    }

    @Override
    public boolean updateById(Appointment entity) {
        encode(entity, false);
        return super.updateById(entity);
    }

    /**
     * 新增补全单号/默认状态；车牌与手机号加密入库（已是密文则跳过，避免二次加密）。
     * 后台「预约管理」与会员端下单共用同一 Service，保证两条写入路径行为一致。
     */
    private void encode(Appointment entity, boolean insert) {
        if (insert && cn.hutool.core.util.StrUtil.isBlank(entity.getAppointmentNo())) {
            entity.setAppointmentNo(com.auto710.common.util.IdGenUtil.appointmentNo());
        }
        if (insert && cn.hutool.core.util.StrUtil.isBlank(entity.getStatus())) {
            entity.setStatus(com.auto710.common.constant.AppointmentStatus.WAIT_CONFIRM);
        }
        if (!com.auto710.common.util.EncryptUtil.isEncrypted(entity.getPlateNo())) {
            entity.setPlateNo(com.auto710.common.util.EncryptUtil.encrypt(entity.getPlateNo()));
        }
        if (!com.auto710.common.util.EncryptUtil.isEncrypted(entity.getContactPhone())) {
            entity.setContactPhone(com.auto710.common.util.EncryptUtil.encrypt(entity.getContactPhone()));
        }
    }
`,
  },
  {
    t: 'work_order', c: '维修工单', uk: ['order_no'], idx: ['member_id', 'status', 'technician_id'],
    f: [
      ['order_no', 'varchar(32)', '工单号'],
      ['appointment_id', 'bigint', '关联预约单ID'],
      ['member_id', 'bigint', '会员ID'],
      ['store_id', 'bigint', '门店ID'],
      ['vehicle_id', 'bigint', '车辆ID'],
      ['plate_no', 'varchar(128)', '车牌号(加密)'],
      ['brand_model', 'varchar(128)', '品牌车型'],
      ['mileage', 'int', '进厂里程'],
      ['contact_name', 'varchar(64)', '联系人'],
      ['contact_phone', 'varchar(128)', '联系电话(加密)'],
      ['fault_desc', 'text', '故障描述/客户诉求'],
      ['service_items', 'text', '服务项目(JSON)'],
      ['status', 'varchar(16)', '状态 待接车/待派工/施工中/待质检/待结算/已离店/已评价'],
      ['total_amount', 'decimal(12,2)', '工单总额'],
      ['labor_amount', 'decimal(12,2)', '工时费合计'],
      ['parts_amount', 'decimal(12,2)', '配件费合计'],
      ['discount_amount', 'decimal(12,2)', '优惠金额'],
      ['paid_amount', 'decimal(12,2)', '实收金额'],
      ['pay_type', 'varchar(32)', '支付方式 微信/储值/现金/混合'],
      ['pay_time', 'datetime', '支付时间'],
      ['receive_time', 'datetime', '接车时间'],
      ['start_time', 'datetime', '开工时间'],
      ['finish_time', 'datetime', '完工时间'],
      ['deliver_time', 'datetime', '交车时间'],
      ['technician_id', 'bigint', '主责技师ID'],
      ['is_urgent', 'tinyint', '是否加急 1是 0否'],
      ['evaluate_status', 'tinyint', '评价状态 0未评 1已评'],
      ['remark', 'text', '备注'],
    ],
  },
  {
    t: 'work_order_item', c: '工单明细', idx: ['work_order_id'],
    f: [
      ['work_order_id', 'bigint', '工单ID'],
      ['item_type', 'varchar(16)', '类型 服务/配件'],
      ['item_id', 'bigint', '项目/配件ID'],
      ['item_name', 'varchar(128)', '名称'],
      ['unit', 'varchar(16)', '单位'],
      ['quantity', 'decimal(10,2)', '数量'],
      ['price', 'decimal(10,2)', '单价'],
      ['amount', 'decimal(10,2)', '金额'],
      ['technician_id', 'bigint', '施工技师ID'],
      ['remark', 'varchar(255)', '备注'],
    ],
  },
  {
    t: 'work_order_log', c: '工单状态流转日志', idx: ['work_order_id'],
    f: [
      ['work_order_id', 'bigint', '工单ID'],
      ['from_status', 'varchar(16)', '原状态'],
      ['to_status', 'varchar(16)', '新状态'],
      ['operator_id', 'bigint', '操作人ID'],
      ['operator_name', 'varchar(64)', '操作人'],
      ['remark', 'varchar(255)', '备注'],
    ],
  },
  {
    t: 'work_order_check', c: '接车检查/报价', idx: ['work_order_id'],
    f: [
      ['work_order_id', 'bigint', '工单ID'],
      ['check_type', 'varchar(16)', '类型 检查项/报价项'],
      ['check_name', 'varchar(128)', '检查项名称'],
      ['check_result', 'varchar(255)', '检查结果'],
      ['quote_price', 'decimal(10,2)', '报价'],
      ['customer_confirm', 'tinyint', '客户是否确认 1是 0否'],
      ['confirm_time', 'datetime', '确认时间'],
      ['images', 'text', '图片(JSON)'],
      ['remark', 'varchar(255)', '备注'],
    ],
  },
  {
    t: 'dispatch_record', c: '派工记录', idx: ['work_order_id', 'technician_id'],
    f: [
      ['work_order_id', 'bigint', '工单ID'],
      ['technician_id', 'bigint', '技师ID'],
      ['technician_name', 'varchar(64)', '技师姓名'],
      ['dispatch_type', 'varchar(16)', '类型 派工/接单/完工'],
      ['dispatch_time', 'datetime', '操作时间'],
      ['status', 'varchar(16)', '状态'],
      ['remark', 'varchar(255)', '备注'],
    ],
  },
  {
    t: 'refund_record', c: '退款记录', uk: ['refund_no'], idx: ['work_order_id', 'member_id'],
    f: [
      ['refund_no', 'varchar(32)', '退款单号'],
      ['work_order_id', 'bigint', '工单ID'],
      ['order_no', 'varchar(32)', '原订单号'],
      ['member_id', 'bigint', '会员ID'],
      ['refund_amount', 'decimal(12,2)', '退款金额'],
      ['reason', 'varchar(255)', '退款原因'],
      ['images', 'text', '凭证图片(JSON)'],
      ['status', 'varchar(16)', '状态 待审核/已通过/已拒绝/已退款'],
      ['audit_user_id', 'bigint', '审核人ID'],
      ['audit_time', 'datetime', '审核时间'],
      ['audit_remark', 'varchar(255)', '审核备注'],
    ],
  },
  {
    t: 'work_order_evaluate', c: '工单评价', idx: ['work_order_id', 'member_id'],
    f: [
      ['work_order_id', 'bigint', '工单ID'],
      ['member_id', 'bigint', '会员ID'],
      ['score', 'int', '综合评分 1-5'],
      ['service_score', 'int', '服务评分'],
      ['tech_score', 'int', '技师评分'],
      ['content', 'varchar(512)', '评价内容'],
      ['images', 'text', '评价图片(JSON)'],
      ['reply', 'varchar(512)', '门店回复'],
      ['reply_time', 'datetime', '回复时间'],
      ['status', 'tinyint', '状态 1显示 0隐藏'],
    ],
  },

  /* ==================== 九、存胎寄存 ==================== */
  {
    t: 'tire_storage_order', c: '存胎寄存单', uk: ['storage_no'], idx: ['member_id', 'status'],
    f: [
      ['storage_no', 'varchar(32)', '寄存单号'],
      ['member_id', 'bigint', '会员ID'],
      ['store_id', 'bigint', '门店ID'],
      ['vehicle_id', 'bigint', '车辆ID'],
      ['plate_no', 'varchar(128)', '车牌号(加密)'],
      ['tire_count', 'int', '轮胎数量'],
      ['tire_type', 'varchar(32)', '轮胎类型 冬胎/夏胎/四季胎'],
      ['tire_brand', 'varchar(64)', '轮胎品牌'],
      ['tire_size', 'varchar(64)', '轮胎规格'],
      ['storage_fee', 'decimal(10,2)', '寄存费'],
      ['overdue_fee', 'decimal(10,2)', '超期保管费'],
      ['position_no', 'varchar(32)', '寄存货位号'],
      ['start_date', 'varchar(16)', '寄存开始日期'],
      ['end_date', 'varchar(16)', '预计取胎日期'],
      ['real_out_date', 'varchar(16)', '实际取胎日期'],
      ['remind_preference', 'varchar(64)', '取胎提醒偏好'],
      ['status', 'varchar(16)', '状态 已寄存/寄存中/待取胎/已取胎/超期'],
      ['remark', 'varchar(255)', '备注'],
    ],
  },
  {
    t: 'tire_storage_item', c: '寄存轮胎明细', idx: ['storage_order_id'],
    f: [
      ['storage_order_id', 'bigint', '寄存单ID'],
      ['tire_position', 'varchar(16)', '位置 左前/右前/左后/右后/备胎'],
      ['tire_brand', 'varchar(64)', '品牌'],
      ['tire_size', 'varchar(64)', '规格'],
      ['tread_depth', 'varchar(16)', '花纹深度'],
      ['images', 'text', '照片(JSON)'],
      ['remark', 'varchar(255)', '备注'],
    ],
  },
  {
    t: 'tire_storage_proof', c: '寄存凭证', idx: ['storage_order_id'],
    f: [
      ['storage_order_id', 'bigint', '寄存单ID'],
      ['proof_type', 'varchar(16)', '类型 入库/取胎'],
      ['images', 'text', '凭证图片(JSON)'],
      ['operator_id', 'bigint', '操作人ID'],
      ['operator_name', 'varchar(64)', '操作人'],
      ['proof_time', 'datetime', '凭证时间'],
      ['remark', 'varchar(255)', '备注'],
    ],
  },
  {
    t: 'tire_storage_stock', c: '寄存库存', idx: ['store_id', 'position_no'],
    f: [
      ['store_id', 'bigint', '门店ID'],
      ['position_no', 'varchar(32)', '货位号'],
      ['storage_order_id', 'bigint', '寄存单ID'],
      ['plate_no', 'varchar(128)', '车牌号(加密)'],
      ['tire_count', 'int', '轮胎数量'],
      ['status', 'varchar(16)', '状态 在库/已出库'],
      ['in_time', 'datetime', '入库时间'],
      ['out_time', 'datetime', '出库时间'],
    ],
  },

  /* ==================== 十、配件 / 库存 ==================== */
  {
    t: 'part_category', c: '配件分类', idx: ['parent_id'],
    f: [
      ['parent_id', 'bigint', '父级ID'],
      ['category_name', 'varchar(64)', '分类名称'],
      ['sort', 'int', '排序'],
      ['status', 'tinyint', '状态 1启用 0禁用'],
    ],
  },
  {
    t: 'part_info', c: '配件耗材', uk: ['part_code'], idx: ['category_id'],
    f: [
      ['part_code', 'varchar(64)', '配件编码'],
      ['part_name', 'varchar(128)', '配件名称'],
      ['category_id', 'bigint', '分类ID'],
      ['brand', 'varchar(64)', '品牌'],
      ['spec', 'varchar(128)', '规格型号'],
      ['unit', 'varchar(16)', '单位'],
      ['purchase_price', 'decimal(10,2)', '成本价'],
      ['sale_price', 'decimal(10,2)', '售价'],
      ['stock', 'int', '当前库存'],
      ['warn_stock', 'int', '预警库存'],
      ['cover', 'varchar(512)', '图片'],
      ['supplier', 'varchar(128)', '供应商'],
      ['status', 'tinyint', '状态 1启用 0禁用'],
      ['remark', 'varchar(255)', '备注'],
    ],
  },
  {
    t: 'part_stock_record', c: '配件出入库记录', idx: ['part_id', 'work_order_id'],
    f: [
      ['part_id', 'bigint', '配件ID'],
      ['part_name', 'varchar(128)', '配件名称'],
      ['record_type', 'varchar(16)', '类型 入库/出库/盘点/退货'],
      ['quantity', 'int', '数量'],
      ['before_stock', 'int', '变动前库存'],
      ['after_stock', 'int', '变动后库存'],
      ['unit_price', 'decimal(10,2)', '单价'],
      ['amount', 'decimal(10,2)', '金额'],
      ['work_order_id', 'bigint', '关联工单ID'],
      ['order_no', 'varchar(32)', '关联单号'],
      ['operator_id', 'bigint', '操作人ID'],
      ['operator_name', 'varchar(64)', '操作人'],
      ['remark', 'varchar(255)', '备注'],
    ],
  },
  {
    t: 'part_supplier', c: '配件供应商',
    f: [
      ['user_id', 'bigint', '关联系统用户ID(供应商端登录账号)'],
      ['supplier_name', 'varchar(128)', '供应商名称'],
      ['contact_name', 'varchar(64)', '联系人'],
      ['phone', 'varchar(64)', '联系电话'],
      ['address', 'varchar(255)', '地址'],
      ['remark', 'varchar(255)', '备注'],
      ['status', 'tinyint', '状态 1启用 0禁用'],
    ],
  },
  {
    t: 'supplier_delivery', c: '供应商送货单', uk: ['delivery_no'], idx: ['supplier_id', 'status'],
    f: [
      ['delivery_no', 'varchar(32)', '送货单号'],
      ['supplier_id', 'bigint', '供应商ID'],
      ['supplier_name', 'varchar(128)', '供应商名称'],
      ['store_id', 'bigint', '收货门店ID'],
      ['item_count', 'int', '明细条数'],
      ['total_amount', 'decimal(12,2)', '合计金额'],
      ['expect_date', 'varchar(16)', '预计到货日期'],
      ['status', 'varchar(16)', '状态 待确认/已入库/已拒绝'],
      ['submit_time', 'datetime', '提交时间'],
      ['confirm_user_id', 'bigint', '确认人ID'],
      ['confirm_time', 'datetime', '确认时间'],
      ['confirm_remark', 'varchar(255)', '确认备注'],
      ['remark', 'varchar(255)', '备注'],
    ],
  },
  {
    t: 'supplier_delivery_item', c: '供应商送货明细', idx: ['delivery_id', 'part_id'],
    f: [
      ['delivery_id', 'bigint', '送货单ID'],
      ['part_id', 'bigint', '配件ID'],
      ['part_name', 'varchar(128)', '配件名称'],
      ['spec', 'varchar(128)', '规格型号'],
      ['unit', 'varchar(16)', '单位'],
      ['quantity', 'int', '数量'],
      ['unit_price', 'decimal(10,2)', '单价'],
      ['amount', 'decimal(12,2)', '金额'],
      ['remark', 'varchar(255)', '备注'],
    ],
  },

  /* ==================== 十一、官网 CMS ==================== */
  {
    t: 'cms_article_category', c: '文章分类', idx: ['parent_id'],
    f: [
      ['parent_id', 'bigint', '父级ID'],
      ['category_name', 'varchar(64)', '分类名称'],
      ['category_name_en', 'varchar(64)', '分类英文名称'],
      ['sort', 'int', '排序'],
      ['status', 'tinyint', '状态 1启用 0禁用'],
    ],
  },
  {
    t: 'cms_article', c: '养护知识文章', idx: ['category_id', 'status'],
    f: [
      ['category_id', 'bigint', '分类ID'],
      ['title', 'varchar(255)', '标题'],
      ['title_en', 'varchar(255)', '标题英文'],
      ['cover', 'varchar(512)', '封面图'],
      ['summary', 'varchar(512)', '摘要'],
      ['summary_en', 'varchar(512)', '摘要英文'],
      ['content', 'longtext', '正文(富文本)'],
      ['content_en', 'longtext', '正文英文(富文本)'],
      ['author', 'varchar(64)', '作者'],
      ['source', 'varchar(128)', '来源'],
      ['seo_title', 'varchar(255)', 'SEO标题'],
      ['seo_keywords', 'varchar(255)', 'SEO关键词'],
      ['seo_description', 'varchar(512)', 'SEO描述'],
      ['tags', 'varchar(255)', '标签'],
      ['view_count', 'int', '浏览量'],
      ['is_top', 'tinyint', '是否置顶 1是 0否'],
      ['status', 'tinyint', '状态 1发布 0草稿'],
      ['publish_time', 'datetime', '发布时间'],
    ],
  },
  {
    t: 'cms_brand_story', c: '品牌故事',
    f: [
      ['title', 'varchar(255)', '标题'],
      ['title_en', 'varchar(255)', '标题英文'],
      ['cover', 'varchar(512)', '封面'],
      ['content', 'longtext', '内容(富文本)'],
      ['content_en', 'longtext', '英文内容(富文本)'],
      ['images', 'text', '图集(JSON)'],
      ['sort', 'int', '排序'],
      ['status', 'tinyint', '状态 1启用 0禁用'],
    ],
  },
  {
    t: 'cms_case', c: '施工案例', idx: ['case_type', 'status'],
    f: [
      ['case_name', 'varchar(255)', '案例名称'],
      ['case_name_en', 'varchar(255)', '案例英文名称'],
      ['case_type', 'varchar(32)', '类型 维修/保养/美容/存胎'],
      ['cover', 'varchar(512)', '封面'],
      ['before_images', 'text', '施工前图(JSON)'],
      ['after_images', 'text', '施工后图(JSON)'],
      ['content', 'longtext', '案例描述'],
      ['content_en', 'longtext', '英文描述'],
      ['service_item_id', 'bigint', '关联服务项目ID'],
      ['view_count', 'int', '浏览量'],
      ['sort', 'int', '排序'],
      ['status', 'tinyint', '状态 1上架 0下架'],
    ],
  },
  {
    t: 'cms_banner', c: '轮播图', idx: ['platform'],
    f: [
      ['platform', 'varchar(16)', '端 home/service/www'],
      ['title', 'varchar(255)', '标题'],
      ['title_en', 'varchar(255)', '标题英文'],
      ['image', 'varchar(512)', '图片'],
      ['link', 'varchar(512)', '跳转链接'],
      ['sort', 'int', '排序'],
      ['status', 'tinyint', '状态 1启用 0禁用'],
    ],
  },
  {
    t: 'cms_message', c: '在线留言', idx: ['status'],
    f: [
      ['member_id', 'bigint', '会员ID'],
      ['name', 'varchar(64)', '姓名'],
      ['phone', 'varchar(128)', '手机号(加密)'],
      ['email', 'varchar(128)', '邮箱'],
      ['content', 'text', '留言内容'],
      ['source', 'varchar(16)', '来源 www/miniapp/h5'],
      ['status', 'tinyint', '状态 0未处理 1已处理'],
      ['reply', 'text', '回复内容'],
      ['reply_time', 'datetime', '回复时间'],
      ['ip', 'varchar(64)', '提交IP'],
    ],
  },

  /* ==================== 十二、微信公众号 ==================== */
  {
    t: 'wechat_fans', c: '公众号粉丝', idx: ['openid', 'unionid'],
    f: [
      ['openid', 'varchar(128)', 'OpenID'],
      ['unionid', 'varchar(128)', 'UnionID'],
      ['nickname', 'varchar(128)', '昵称'],
      ['avatar', 'varchar(512)', '头像'],
      ['gender', 'tinyint', '性别'],
      ['subscribe', 'tinyint', '是否关注 1是 0否'],
      ['subscribe_time', 'datetime', '关注时间'],
      ['tags', 'varchar(255)', '标签'],
      ['remark', 'varchar(255)', '备注'],
    ],
  },
  {
    t: 'wechat_material', c: '公众号素材',
    f: [
      ['material_type', 'varchar(16)', '类型 图文/图片/视频'],
      ['title', 'varchar(255)', '标题'],
      ['media_id', 'varchar(128)', '微信媒体ID'],
      ['url', 'varchar(512)', '素材地址'],
      ['thumb_url', 'varchar(512)', '缩略图'],
      ['content', 'text', '图文内容'],
      ['expire_time', 'datetime', '过期时间'],
      ['status', 'tinyint', '状态 1有效 0失效'],
    ],
  },
  {
    t: 'wechat_template_msg', c: '模板消息配置',
    f: [
      ['msg_type', 'varchar(64)', '消息类型 预约确认/接车/施工进度/完工/结算/存胎到期/退款'],
      ['template_id', 'varchar(128)', '微信模板ID'],
      ['title', 'varchar(128)', '模板标题'],
      ['content_template', 'text', '内容模板(变量占位)'],
      ['status', 'tinyint', '状态 1启用 0禁用'],
      ['remark', 'varchar(255)', '备注'],
    ],
  },
  {
    t: 'wechat_reply_rule', c: '公众号回复规则',
    f: [
      ['rule_type', 'varchar(16)', '类型 关注/关键词/默认'],
      ['keyword', 'varchar(128)', '关键词'],
      ['match_type', 'varchar(16)', '匹配方式 全匹配/半匹配'],
      ['reply_type', 'varchar(16)', '回复类型 文本/图文/图片'],
      ['content', 'text', '回复内容'],
      ['material_id', 'bigint', '关联素材ID'],
      ['status', 'tinyint', '状态 1启用 0禁用'],
      ['sort', 'int', '排序'],
    ],
  },

  /* ==================== 十三、统计 ==================== */
  {
    t: 'stat_daily', c: '每日经营统计', uk: ['stat_date'],
    f: [
      ['stat_date', 'varchar(16)', '统计日期 yyyy-MM-dd'],
      ['uv', 'int', '访问人数UV'],
      ['pv', 'int', '访问量PV'],
      ['appointment_count', 'int', '预约量'],
      ['order_count', 'int', '工单量'],
      ['revenue', 'decimal(12,2)', '营收'],
      ['gross_profit', 'decimal(12,2)', '毛利'],
      ['tire_count', 'int', '存胎量'],
      ['member_count', 'int', '新增会员'],
      ['recharge_amount', 'decimal(12,2)', '储值充值额'],
    ],
  },
];

module.exports = { TABLES };
