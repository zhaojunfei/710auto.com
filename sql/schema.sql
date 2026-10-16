-- =====================================================================
-- 710号汽车之家 数字化多端平台 数据库初始化脚本 (MySQL 8.0 / utf8mb4)
-- 自动生成，请勿手工修改；修改表定义请编辑 tools/tables.js 后重新执行 node tools/generate.js
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `auto_repair` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `auto_repair`;
SET NAMES utf8mb4;

-- 应用数据库账号：本地手工初始化时需要；docker 首次初始化由镜像环境变量自动创建，此处为幂等兜底
CREATE USER IF NOT EXISTS 'auto_repair_app'@'%' IDENTIFIED BY 'auto710@2026';
CREATE USER IF NOT EXISTS 'auto_repair_app'@'localhost' IDENTIFIED BY 'auto710@2026';
GRANT ALL PRIVILEGES ON `auto_repair`.* TO 'auto_repair_app'@'%';
GRANT ALL PRIVILEGES ON `auto_repair`.* TO 'auto_repair_app'@'localhost';
FLUSH PRIVILEGES;
-- 系统用户
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` VARCHAR(64) DEFAULT NULL COMMENT '登录账号',
  `password` VARCHAR(128) DEFAULT NULL COMMENT '密码(BCrypt)',
  `real_name` VARCHAR(64) DEFAULT NULL COMMENT '真实姓名',
  `phone` VARCHAR(128) DEFAULT NULL COMMENT '手机号(加密)',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
  `email` VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
  `store_id` BIGINT DEFAULT NULL COMMENT '所属门店',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1启用 0禁用',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_username` (`username`),
  KEY `idx_sys_user_store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统用户';

-- 系统角色
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_name` VARCHAR(64) DEFAULT NULL COMMENT '角色名称',
  `role_code` VARCHAR(64) DEFAULT NULL COMMENT '角色编码',
  `data_scope` TINYINT DEFAULT NULL COMMENT '数据范围 1全部 2本店 3本人',
  `sort` INT DEFAULT NULL COMMENT '排序',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1启用 0禁用',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_role_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统角色';

-- 用户角色关联
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT DEFAULT NULL COMMENT '用户ID',
  `role_id` BIGINT DEFAULT NULL COMMENT '角色ID',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_sys_user_role_user_id` (`user_id`),
  KEY `idx_sys_user_role_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户角色关联';

-- 系统菜单权限
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父级ID',
  `menu_name` VARCHAR(64) DEFAULT NULL COMMENT '菜单名称',
  `menu_type` VARCHAR(16) DEFAULT NULL COMMENT '类型 DIR/MENU/BUTTON',
  `path` VARCHAR(255) DEFAULT NULL COMMENT '路由地址',
  `component` VARCHAR(255) DEFAULT NULL COMMENT '组件路径',
  `perms` VARCHAR(128) DEFAULT NULL COMMENT '权限标识',
  `icon` VARCHAR(64) DEFAULT NULL COMMENT '图标',
  `sort` INT DEFAULT NULL COMMENT '排序',
  `visible` TINYINT DEFAULT NULL COMMENT '是否显示 1是 0否',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1启用 0禁用',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_sys_menu_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统菜单权限';

-- 角色菜单关联
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_id` BIGINT DEFAULT NULL COMMENT '角色ID',
  `menu_id` BIGINT DEFAULT NULL COMMENT '菜单ID',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_sys_role_menu_role_id` (`role_id`),
  KEY `idx_sys_role_menu_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色菜单关联';

-- 操作审计日志
DROP TABLE IF EXISTS `sys_audit_log`;
CREATE TABLE `sys_audit_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
  `username` VARCHAR(64) DEFAULT NULL COMMENT '操作人账号',
  `module` VARCHAR(64) DEFAULT NULL COMMENT '模块',
  `operation` VARCHAR(64) DEFAULT NULL COMMENT '操作类型',
  `method` VARCHAR(255) DEFAULT NULL COMMENT '请求方法',
  `request_url` VARCHAR(255) DEFAULT NULL COMMENT '请求地址',
  `request_method` VARCHAR(16) DEFAULT NULL COMMENT '请求方式',
  `request_param` TEXT COMMENT '请求参数',
  `response_result` TEXT COMMENT '返回结果',
  `ip` VARCHAR(64) DEFAULT NULL COMMENT '操作IP',
  `user_agent` VARCHAR(512) DEFAULT NULL COMMENT '浏览器UA',
  `cost_time` BIGINT DEFAULT NULL COMMENT '耗时(ms)',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1成功 0失败',
  `error_msg` TEXT COMMENT '异常信息',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_sys_audit_log_user_id` (`user_id`),
  KEY `idx_sys_audit_log_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='操作审计日志';

-- 系统全局配置
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `config_key` VARCHAR(128) DEFAULT NULL COMMENT '配置键',
  `config_value` TEXT COMMENT '配置值',
  `config_name` VARCHAR(128) DEFAULT NULL COMMENT '配置名称',
  `config_group` VARCHAR(64) DEFAULT NULL COMMENT '配置分组',
  `config_type` VARCHAR(32) DEFAULT NULL COMMENT '值类型 string/int/bool/json/secret',
  `encrypted` TINYINT DEFAULT NULL COMMENT '是否加密 1是 0否',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_config_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统全局配置';

-- 字典类型
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dict_type` VARCHAR(64) DEFAULT NULL COMMENT '字典类型编码',
  `dict_name` VARCHAR(64) DEFAULT NULL COMMENT '字典名称',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1启用 0禁用',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_dict_type_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典类型';

-- 字典数据
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dict_type` VARCHAR(64) DEFAULT NULL COMMENT '字典类型编码',
  `dict_label` VARCHAR(64) DEFAULT NULL COMMENT '字典标签',
  `dict_value` VARCHAR(64) DEFAULT NULL COMMENT '字典键值',
  `dict_sort` INT DEFAULT NULL COMMENT '排序',
  `css_class` VARCHAR(64) DEFAULT NULL COMMENT '样式类',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1启用 0禁用',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_sys_dict_data_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典数据';

-- 文件上传记录
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `file_name` VARCHAR(255) DEFAULT NULL COMMENT '存储文件名',
  `original_name` VARCHAR(255) DEFAULT NULL COMMENT '原始文件名',
  `file_url` VARCHAR(512) DEFAULT NULL COMMENT '访问地址',
  `file_type` VARCHAR(64) DEFAULT NULL COMMENT '文件类型',
  `file_size` BIGINT DEFAULT NULL COMMENT '文件大小(字节)',
  `storage_driver` VARCHAR(32) DEFAULT NULL COMMENT '存储驱动 local/s3/oss/cos/minio',
  `bucket` VARCHAR(128) DEFAULT NULL COMMENT '存储桶',
  `md5` VARCHAR(64) DEFAULT NULL COMMENT '文件MD5',
  `biz_type` VARCHAR(64) DEFAULT NULL COMMENT '业务类型',
  `member_id` BIGINT DEFAULT NULL COMMENT '上传会员ID',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_sys_file_biz_type` (`biz_type`),
  KEY `idx_sys_file_member_id` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文件上传记录';

-- 语言配置
DROP TABLE IF EXISTS `sys_language`;
CREATE TABLE `sys_language` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `lang_code` VARCHAR(16) DEFAULT NULL COMMENT '语言编码 zh-CN/en-US',
  `lang_name` VARCHAR(64) DEFAULT NULL COMMENT '语言名称',
  `lang_name_en` VARCHAR(64) DEFAULT NULL COMMENT '语言英文名称',
  `flag_icon` VARCHAR(255) DEFAULT NULL COMMENT '国旗图标',
  `is_default` TINYINT DEFAULT NULL COMMENT '是否默认 1是 0否',
  `status` TINYINT DEFAULT NULL COMMENT '是否启用 1是 0否',
  `sort` INT DEFAULT NULL COMMENT '排序',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_language_lang_code` (`lang_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='语言配置';

-- 内容多语言字段
DROP TABLE IF EXISTS `sys_i18n_content`;
CREATE TABLE `sys_i18n_content` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_type` VARCHAR(64) DEFAULT NULL COMMENT '业务类型 service/article/menu/banner/config',
  `biz_id` BIGINT DEFAULT NULL COMMENT '业务主键',
  `lang_code` VARCHAR(16) DEFAULT NULL COMMENT '语言编码',
  `field_key` VARCHAR(64) DEFAULT NULL COMMENT '字段名',
  `field_value` LONGTEXT COMMENT '该语言下的字段内容',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_sys_i18n_content_biz_type` (`biz_type`),
  KEY `idx_sys_i18n_content_biz_id` (`biz_id`),
  KEY `idx_sys_i18n_content_lang_code` (`lang_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='内容多语言字段';

-- 多端可视化菜单
DROP TABLE IF EXISTS `cms_menu`;
CREATE TABLE `cms_menu` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `platform` VARCHAR(16) DEFAULT NULL COMMENT '端 mp/public/miniapp/h5',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父级ID',
  `menu_name` VARCHAR(64) DEFAULT NULL COMMENT '菜单名称',
  `menu_name_en` VARCHAR(64) DEFAULT NULL COMMENT '菜单英文名称',
  `menu_type` VARCHAR(32) DEFAULT NULL COMMENT 'view/miniprogram/click/keyword/article/link',
  `url` VARCHAR(512) DEFAULT NULL COMMENT '跳转地址',
  `app_id` VARCHAR(128) DEFAULT NULL COMMENT '小程序AppID',
  `page_path` VARCHAR(255) DEFAULT NULL COMMENT '小程序页面路径',
  `key_value` VARCHAR(64) DEFAULT NULL COMMENT 'click/keyword 关键字',
  `icon` VARCHAR(255) DEFAULT NULL COMMENT '图标',
  `sort` INT DEFAULT NULL COMMENT '排序',
  `visible` TINYINT DEFAULT NULL COMMENT '是否显示 1是 0否',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1启用 0禁用',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_cms_menu_platform` (`platform`),
  KEY `idx_cms_menu_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='多端可视化菜单';

-- 门店信息
DROP TABLE IF EXISTS `store_info`;
CREATE TABLE `store_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `store_name` VARCHAR(128) DEFAULT NULL COMMENT '门店名称',
  `store_name_en` VARCHAR(128) DEFAULT NULL COMMENT '门店英文名称',
  `logo` VARCHAR(512) DEFAULT NULL COMMENT '门店Logo',
  `phone` VARCHAR(64) DEFAULT NULL COMMENT '客服电话',
  `address` VARCHAR(255) DEFAULT NULL COMMENT '门店地址',
  `address_en` VARCHAR(255) DEFAULT NULL COMMENT '门店英文地址',
  `longitude` VARCHAR(32) DEFAULT NULL COMMENT '经度',
  `latitude` VARCHAR(32) DEFAULT NULL COMMENT '纬度',
  `business_hours` VARCHAR(64) DEFAULT NULL COMMENT '营业时间',
  `intro` TEXT COMMENT '门店简介',
  `intro_en` TEXT COMMENT '门店英文简介',
  `business_license` VARCHAR(512) DEFAULT NULL COMMENT '营业执照',
  `repair_cert` VARCHAR(512) DEFAULT NULL COMMENT '机动车维修经营备案',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1启用 0禁用',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='门店信息';

-- 工位
DROP TABLE IF EXISTS `work_station`;
CREATE TABLE `work_station` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `store_id` BIGINT DEFAULT NULL COMMENT '门店ID',
  `station_name` VARCHAR(64) DEFAULT NULL COMMENT '工位名称',
  `station_type` VARCHAR(32) DEFAULT NULL COMMENT '工位类型 机修/保养/轮胎/美容',
  `capacity` INT DEFAULT NULL COMMENT '同时开工上限',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1启用 0禁用',
  `sort` INT DEFAULT NULL COMMENT '排序',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_work_station_store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='工位';

-- 技师
DROP TABLE IF EXISTS `technician`;
CREATE TABLE `technician` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT DEFAULT NULL COMMENT '关联系统用户ID',
  `store_id` BIGINT DEFAULT NULL COMMENT '门店ID',
  `tech_name` VARCHAR(64) DEFAULT NULL COMMENT '技师姓名',
  `phone` VARCHAR(128) DEFAULT NULL COMMENT '手机号(加密)',
  `avatar` VARCHAR(512) DEFAULT NULL COMMENT '头像',
  `tech_type` VARCHAR(32) DEFAULT NULL COMMENT '工种 机修/钣喷/美容/轮胎/通用',
  `level` VARCHAR(32) DEFAULT NULL COMMENT '技师等级 初级/中级/高级/专家',
  `cert_no` VARCHAR(64) DEFAULT NULL COMMENT '资质证书编号',
  `qualification` VARCHAR(512) DEFAULT NULL COMMENT '资质证明图片',
  `intro` TEXT COMMENT '技师简介',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1在职 0离职',
  `sort` INT DEFAULT NULL COMMENT '排序',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_technician_user_id` (`user_id`),
  KEY `idx_technician_store_id` (`store_id`),
  KEY `idx_technician_tech_type` (`tech_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='技师';

-- 技师排班
DROP TABLE IF EXISTS `technician_schedule`;
CREATE TABLE `technician_schedule` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `technician_id` BIGINT DEFAULT NULL COMMENT '技师ID',
  `work_date` VARCHAR(16) DEFAULT NULL COMMENT '排班日期 yyyy-MM-dd',
  `shift` VARCHAR(16) DEFAULT NULL COMMENT '班次 早班/中班/晚班/休息',
  `start_time` VARCHAR(16) DEFAULT NULL COMMENT '开始时间',
  `end_time` VARCHAR(16) DEFAULT NULL COMMENT '结束时间',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1有效 0无效',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_technician_schedule_technician_id` (`technician_id`),
  KEY `idx_technician_schedule_work_date` (`work_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='技师排班';

-- 服务分类
DROP TABLE IF EXISTS `service_category`;
CREATE TABLE `service_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父级ID',
  `category_name` VARCHAR(64) DEFAULT NULL COMMENT '分类名称',
  `category_name_en` VARCHAR(64) DEFAULT NULL COMMENT '分类英文名称',
  `icon` VARCHAR(512) DEFAULT NULL COMMENT '分类图标',
  `cover` VARCHAR(512) DEFAULT NULL COMMENT '分类封面',
  `description` TEXT COMMENT '分类描述',
  `sort` INT DEFAULT NULL COMMENT '排序',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1上架 0下架',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_service_category_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='服务分类';

-- 服务项目
DROP TABLE IF EXISTS `service_item`;
CREATE TABLE `service_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
  `item_name` VARCHAR(128) DEFAULT NULL COMMENT '项目名称',
  `item_name_en` VARCHAR(128) DEFAULT NULL COMMENT '项目英文名称',
  `item_code` VARCHAR(64) DEFAULT NULL COMMENT '项目编码',
  `cover` VARCHAR(512) DEFAULT NULL COMMENT '封面图',
  `images` TEXT COMMENT '图集(JSON数组)',
  `description` LONGTEXT COMMENT '项目详情(富文本)',
  `description_en` LONGTEXT COMMENT '项目详情英文(富文本)',
  `labor_fee` DECIMAL(10,2) DEFAULT NULL COMMENT '工时费',
  `price` DECIMAL(10,2) DEFAULT NULL COMMENT '参考价格',
  `price_desc` VARCHAR(255) DEFAULT NULL COMMENT '价格说明',
  `duration` INT DEFAULT NULL COMMENT '所需时长(分钟)',
  `support_home` TINYINT DEFAULT NULL COMMENT '是否支持上门 1是 0否',
  `bookable` TINYINT DEFAULT NULL COMMENT '是否可预约 1是 0否',
  `station_type` VARCHAR(32) DEFAULT NULL COMMENT '所需工位类型',
  `tech_require` VARCHAR(255) DEFAULT NULL COMMENT '技师资质要求',
  `case_images` TEXT COMMENT '施工案例图(JSON数组)',
  `audit_status` TINYINT DEFAULT NULL COMMENT '审核状态 0待审 1通过 2驳回',
  `view_count` INT DEFAULT NULL COMMENT '浏览量',
  `sort` INT DEFAULT NULL COMMENT '排序',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1上架 0下架',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_service_item_category_id` (`category_id`),
  KEY `idx_service_item_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='服务项目';

-- 价格配置
DROP TABLE IF EXISTS `service_price`;
CREATE TABLE `service_price` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `item_id` BIGINT DEFAULT NULL COMMENT '服务项目ID',
  `price_name` VARCHAR(64) DEFAULT NULL COMMENT '价格名称',
  `price_type` VARCHAR(32) DEFAULT NULL COMMENT '类型 基础/车型/时段',
  `price` DECIMAL(10,2) DEFAULT NULL COMMENT '价格',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '说明',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1启用 0禁用',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_service_price_item_id` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='价格配置';

-- 会员
DROP TABLE IF EXISTS `member`;
CREATE TABLE `member` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `unionid` VARCHAR(128) DEFAULT NULL COMMENT '微信UnionID',
  `openid` VARCHAR(128) DEFAULT NULL COMMENT '微信公众号OpenID',
  `miniapp_openid` VARCHAR(128) DEFAULT NULL COMMENT '小程序OpenID',
  `h5_openid` VARCHAR(128) DEFAULT NULL COMMENT 'H5 OpenID',
  `nickname` VARCHAR(128) DEFAULT NULL COMMENT '昵称',
  `avatar` VARCHAR(512) DEFAULT NULL COMMENT '头像',
  `real_name` VARCHAR(64) DEFAULT NULL COMMENT '真实姓名',
  `phone` VARCHAR(128) DEFAULT NULL COMMENT '手机号(加密)',
  `gender` TINYINT DEFAULT NULL COMMENT '性别 0未知 1男 2女',
  `birthday` VARCHAR(16) DEFAULT NULL COMMENT '生日',
  `level_id` BIGINT DEFAULT NULL COMMENT '会员等级ID',
  `points` INT DEFAULT NULL COMMENT '积分',
  `balance` DECIMAL(12,2) DEFAULT NULL COMMENT '储值余额',
  `source` VARCHAR(16) DEFAULT NULL COMMENT '注册来源 mp/miniapp/h5',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1正常 0禁用',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_member_unionid` (`unionid`),
  KEY `idx_member_openid` (`openid`),
  KEY `idx_member_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='会员';

-- 会员车辆
DROP TABLE IF EXISTS `member_vehicle`;
CREATE TABLE `member_vehicle` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `member_id` BIGINT DEFAULT NULL COMMENT '会员ID',
  `plate_no` VARCHAR(128) DEFAULT NULL COMMENT '车牌号(加密)',
  `plate_color` VARCHAR(16) DEFAULT NULL COMMENT '车牌颜色',
  `brand` VARCHAR(64) DEFAULT NULL COMMENT '品牌',
  `model` VARCHAR(128) DEFAULT NULL COMMENT '车型',
  `vin` VARCHAR(128) DEFAULT NULL COMMENT '车架号VIN(加密)',
  `engine_no` VARCHAR(64) DEFAULT NULL COMMENT '发动机号',
  `mileage` INT DEFAULT NULL COMMENT '当前里程(公里)',
  `buy_date` VARCHAR(16) DEFAULT NULL COMMENT '购车日期',
  `is_default` TINYINT DEFAULT NULL COMMENT '是否默认 1是 0否',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_member_vehicle_member_id` (`member_id`),
  KEY `idx_member_vehicle_plate_no` (`plate_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='会员车辆';

-- 会员等级
DROP TABLE IF EXISTS `member_level`;
CREATE TABLE `member_level` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `level_name` VARCHAR(64) DEFAULT NULL COMMENT '等级名称',
  `level_code` VARCHAR(32) DEFAULT NULL COMMENT '等级编码',
  `min_points` INT DEFAULT NULL COMMENT '所需最低积分',
  `discount` DECIMAL(4,2) DEFAULT NULL COMMENT '折扣率 如0.95',
  `icon` VARCHAR(512) DEFAULT NULL COMMENT '等级图标',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1启用 0禁用',
  `sort` INT DEFAULT NULL COMMENT '排序',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='会员等级';

-- 积分流水
DROP TABLE IF EXISTS `member_points_log`;
CREATE TABLE `member_points_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `member_id` BIGINT DEFAULT NULL COMMENT '会员ID',
  `change_points` INT DEFAULT NULL COMMENT '变动积分(正负)',
  `balance_points` INT DEFAULT NULL COMMENT '变动后积分',
  `type` VARCHAR(16) DEFAULT NULL COMMENT '类型 获取/消耗',
  `source` VARCHAR(64) DEFAULT NULL COMMENT '来源',
  `biz_no` VARCHAR(64) DEFAULT NULL COMMENT '关联单号',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_member_points_log_member_id` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='积分流水';

-- 储值流水
DROP TABLE IF EXISTS `member_balance_log`;
CREATE TABLE `member_balance_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `member_id` BIGINT DEFAULT NULL COMMENT '会员ID',
  `change_amount` DECIMAL(12,2) DEFAULT NULL COMMENT '变动金额(正负)',
  `balance_amount` DECIMAL(12,2) DEFAULT NULL COMMENT '变动后余额',
  `type` VARCHAR(16) DEFAULT NULL COMMENT '类型 充值/消费/退款/赠送',
  `biz_no` VARCHAR(64) DEFAULT NULL COMMENT '关联单号',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_member_balance_log_member_id` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='储值流水';

-- 优惠券
DROP TABLE IF EXISTS `coupon`;
CREATE TABLE `coupon` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `coupon_name` VARCHAR(128) DEFAULT NULL COMMENT '优惠券名称',
  `coupon_type` VARCHAR(32) DEFAULT NULL COMMENT '类型 满减/折扣/工时抵扣/洗车券',
  `face_value` DECIMAL(10,2) DEFAULT NULL COMMENT '面额',
  `min_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '使用门槛金额',
  `discount` DECIMAL(4,2) DEFAULT NULL COMMENT '折扣率',
  `total_count` INT DEFAULT NULL COMMENT '发行总量 0不限',
  `received_count` INT DEFAULT NULL COMMENT '已领取数量',
  `used_count` INT DEFAULT NULL COMMENT '已使用数量',
  `valid_type` VARCHAR(16) DEFAULT NULL COMMENT '有效期类型 固定/领取后N天',
  `start_time` DATETIME DEFAULT NULL COMMENT '生效时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '失效时间',
  `valid_days` INT DEFAULT NULL COMMENT '领取后有效天数',
  `scope` VARCHAR(255) DEFAULT NULL COMMENT '适用范围',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '使用说明',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1启用 0停用',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='优惠券';

-- 会员优惠券
DROP TABLE IF EXISTS `member_coupon`;
CREATE TABLE `member_coupon` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `member_id` BIGINT DEFAULT NULL COMMENT '会员ID',
  `coupon_id` BIGINT DEFAULT NULL COMMENT '优惠券ID',
  `coupon_code` VARCHAR(64) DEFAULT NULL COMMENT '券码',
  `status` VARCHAR(16) DEFAULT NULL COMMENT '状态 未使用/已使用/已过期',
  `biz_no` VARCHAR(64) DEFAULT NULL COMMENT '使用订单号',
  `receive_time` DATETIME DEFAULT NULL COMMENT '领取时间',
  `use_time` DATETIME DEFAULT NULL COMMENT '使用时间',
  `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_member_coupon_member_id` (`member_id`),
  KEY `idx_member_coupon_coupon_id` (`coupon_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='会员优惠券';

-- 养护套餐
DROP TABLE IF EXISTS `maintenance_package`;
CREATE TABLE `maintenance_package` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `package_name` VARCHAR(128) DEFAULT NULL COMMENT '套餐名称',
  `package_name_en` VARCHAR(128) DEFAULT NULL COMMENT '套餐英文名称',
  `cover` VARCHAR(512) DEFAULT NULL COMMENT '封面',
  `description` TEXT COMMENT '套餐说明',
  `price` DECIMAL(10,2) DEFAULT NULL COMMENT '套餐价',
  `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
  `times` INT DEFAULT NULL COMMENT '包含次数',
  `valid_days` INT DEFAULT NULL COMMENT '有效天数',
  `items` TEXT COMMENT '包含项目(JSON)',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1上架 0下架',
  `sort` INT DEFAULT NULL COMMENT '排序',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='养护套餐';

-- 会员套餐
DROP TABLE IF EXISTS `member_package`;
CREATE TABLE `member_package` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `member_id` BIGINT DEFAULT NULL COMMENT '会员ID',
  `package_id` BIGINT DEFAULT NULL COMMENT '套餐ID',
  `package_name` VARCHAR(128) DEFAULT NULL COMMENT '套餐名称',
  `total_times` INT DEFAULT NULL COMMENT '总次数',
  `used_times` INT DEFAULT NULL COMMENT '已用次数',
  `remain_times` INT DEFAULT NULL COMMENT '剩余次数',
  `buy_time` DATETIME DEFAULT NULL COMMENT '购买时间',
  `expire_time` DATETIME DEFAULT NULL COMMENT '到期时间',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1有效 0失效',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_member_package_member_id` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='会员套餐';

-- 营销活动
DROP TABLE IF EXISTS `marketing_activity`;
CREATE TABLE `marketing_activity` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `activity_name` VARCHAR(128) DEFAULT NULL COMMENT '活动名称',
  `activity_type` VARCHAR(32) DEFAULT NULL COMMENT '类型 拼团/节日/充值赠/新人',
  `cover` VARCHAR(512) DEFAULT NULL COMMENT '活动封面',
  `description` TEXT COMMENT '活动说明',
  `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
  `rules` TEXT COMMENT '活动规则(JSON)',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1进行中 0已结束',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='营销活动';

-- 储值充赠规则
DROP TABLE IF EXISTS `recharge_rule`;
CREATE TABLE `recharge_rule` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `rule_name` VARCHAR(64) DEFAULT NULL COMMENT '规则名称',
  `recharge_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '充值金额',
  `gift_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '赠送金额',
  `gift_points` INT DEFAULT NULL COMMENT '赠送积分',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1启用 0禁用',
  `sort` INT DEFAULT NULL COMMENT '排序',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='储值充赠规则';

-- 预约单
DROP TABLE IF EXISTS `appointment`;
CREATE TABLE `appointment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `appointment_no` VARCHAR(32) DEFAULT NULL COMMENT '预约单号',
  `member_id` BIGINT DEFAULT NULL COMMENT '会员ID',
  `store_id` BIGINT DEFAULT NULL COMMENT '门店ID',
  `item_id` BIGINT DEFAULT NULL COMMENT '服务项目ID',
  `item_name` VARCHAR(128) DEFAULT NULL COMMENT '服务项目名称',
  `vehicle_id` BIGINT DEFAULT NULL COMMENT '车辆ID',
  `plate_no` VARCHAR(128) DEFAULT NULL COMMENT '车牌号(加密)',
  `brand_model` VARCHAR(128) DEFAULT NULL COMMENT '品牌车型',
  `contact_name` VARCHAR(64) DEFAULT NULL COMMENT '联系人',
  `contact_phone` VARCHAR(128) DEFAULT NULL COMMENT '联系电话(加密)',
  `appoint_time` DATETIME DEFAULT NULL COMMENT '预约到店时间',
  `remark` TEXT COMMENT '客户备注',
  `status` VARCHAR(16) DEFAULT NULL COMMENT '状态 待确认/已确认/待接车/已完成/已取消',
  `source` VARCHAR(16) DEFAULT NULL COMMENT '来源 miniapp/h5/mp/admin',
  `cancel_reason` VARCHAR(255) DEFAULT NULL COMMENT '取消原因',
  `confirm_time` DATETIME DEFAULT NULL COMMENT '确认时间',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_appointment_appointment_no` (`appointment_no`),
  KEY `idx_appointment_member_id` (`member_id`),
  KEY `idx_appointment_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='预约单';

-- 维修工单
DROP TABLE IF EXISTS `work_order`;
CREATE TABLE `work_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_no` VARCHAR(32) DEFAULT NULL COMMENT '工单号',
  `appointment_id` BIGINT DEFAULT NULL COMMENT '关联预约单ID',
  `member_id` BIGINT DEFAULT NULL COMMENT '会员ID',
  `store_id` BIGINT DEFAULT NULL COMMENT '门店ID',
  `vehicle_id` BIGINT DEFAULT NULL COMMENT '车辆ID',
  `plate_no` VARCHAR(128) DEFAULT NULL COMMENT '车牌号(加密)',
  `brand_model` VARCHAR(128) DEFAULT NULL COMMENT '品牌车型',
  `mileage` INT DEFAULT NULL COMMENT '进厂里程',
  `contact_name` VARCHAR(64) DEFAULT NULL COMMENT '联系人',
  `contact_phone` VARCHAR(128) DEFAULT NULL COMMENT '联系电话(加密)',
  `fault_desc` TEXT COMMENT '故障描述/客户诉求',
  `service_items` TEXT COMMENT '服务项目(JSON)',
  `status` VARCHAR(16) DEFAULT NULL COMMENT '状态 待接车/待派工/施工中/待质检/待结算/已离店/已评价',
  `total_amount` DECIMAL(12,2) DEFAULT NULL COMMENT '工单总额',
  `labor_amount` DECIMAL(12,2) DEFAULT NULL COMMENT '工时费合计',
  `parts_amount` DECIMAL(12,2) DEFAULT NULL COMMENT '配件费合计',
  `discount_amount` DECIMAL(12,2) DEFAULT NULL COMMENT '优惠金额',
  `paid_amount` DECIMAL(12,2) DEFAULT NULL COMMENT '实收金额',
  `pay_type` VARCHAR(32) DEFAULT NULL COMMENT '支付方式 微信/储值/现金/混合',
  `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
  `receive_time` DATETIME DEFAULT NULL COMMENT '接车时间',
  `start_time` DATETIME DEFAULT NULL COMMENT '开工时间',
  `finish_time` DATETIME DEFAULT NULL COMMENT '完工时间',
  `deliver_time` DATETIME DEFAULT NULL COMMENT '交车时间',
  `technician_id` BIGINT DEFAULT NULL COMMENT '主责技师ID',
  `is_urgent` TINYINT DEFAULT NULL COMMENT '是否加急 1是 0否',
  `evaluate_status` TINYINT DEFAULT NULL COMMENT '评价状态 0未评 1已评',
  `remark` TEXT COMMENT '备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_work_order_order_no` (`order_no`),
  KEY `idx_work_order_member_id` (`member_id`),
  KEY `idx_work_order_status` (`status`),
  KEY `idx_work_order_technician_id` (`technician_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='维修工单';

-- 工单明细
DROP TABLE IF EXISTS `work_order_item`;
CREATE TABLE `work_order_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `work_order_id` BIGINT DEFAULT NULL COMMENT '工单ID',
  `item_type` VARCHAR(16) DEFAULT NULL COMMENT '类型 服务/配件',
  `item_id` BIGINT DEFAULT NULL COMMENT '项目/配件ID',
  `item_name` VARCHAR(128) DEFAULT NULL COMMENT '名称',
  `unit` VARCHAR(16) DEFAULT NULL COMMENT '单位',
  `quantity` DECIMAL(10,2) DEFAULT NULL COMMENT '数量',
  `price` DECIMAL(10,2) DEFAULT NULL COMMENT '单价',
  `amount` DECIMAL(10,2) DEFAULT NULL COMMENT '金额',
  `technician_id` BIGINT DEFAULT NULL COMMENT '施工技师ID',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_work_order_item_work_order_id` (`work_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='工单明细';

-- 工单状态流转日志
DROP TABLE IF EXISTS `work_order_log`;
CREATE TABLE `work_order_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `work_order_id` BIGINT DEFAULT NULL COMMENT '工单ID',
  `from_status` VARCHAR(16) DEFAULT NULL COMMENT '原状态',
  `to_status` VARCHAR(16) DEFAULT NULL COMMENT '新状态',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
  `operator_name` VARCHAR(64) DEFAULT NULL COMMENT '操作人',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_work_order_log_work_order_id` (`work_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='工单状态流转日志';

-- 接车检查/报价
DROP TABLE IF EXISTS `work_order_check`;
CREATE TABLE `work_order_check` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `work_order_id` BIGINT DEFAULT NULL COMMENT '工单ID',
  `check_type` VARCHAR(16) DEFAULT NULL COMMENT '类型 检查项/报价项',
  `check_name` VARCHAR(128) DEFAULT NULL COMMENT '检查项名称',
  `check_result` VARCHAR(255) DEFAULT NULL COMMENT '检查结果',
  `quote_price` DECIMAL(10,2) DEFAULT NULL COMMENT '报价',
  `customer_confirm` TINYINT DEFAULT NULL COMMENT '客户是否确认 1是 0否',
  `confirm_time` DATETIME DEFAULT NULL COMMENT '确认时间',
  `images` TEXT COMMENT '图片(JSON)',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_work_order_check_work_order_id` (`work_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='接车检查/报价';

-- 派工记录
DROP TABLE IF EXISTS `dispatch_record`;
CREATE TABLE `dispatch_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `work_order_id` BIGINT DEFAULT NULL COMMENT '工单ID',
  `technician_id` BIGINT DEFAULT NULL COMMENT '技师ID',
  `technician_name` VARCHAR(64) DEFAULT NULL COMMENT '技师姓名',
  `dispatch_type` VARCHAR(16) DEFAULT NULL COMMENT '类型 派工/接单/完工',
  `dispatch_time` DATETIME DEFAULT NULL COMMENT '操作时间',
  `status` VARCHAR(16) DEFAULT NULL COMMENT '状态',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_dispatch_record_work_order_id` (`work_order_id`),
  KEY `idx_dispatch_record_technician_id` (`technician_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='派工记录';

-- 退款记录
DROP TABLE IF EXISTS `refund_record`;
CREATE TABLE `refund_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `refund_no` VARCHAR(32) DEFAULT NULL COMMENT '退款单号',
  `work_order_id` BIGINT DEFAULT NULL COMMENT '工单ID',
  `order_no` VARCHAR(32) DEFAULT NULL COMMENT '原订单号',
  `member_id` BIGINT DEFAULT NULL COMMENT '会员ID',
  `refund_amount` DECIMAL(12,2) DEFAULT NULL COMMENT '退款金额',
  `reason` VARCHAR(255) DEFAULT NULL COMMENT '退款原因',
  `images` TEXT COMMENT '凭证图片(JSON)',
  `status` VARCHAR(16) DEFAULT NULL COMMENT '状态 待审核/已通过/已拒绝/已退款',
  `audit_user_id` BIGINT DEFAULT NULL COMMENT '审核人ID',
  `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `audit_remark` VARCHAR(255) DEFAULT NULL COMMENT '审核备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_refund_record_refund_no` (`refund_no`),
  KEY `idx_refund_record_work_order_id` (`work_order_id`),
  KEY `idx_refund_record_member_id` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='退款记录';

-- 工单评价
DROP TABLE IF EXISTS `work_order_evaluate`;
CREATE TABLE `work_order_evaluate` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `work_order_id` BIGINT DEFAULT NULL COMMENT '工单ID',
  `member_id` BIGINT DEFAULT NULL COMMENT '会员ID',
  `score` INT DEFAULT NULL COMMENT '综合评分 1-5',
  `service_score` INT DEFAULT NULL COMMENT '服务评分',
  `tech_score` INT DEFAULT NULL COMMENT '技师评分',
  `content` VARCHAR(512) DEFAULT NULL COMMENT '评价内容',
  `images` TEXT COMMENT '评价图片(JSON)',
  `reply` VARCHAR(512) DEFAULT NULL COMMENT '门店回复',
  `reply_time` DATETIME DEFAULT NULL COMMENT '回复时间',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1显示 0隐藏',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_work_order_evaluate_work_order_id` (`work_order_id`),
  KEY `idx_work_order_evaluate_member_id` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='工单评价';

-- 存胎寄存单
DROP TABLE IF EXISTS `tire_storage_order`;
CREATE TABLE `tire_storage_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `storage_no` VARCHAR(32) DEFAULT NULL COMMENT '寄存单号',
  `member_id` BIGINT DEFAULT NULL COMMENT '会员ID',
  `store_id` BIGINT DEFAULT NULL COMMENT '门店ID',
  `vehicle_id` BIGINT DEFAULT NULL COMMENT '车辆ID',
  `plate_no` VARCHAR(128) DEFAULT NULL COMMENT '车牌号(加密)',
  `tire_count` INT DEFAULT NULL COMMENT '轮胎数量',
  `tire_type` VARCHAR(32) DEFAULT NULL COMMENT '轮胎类型 冬胎/夏胎/四季胎',
  `tire_brand` VARCHAR(64) DEFAULT NULL COMMENT '轮胎品牌',
  `tire_size` VARCHAR(64) DEFAULT NULL COMMENT '轮胎规格',
  `storage_fee` DECIMAL(10,2) DEFAULT NULL COMMENT '寄存费',
  `overdue_fee` DECIMAL(10,2) DEFAULT NULL COMMENT '超期保管费',
  `position_no` VARCHAR(32) DEFAULT NULL COMMENT '寄存货位号',
  `start_date` VARCHAR(16) DEFAULT NULL COMMENT '寄存开始日期',
  `end_date` VARCHAR(16) DEFAULT NULL COMMENT '预计取胎日期',
  `real_out_date` VARCHAR(16) DEFAULT NULL COMMENT '实际取胎日期',
  `remind_preference` VARCHAR(64) DEFAULT NULL COMMENT '取胎提醒偏好',
  `status` VARCHAR(16) DEFAULT NULL COMMENT '状态 已寄存/寄存中/待取胎/已取胎/超期',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tire_storage_order_storage_no` (`storage_no`),
  KEY `idx_tire_storage_order_member_id` (`member_id`),
  KEY `idx_tire_storage_order_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='存胎寄存单';

-- 寄存轮胎明细
DROP TABLE IF EXISTS `tire_storage_item`;
CREATE TABLE `tire_storage_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `storage_order_id` BIGINT DEFAULT NULL COMMENT '寄存单ID',
  `tire_position` VARCHAR(16) DEFAULT NULL COMMENT '位置 左前/右前/左后/右后/备胎',
  `tire_brand` VARCHAR(64) DEFAULT NULL COMMENT '品牌',
  `tire_size` VARCHAR(64) DEFAULT NULL COMMENT '规格',
  `tread_depth` VARCHAR(16) DEFAULT NULL COMMENT '花纹深度',
  `images` TEXT COMMENT '照片(JSON)',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_tire_storage_item_storage_order_id` (`storage_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='寄存轮胎明细';

-- 寄存凭证
DROP TABLE IF EXISTS `tire_storage_proof`;
CREATE TABLE `tire_storage_proof` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `storage_order_id` BIGINT DEFAULT NULL COMMENT '寄存单ID',
  `proof_type` VARCHAR(16) DEFAULT NULL COMMENT '类型 入库/取胎',
  `images` TEXT COMMENT '凭证图片(JSON)',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
  `operator_name` VARCHAR(64) DEFAULT NULL COMMENT '操作人',
  `proof_time` DATETIME DEFAULT NULL COMMENT '凭证时间',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_tire_storage_proof_storage_order_id` (`storage_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='寄存凭证';

-- 寄存库存
DROP TABLE IF EXISTS `tire_storage_stock`;
CREATE TABLE `tire_storage_stock` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `store_id` BIGINT DEFAULT NULL COMMENT '门店ID',
  `position_no` VARCHAR(32) DEFAULT NULL COMMENT '货位号',
  `storage_order_id` BIGINT DEFAULT NULL COMMENT '寄存单ID',
  `plate_no` VARCHAR(128) DEFAULT NULL COMMENT '车牌号(加密)',
  `tire_count` INT DEFAULT NULL COMMENT '轮胎数量',
  `status` VARCHAR(16) DEFAULT NULL COMMENT '状态 在库/已出库',
  `in_time` DATETIME DEFAULT NULL COMMENT '入库时间',
  `out_time` DATETIME DEFAULT NULL COMMENT '出库时间',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_tire_storage_stock_store_id` (`store_id`),
  KEY `idx_tire_storage_stock_position_no` (`position_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='寄存库存';

-- 配件分类
DROP TABLE IF EXISTS `part_category`;
CREATE TABLE `part_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父级ID',
  `category_name` VARCHAR(64) DEFAULT NULL COMMENT '分类名称',
  `sort` INT DEFAULT NULL COMMENT '排序',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1启用 0禁用',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_part_category_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='配件分类';

-- 配件耗材
DROP TABLE IF EXISTS `part_info`;
CREATE TABLE `part_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `part_code` VARCHAR(64) DEFAULT NULL COMMENT '配件编码',
  `part_name` VARCHAR(128) DEFAULT NULL COMMENT '配件名称',
  `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
  `brand` VARCHAR(64) DEFAULT NULL COMMENT '品牌',
  `spec` VARCHAR(128) DEFAULT NULL COMMENT '规格型号',
  `unit` VARCHAR(16) DEFAULT NULL COMMENT '单位',
  `purchase_price` DECIMAL(10,2) DEFAULT NULL COMMENT '成本价',
  `sale_price` DECIMAL(10,2) DEFAULT NULL COMMENT '售价',
  `stock` INT DEFAULT NULL COMMENT '当前库存',
  `warn_stock` INT DEFAULT NULL COMMENT '预警库存',
  `cover` VARCHAR(512) DEFAULT NULL COMMENT '图片',
  `supplier` VARCHAR(128) DEFAULT NULL COMMENT '供应商',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1启用 0禁用',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_part_info_part_code` (`part_code`),
  KEY `idx_part_info_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='配件耗材';

-- 配件出入库记录
DROP TABLE IF EXISTS `part_stock_record`;
CREATE TABLE `part_stock_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `part_id` BIGINT DEFAULT NULL COMMENT '配件ID',
  `part_name` VARCHAR(128) DEFAULT NULL COMMENT '配件名称',
  `record_type` VARCHAR(16) DEFAULT NULL COMMENT '类型 入库/出库/盘点/退货',
  `quantity` INT DEFAULT NULL COMMENT '数量',
  `before_stock` INT DEFAULT NULL COMMENT '变动前库存',
  `after_stock` INT DEFAULT NULL COMMENT '变动后库存',
  `unit_price` DECIMAL(10,2) DEFAULT NULL COMMENT '单价',
  `amount` DECIMAL(10,2) DEFAULT NULL COMMENT '金额',
  `work_order_id` BIGINT DEFAULT NULL COMMENT '关联工单ID',
  `order_no` VARCHAR(32) DEFAULT NULL COMMENT '关联单号',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
  `operator_name` VARCHAR(64) DEFAULT NULL COMMENT '操作人',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_part_stock_record_part_id` (`part_id`),
  KEY `idx_part_stock_record_work_order_id` (`work_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='配件出入库记录';

-- 配件供应商
DROP TABLE IF EXISTS `part_supplier`;
CREATE TABLE `part_supplier` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `supplier_name` VARCHAR(128) DEFAULT NULL COMMENT '供应商名称',
  `contact_name` VARCHAR(64) DEFAULT NULL COMMENT '联系人',
  `phone` VARCHAR(64) DEFAULT NULL COMMENT '联系电话',
  `address` VARCHAR(255) DEFAULT NULL COMMENT '地址',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1启用 0禁用',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='配件供应商';

-- 文章分类
DROP TABLE IF EXISTS `cms_article_category`;
CREATE TABLE `cms_article_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父级ID',
  `category_name` VARCHAR(64) DEFAULT NULL COMMENT '分类名称',
  `category_name_en` VARCHAR(64) DEFAULT NULL COMMENT '分类英文名称',
  `sort` INT DEFAULT NULL COMMENT '排序',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1启用 0禁用',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_cms_article_category_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文章分类';

-- 养护知识文章
DROP TABLE IF EXISTS `cms_article`;
CREATE TABLE `cms_article` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
  `title` VARCHAR(255) DEFAULT NULL COMMENT '标题',
  `title_en` VARCHAR(255) DEFAULT NULL COMMENT '标题英文',
  `cover` VARCHAR(512) DEFAULT NULL COMMENT '封面图',
  `summary` VARCHAR(512) DEFAULT NULL COMMENT '摘要',
  `summary_en` VARCHAR(512) DEFAULT NULL COMMENT '摘要英文',
  `content` LONGTEXT COMMENT '正文(富文本)',
  `content_en` LONGTEXT COMMENT '正文英文(富文本)',
  `author` VARCHAR(64) DEFAULT NULL COMMENT '作者',
  `source` VARCHAR(128) DEFAULT NULL COMMENT '来源',
  `seo_title` VARCHAR(255) DEFAULT NULL COMMENT 'SEO标题',
  `seo_keywords` VARCHAR(255) DEFAULT NULL COMMENT 'SEO关键词',
  `seo_description` VARCHAR(512) DEFAULT NULL COMMENT 'SEO描述',
  `tags` VARCHAR(255) DEFAULT NULL COMMENT '标签',
  `view_count` INT DEFAULT NULL COMMENT '浏览量',
  `is_top` TINYINT DEFAULT NULL COMMENT '是否置顶 1是 0否',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1发布 0草稿',
  `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_cms_article_category_id` (`category_id`),
  KEY `idx_cms_article_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='养护知识文章';

-- 品牌故事
DROP TABLE IF EXISTS `cms_brand_story`;
CREATE TABLE `cms_brand_story` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` VARCHAR(255) DEFAULT NULL COMMENT '标题',
  `title_en` VARCHAR(255) DEFAULT NULL COMMENT '标题英文',
  `cover` VARCHAR(512) DEFAULT NULL COMMENT '封面',
  `content` LONGTEXT COMMENT '内容(富文本)',
  `content_en` LONGTEXT COMMENT '英文内容(富文本)',
  `images` TEXT COMMENT '图集(JSON)',
  `sort` INT DEFAULT NULL COMMENT '排序',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1启用 0禁用',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='品牌故事';

-- 施工案例
DROP TABLE IF EXISTS `cms_case`;
CREATE TABLE `cms_case` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `case_name` VARCHAR(255) DEFAULT NULL COMMENT '案例名称',
  `case_name_en` VARCHAR(255) DEFAULT NULL COMMENT '案例英文名称',
  `case_type` VARCHAR(32) DEFAULT NULL COMMENT '类型 维修/保养/美容/存胎',
  `cover` VARCHAR(512) DEFAULT NULL COMMENT '封面',
  `before_images` TEXT COMMENT '施工前图(JSON)',
  `after_images` TEXT COMMENT '施工后图(JSON)',
  `content` LONGTEXT COMMENT '案例描述',
  `content_en` LONGTEXT COMMENT '英文描述',
  `service_item_id` BIGINT DEFAULT NULL COMMENT '关联服务项目ID',
  `view_count` INT DEFAULT NULL COMMENT '浏览量',
  `sort` INT DEFAULT NULL COMMENT '排序',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1上架 0下架',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_cms_case_case_type` (`case_type`),
  KEY `idx_cms_case_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='施工案例';

-- 轮播图
DROP TABLE IF EXISTS `cms_banner`;
CREATE TABLE `cms_banner` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `platform` VARCHAR(16) DEFAULT NULL COMMENT '端 home/service/www',
  `title` VARCHAR(255) DEFAULT NULL COMMENT '标题',
  `title_en` VARCHAR(255) DEFAULT NULL COMMENT '标题英文',
  `image` VARCHAR(512) DEFAULT NULL COMMENT '图片',
  `link` VARCHAR(512) DEFAULT NULL COMMENT '跳转链接',
  `sort` INT DEFAULT NULL COMMENT '排序',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1启用 0禁用',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_cms_banner_platform` (`platform`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='轮播图';

-- 在线留言
DROP TABLE IF EXISTS `cms_message`;
CREATE TABLE `cms_message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `member_id` BIGINT DEFAULT NULL COMMENT '会员ID',
  `name` VARCHAR(64) DEFAULT NULL COMMENT '姓名',
  `phone` VARCHAR(128) DEFAULT NULL COMMENT '手机号(加密)',
  `email` VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
  `content` TEXT COMMENT '留言内容',
  `source` VARCHAR(16) DEFAULT NULL COMMENT '来源 www/miniapp/h5',
  `status` TINYINT DEFAULT NULL COMMENT '状态 0未处理 1已处理',
  `reply` TEXT COMMENT '回复内容',
  `reply_time` DATETIME DEFAULT NULL COMMENT '回复时间',
  `ip` VARCHAR(64) DEFAULT NULL COMMENT '提交IP',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_cms_message_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='在线留言';

-- 公众号粉丝
DROP TABLE IF EXISTS `wechat_fans`;
CREATE TABLE `wechat_fans` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `openid` VARCHAR(128) DEFAULT NULL COMMENT 'OpenID',
  `unionid` VARCHAR(128) DEFAULT NULL COMMENT 'UnionID',
  `nickname` VARCHAR(128) DEFAULT NULL COMMENT '昵称',
  `avatar` VARCHAR(512) DEFAULT NULL COMMENT '头像',
  `gender` TINYINT DEFAULT NULL COMMENT '性别',
  `subscribe` TINYINT DEFAULT NULL COMMENT '是否关注 1是 0否',
  `subscribe_time` DATETIME DEFAULT NULL COMMENT '关注时间',
  `tags` VARCHAR(255) DEFAULT NULL COMMENT '标签',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  KEY `idx_wechat_fans_openid` (`openid`),
  KEY `idx_wechat_fans_unionid` (`unionid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='公众号粉丝';

-- 公众号素材
DROP TABLE IF EXISTS `wechat_material`;
CREATE TABLE `wechat_material` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `material_type` VARCHAR(16) DEFAULT NULL COMMENT '类型 图文/图片/视频',
  `title` VARCHAR(255) DEFAULT NULL COMMENT '标题',
  `media_id` VARCHAR(128) DEFAULT NULL COMMENT '微信媒体ID',
  `url` VARCHAR(512) DEFAULT NULL COMMENT '素材地址',
  `thumb_url` VARCHAR(512) DEFAULT NULL COMMENT '缩略图',
  `content` TEXT COMMENT '图文内容',
  `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1有效 0失效',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='公众号素材';

-- 模板消息配置
DROP TABLE IF EXISTS `wechat_template_msg`;
CREATE TABLE `wechat_template_msg` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `msg_type` VARCHAR(64) DEFAULT NULL COMMENT '消息类型 预约确认/接车/施工进度/完工/结算/存胎到期/退款',
  `template_id` VARCHAR(128) DEFAULT NULL COMMENT '微信模板ID',
  `title` VARCHAR(128) DEFAULT NULL COMMENT '模板标题',
  `content_template` TEXT COMMENT '内容模板(变量占位)',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1启用 0禁用',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='模板消息配置';

-- 公众号回复规则
DROP TABLE IF EXISTS `wechat_reply_rule`;
CREATE TABLE `wechat_reply_rule` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `rule_type` VARCHAR(16) DEFAULT NULL COMMENT '类型 关注/关键词/默认',
  `keyword` VARCHAR(128) DEFAULT NULL COMMENT '关键词',
  `match_type` VARCHAR(16) DEFAULT NULL COMMENT '匹配方式 全匹配/半匹配',
  `reply_type` VARCHAR(16) DEFAULT NULL COMMENT '回复类型 文本/图文/图片',
  `content` TEXT COMMENT '回复内容',
  `material_id` BIGINT DEFAULT NULL COMMENT '关联素材ID',
  `status` TINYINT DEFAULT NULL COMMENT '状态 1启用 0禁用',
  `sort` INT DEFAULT NULL COMMENT '排序',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='公众号回复规则';

-- 每日经营统计
DROP TABLE IF EXISTS `stat_daily`;
CREATE TABLE `stat_daily` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `stat_date` VARCHAR(16) DEFAULT NULL COMMENT '统计日期 yyyy-MM-dd',
  `uv` INT DEFAULT NULL COMMENT '访问人数UV',
  `pv` INT DEFAULT NULL COMMENT '访问量PV',
  `appointment_count` INT DEFAULT NULL COMMENT '预约量',
  `order_count` INT DEFAULT NULL COMMENT '工单量',
  `revenue` DECIMAL(12,2) DEFAULT NULL COMMENT '营收',
  `gross_profit` DECIMAL(12,2) DEFAULT NULL COMMENT '毛利',
  `tire_count` INT DEFAULT NULL COMMENT '存胎量',
  `member_count` INT DEFAULT NULL COMMENT '新增会员',
  `recharge_amount` DECIMAL(12,2) DEFAULT NULL COMMENT '储值充值额',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_stat_daily_stat_date` (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='每日经营统计';

