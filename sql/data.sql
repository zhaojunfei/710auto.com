-- =====================================================================
-- 710号汽车之家 数字化多端平台 初始化数据脚本
-- 默认账号：admin / admin123   （超级管理员）
--            tech01 / admin123 （接车技师）
-- =====================================================================
USE `auto_repair`;
SET NAMES utf8mb4;

-- ---------------- 角色 ----------------
INSERT INTO `sys_role` (id, role_name, role_code, data_scope, sort, status, remark, create_time, update_time, deleted) VALUES
(1, '超级管理员', 'SUPER_ADMIN', 1, 1, 1, '拥有全部权限', NOW(), NOW(), 0),
(2, '门店店长',   'STORE_MANAGER', 2, 2, 1, '本店全部经营管理权限', NOW(), NOW(), 0),
(3, '前台/接车员','RECEPTION', 2, 3, 1, '预约接车、工单录入', NOW(), NOW(), 0),
(4, '接车技师',   'TECHNICIAN', 3, 4, 1, '仅本人被派工单', NOW(), NOW(), 0),
(5, '财务',       'FINANCE', 2, 5, 1, '结算、退款、报表', NOW(), NOW(), 0),
(6, '运营',       'OPERATOR', 1, 6, 1, '内容 CMS、营销、会员', NOW(), NOW(), 0),
(7, '只读账号',   'READONLY', 1, 7, 1, '仅查看', NOW(), NOW(), 0),
(8, '供应商',     'SUPPLIER', 3, 8, 1, '供应商端：维护报价、提交送货单、对账', NOW(), NOW(), 0);

-- ---------------- 系统用户 ----------------
INSERT INTO `sys_user` (id, username, password, real_name, phone, store_id, status, remark, create_time, update_time, deleted) VALUES
(1, 'admin',  '$2a$10$7a0uFHlnjp5J1e6VoXdYOet.0FJ1owx7MeS3QgMfsWtZWdk0CQjtC', '超级管理员', NULL, 1, 1, '系统内置超级管理员', NOW(), NOW(), 0),
(2, 'manager','$2a$10$7a0uFHlnjp5J1e6VoXdYOet.0FJ1owx7MeS3QgMfsWtZWdk0CQjtC', '王店长', NULL, 1, 1, '门店店长', NOW(), NOW(), 0),
(3, 'tech01', '$2a$10$7a0uFHlnjp5J1e6VoXdYOet.0FJ1owx7MeS3QgMfsWtZWdk0CQjtC', '李师傅', NULL, 1, 1, '机修技师', NOW(), NOW(), 0),
(4, 'finance','$2a$10$7a0uFHlnjp5J1e6VoXdYOet.0FJ1owx7MeS3QgMfsWtZWdk0CQjtC', '张会计', NULL, 1, 1, '财务', NOW(), NOW(), 0),
(5, 'supplier01','$2a$10$7a0uFHlnjp5J1e6VoXdYOet.0FJ1owx7MeS3QgMfsWtZWdk0CQjtC', '哈尔滨汽配城-王经理', NULL, 1, 1, '供应商端账号', NOW(), NOW(), 0);

INSERT INTO `sys_user_role` (user_id, role_id, create_time, update_time, deleted) VALUES
(1, 1, NOW(), NOW(), 0),
(2, 2, NOW(), NOW(), 0),
(3, 4, NOW(), NOW(), 0),
(4, 5, NOW(), NOW(), 0),
(5, 8, NOW(), NOW(), 0);

-- ---------------- 系统菜单权限 ----------------
INSERT INTO `sys_menu` (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort, visible, status, create_time, update_time, deleted) VALUES
(1, 0, '数据看板', 'MENU', '/dashboard', 'dashboard/index', 'dashboard:view', 'DataLine', 1, 1, 1, NOW(), NOW(), 0),
(2, 0, '门店工位', 'MENU', '/store', 'store/index', 'store:view', 'OfficeBuilding', 2, 1, 1, NOW(), NOW(), 0),
(3, 0, '服务项目', 'MENU', '/service', 'service/index', 'service:view', 'Tools', 3, 1, 1, NOW(), NOW(), 0),
(4, 0, '预约工单', 'MENU', '/workorder', 'workorder/index', 'workorder:view', 'Tickets', 4, 1, 1, NOW(), NOW(), 0),
(5, 0, '存胎寄存', 'MENU', '/tire', 'tire/index', 'tire:view', 'Van', 5, 1, 1, NOW(), NOW(), 0),
(6, 0, '配件库存', 'MENU', '/inventory', 'inventory/index', 'inventory:view', 'Box', 6, 1, 1, NOW(), NOW(), 0),
(7, 0, '会员营销', 'MENU', '/member', 'member/index', 'member:view', 'User', 7, 1, 1, NOW(), NOW(), 0),
(8, 0, '官网CMS', 'MENU', '/cms', 'cms/index', 'cms:view', 'Document', 8, 1, 1, NOW(), NOW(), 0),
(9, 0, '公众号', 'MENU', '/wechat', 'wechat/index', 'wechat:view', 'ChatDotRound', 9, 1, 1, NOW(), NOW(), 0),
(10, 0, '系统管理', 'DIR', '/system', NULL, NULL, 'Setting', 10, 1, 1, NOW(), NOW(), 0),
(11, 10, '用户角色', 'MENU', '/system/user', 'system/user', 'system:user:list', 'UserFilled', 1, 1, 1, NOW(), NOW(), 0),
(12, 10, '系统参数', 'MENU', '/system/config', 'system/config', 'system:config:list', 'SetUp', 2, 1, 1, NOW(), NOW(), 0),
(13, 10, '多语言配置', 'MENU', '/system/language', 'system/language', 'system:language:list', 'Switch', 3, 1, 1, NOW(), NOW(), 0),
(14, 10, '菜单可视化', 'MENU', '/system/menu', 'system/menu', 'system:menu:list', 'Grid', 4, 1, 1, NOW(), NOW(), 0),
(15, 10, '对象存储', 'MENU', '/system/storage', 'system/storage', 'system:storage:list', 'Upload', 5, 1, 1, NOW(), NOW(), 0),
(16, 10, '审计日志', 'MENU', '/system/audit', 'system/audit', 'system:audit:list', 'Bell', 6, 1, 1, NOW(), NOW(), 0),
(17, 10, '数据字典', 'MENU', '/system/dict', 'system/dict', 'system:dict:list', 'Collection', 7, 1, 1, NOW(), NOW(), 0),
(18, 10, '文件管理', 'MENU', '/system/file', 'system/file', 'system:file:list', 'Folder', 8, 1, 1, NOW(), NOW(), 0),
(19, 10, '菜单管理', 'MENU', '/system/menu-manage', 'system/menu-manage', 'system:menu:manage', 'Menu', 9, 1, 1, NOW(), NOW(), 0);

-- 按钮级权限点（新增/编辑/删除）：挂在对应业务菜单下，perms 形如 store-info:add。
-- 前端 CrudTable 默认按资源名（url 去掉 /admin/）做按钮级权限控制。
INSERT INTO `sys_menu` (parent_id, menu_name, menu_type, perms, sort, visible, status, create_time, update_time, deleted)
SELECT r.pid,
       CASE a.act WHEN 'add' THEN '新增' WHEN 'edit' THEN '编辑' ELSE '删除' END,
       'BUTTON',
       CONCAT(r.res, ':', a.act),
       a.srt, 0, 1, NOW(), NOW(), 0
FROM (
    -- 门店工位
    SELECT 2 AS pid, 'store-info' AS res UNION ALL SELECT 2, 'work-station' UNION ALL SELECT 2, 'technician' UNION ALL SELECT 2, 'technician-schedule' UNION ALL
    -- 服务项目
    SELECT 3, 'service-item' UNION ALL SELECT 3, 'service-category' UNION ALL SELECT 3, 'service-price' UNION ALL
    -- 预约工单
    SELECT 4, 'appointments' UNION ALL SELECT 4, 'work-order-evaluate' UNION ALL SELECT 4, 'dispatch-record' UNION ALL SELECT 4, 'refund-record' UNION ALL
    -- 存胎寄存
    SELECT 5, 'tire-storage' UNION ALL SELECT 5, 'tire-storage-stock' UNION ALL SELECT 5, 'tire-storage-item' UNION ALL SELECT 5, 'tire-storage-proof' UNION ALL
    -- 配件库存
    SELECT 6, 'part-info' UNION ALL SELECT 6, 'part-stock-record' UNION ALL SELECT 6, 'part-category' UNION ALL SELECT 6, 'part-supplier' UNION ALL SELECT 6, 'supplier-delivery' UNION ALL
    -- 会员营销
    SELECT 7, 'member' UNION ALL SELECT 7, 'member-vehicle' UNION ALL SELECT 7, 'member-level' UNION ALL SELECT 7, 'coupon' UNION ALL
    SELECT 7, 'member-coupon' UNION ALL SELECT 7, 'recharge-rule' UNION ALL SELECT 7, 'maintenance-package' UNION ALL SELECT 7, 'member-package' UNION ALL
    SELECT 7, 'marketing-activity' UNION ALL SELECT 7, 'member-points-log' UNION ALL SELECT 7, 'member-balance-log' UNION ALL
    -- 官网CMS
    SELECT 8, 'cms-article' UNION ALL SELECT 8, 'cms-article-category' UNION ALL SELECT 8, 'cms-case' UNION ALL
    SELECT 8, 'cms-brand-story' UNION ALL SELECT 8, 'cms-banner' UNION ALL SELECT 8, 'cms-message' UNION ALL
    -- 公众号
    SELECT 9, 'wechat-reply-rule' UNION ALL SELECT 9, 'wechat-template-msg' UNION ALL SELECT 9, 'wechat-fans' UNION ALL SELECT 9, 'wechat-material' UNION ALL
    -- 系统管理
    SELECT 11, 'sys-user' UNION ALL SELECT 11, 'sys-role' UNION ALL
    SELECT 16, 'sys-audit-log' UNION ALL
    SELECT 17, 'sys-dict-type' UNION ALL SELECT 17, 'sys-dict-data' UNION ALL
    SELECT 18, 'sys-file'
) r
CROSS JOIN (
    SELECT 'add' AS act, 1 AS srt UNION ALL
    SELECT 'edit', 2 UNION ALL
    SELECT 'delete', 3
) a;

-- ---------------- 角色-菜单授权 ----------------
-- 超级管理员(role 1)不需要授权，代码内置返回全量菜单；审计日志(16)默认只给超管。
INSERT INTO `sys_role_menu` (role_id, menu_id, create_time, update_time, deleted) VALUES
-- 门店店长：业务菜单 + 用户/参数/存储/字典/文件
(2,1,NOW(),NOW(),0),(2,2,NOW(),NOW(),0),(2,3,NOW(),NOW(),0),(2,4,NOW(),NOW(),0),(2,5,NOW(),NOW(),0),
(2,6,NOW(),NOW(),0),(2,7,NOW(),NOW(),0),(2,8,NOW(),NOW(),0),(2,9,NOW(),NOW(),0),(2,10,NOW(),NOW(),0),
(2,11,NOW(),NOW(),0),(2,12,NOW(),NOW(),0),(2,15,NOW(),NOW(),0),(2,17,NOW(),NOW(),0),(2,18,NOW(),NOW(),0),
-- 前台/接车员：看板、服务、预约工单、存胎、会员
(3,1,NOW(),NOW(),0),(3,3,NOW(),NOW(),0),(3,4,NOW(),NOW(),0),(3,5,NOW(),NOW(),0),(3,7,NOW(),NOW(),0),
-- 接车技师：看板、预约工单
(4,1,NOW(),NOW(),0),(4,4,NOW(),NOW(),0),
-- 财务：看板、工单/退款、会员流水、配件库存
(5,1,NOW(),NOW(),0),(5,4,NOW(),NOW(),0),(5,6,NOW(),NOW(),0),(5,7,NOW(),NOW(),0),
-- 运营：看板、服务、会员营销、官网CMS、公众号
(6,1,NOW(),NOW(),0),(6,3,NOW(),NOW(),0),(6,7,NOW(),NOW(),0),(6,8,NOW(),NOW(),0),(6,9,NOW(),NOW(),0),
-- 只读：仅业务菜单（不授权任何按钮级权限，为真正的只读）
(7,1,NOW(),NOW(),0),(7,2,NOW(),NOW(),0),(7,3,NOW(),NOW(),0),(7,4,NOW(),NOW(),0),(7,5,NOW(),NOW(),0),
(7,6,NOW(),NOW(),0),(7,7,NOW(),NOW(),0),(7,8,NOW(),NOW(),0),(7,9,NOW(),NOW(),0);

-- 按钮级权限授权：凡拥有某菜单的角色，自动获得该菜单下所有按钮权限；只读账号(7)除外。
INSERT INTO `sys_role_menu` (role_id, menu_id, create_time, update_time, deleted)
SELECT rm.role_id, b.id, NOW(), NOW(), 0
FROM `sys_role_menu` rm
JOIN `sys_menu` p ON p.id = rm.menu_id AND p.menu_type = 'MENU'
JOIN `sys_menu` b ON b.parent_id = p.id AND b.menu_type = 'BUTTON'
WHERE rm.deleted = 0 AND rm.role_id <> 7;

-- ---------------- 多语言 ----------------
INSERT INTO `sys_language` (id, lang_code, lang_name, lang_name_en, flag_icon, is_default, status, sort, create_time, update_time, deleted) VALUES
(1, 'zh-CN', '简体中文', 'Chinese',  '🇨🇳', 1, 1, 1, NOW(), NOW(), 0),
(2, 'en-US', 'English',  'English', '🇺🇸', 0, 1, 2, NOW(), NOW(), 0);

-- ---------------- 系统全局参数 ----------------
INSERT INTO `sys_config` (config_key, config_value, config_name, config_group, config_type, encrypted, remark, create_time, update_time, deleted) VALUES
('site.name', '710号汽车之家', '站点名称', 'site', 'string', 0, NULL, NOW(), NOW(), 0),
('site.logo', '', '站点Logo', 'site', 'string', 0, NULL, NOW(), NOW(), 0),
('site.favicon', '', 'Favicon', 'site', 'string', 0, NULL, NOW(), NOW(), 0),
('site.icp', '黑ICP备2026000000号', 'ICP备案号', 'site', 'string', 0, NULL, NOW(), NOW(), 0),
('site.icp.owner', '哈尔滨710号汽车之家', '备案主体', 'site', 'string', 0, NULL, NOW(), NOW(), 0),
('site.service.phone', '0451-8888710', '客服电话', 'site', 'string', 0, NULL, NOW(), NOW(), 0),
('site.business.hours', '08:30-18:30（全年无休）', '营业时间', 'site', 'string', 0, NULL, NOW(), NOW(), 0),
('site.brand.intro', '哈尔滨本地社区型一站式汽车服务门店，专业汽修、保养、轮胎与存胎寄存。', '品牌简介', 'site', 'string', 0, NULL, NOW(), NOW(), 0),
('site.seo.keywords', '哈尔滨汽修,汽车保养,存胎寄存,轮胎更换,710号汽车之家', 'SEO默认关键词', 'site', 'string', 0, NULL, NOW(), NOW(), 0),
('site.seo.description', '710号汽车之家——哈尔滨透明报价的专业汽车维修养护门店，提供保养、维修、轮胎、存胎寄存一站式服务。', 'SEO默认描述', 'site', 'string', 0, NULL, NOW(), NOW(), 0),
('storage.driver', 'local', '当前存储驱动', 'storage', 'string', 0, NULL, NOW(), NOW(), 0),
('storage.config', '{"type":"local","basePath":"./data/upload","urlPrefix":"http://127.0.0.1:8080/upload/local","pathStyle":false}', '对象存储配置', 'storage', 'json', 0, NULL, NOW(), NOW(), 0),
('wechat.mp.appid', '', '公众号AppID', 'wechat', 'string', 0, NULL, NOW(), NOW(), 0),
('wechat.mp.secret', '', '公众号AppSecret', 'wechat', 'secret', 1, NULL, NOW(), NOW(), 0),
('wechat.ma.appid', '', '小程序AppID', 'wechat', 'string', 0, NULL, NOW(), NOW(), 0),
('wechat.ma.secret', '', '小程序AppSecret', 'wechat', 'secret', 1, NULL, NOW(), NOW(), 0),
('pay.mch.id', '', '微信支付商户号', 'pay', 'string', 0, NULL, NOW(), NOW(), 0),
('pay.mch.key', '', '微信支付商户密钥', 'pay', 'secret', 1, NULL, NOW(), NOW(), 0),
('sms.enabled', 'false', '短信通道开关', 'sms', 'bool', 0, NULL, NOW(), NOW(), 0);

-- ---------------- 门店 & 工位 & 技师 ----------------
INSERT INTO `store_info` (id, store_name, store_name_en, logo, phone, address, address_en, longitude, latitude, business_hours, intro, intro_en, status, create_time, update_time, deleted) VALUES
(1, '710号汽车之家（总店）', '710 Auto Home (Main Store)', '', '0451-8888710', '黑龙江省哈尔滨市南岗区××路710号', 'No.710, XX Road, Nangang District, Harbin', '126.642464', '45.756967', '08:30-18:30',
 '哈尔滨本地社区型一站式汽车服务门店，主营汽修维修、养护保养、轮胎服务与冬夏胎寄存。', 'A community one-stop auto service store in Harbin.', 1, NOW(), NOW(), 0);

INSERT INTO `work_station` (id, store_id, station_name, station_type, capacity, status, sort, create_time, update_time, deleted) VALUES
(1, 1, '机修工位A', '机修', 2, 1, 1, NOW(), NOW(), 0),
(2, 1, '保养工位B', '保养', 2, 1, 2, NOW(), NOW(), 0),
(3, 1, '轮胎工位C', '轮胎', 1, 1, 3, NOW(), NOW(), 0),
(4, 1, '美容工位D', '美容', 1, 1, 4, NOW(), NOW(), 0);

INSERT INTO `technician` (id, user_id, store_id, tech_name, phone, tech_type, level, cert_no, intro, status, sort, create_time, update_time, deleted) VALUES
(1, 3, 1, '李师傅', NULL, '机修', '高级', 'JX-2020-001', '10年德系车维修经验，擅长发动机与底盘。', 1, 1, NOW(), NOW(), 0),
(2, NULL, 1, '赵师傅', NULL, '轮胎', '中级', 'LT-2021-008', '轮胎更换、四轮定位专家。', 1, 2, NOW(), NOW(), 0),
(3, NULL, 1, '孙师傅', NULL, '美容', '高级', 'MR-2019-003', '精洗打蜡、贴膜车衣。', 1, 3, NOW(), NOW(), 0);

-- ---------------- 服务分类 & 服务项目 ----------------
INSERT INTO `service_category` (id, parent_id, category_name, category_name_en, description, sort, status, create_time, update_time, deleted) VALUES
(1, 0, '汽修维修', 'Auto Repair', '发动机/变速箱/底盘/电路/钣金喷漆', 1, 1, NOW(), NOW(), 0),
(2, 0, '保养维护', 'Maintenance', '机油机滤/全车油液/刹车/四轮定位', 2, 1, NOW(), NOW(), 0),
(3, 0, '轮胎服务', 'Tire Service', '换胎倒胎/补胎/轮胎销售', 3, 1, NOW(), NOW(), 0),
(4, 0, '存胎寄存', 'Tire Storage', '冬夏胎换季寄存', 4, 1, NOW(), NOW(), 0),
(5, 0, '养护美容', 'Detailing', '精洗/打蜡镀膜/内饰清洁/贴膜车衣', 5, 1, NOW(), NOW(), 0),
(6, 0, '增值代办', 'Value-added Service', '保险/年检/道路救援', 6, 1, NOW(), NOW(), 0);

INSERT INTO `service_item` (category_id, item_name, item_name_en, item_code, cover, description, description_en, labor_fee, price, price_desc, duration, support_home, bookable, station_type, tech_require, audit_status, view_count, sort, status, create_time, update_time, deleted) VALUES
(2, '小保养（机油+机滤）', 'Basic Maintenance', 'BY-001', '', '<p>更换原厂机油、机油滤清器，全车检查。</p>', '<p>Engine oil and oil filter replacement.</p>', 80.00, 380.00, '含工时费与材料', 60, 0, 1, '保养', '中级以上', 1, 0, 1, 1, NOW(), NOW(), 0),
(2, '大保养（全车油液）', 'Full Maintenance', 'BY-002', '', '<p>机油、变速箱油、刹车油、防冻液全车更换。</p>', '<p>Full fluid replacement.</p>', 260.00, 1280.00, '视车型配置', 180, 0, 1, '保养', '高级以上', 1, 0, 2, 1, NOW(), NOW(), 0),
(2, '四轮定位', 'Wheel Alignment', 'BY-003', '', '<p>3D 四轮定位，解决跑偏、吃胎。</p>', '<p>3D wheel alignment.</p>', 150.00, 150.00, '含检测', 60, 0, 1, '轮胎', '中级以上', 1, 0, 3, 1, NOW(), NOW(), 0),
(1, '刹车片更换', 'Brake Pad Replacement', 'WX-001', '', '<p>前后刹车片更换，含刹车系统检查。</p>', '<p>Brake pad replacement.</p>', 120.00, 380.00, '含工时费', 90, 0, 1, '机修', '高级以上', 1, 0, 4, 1, NOW(), NOW(), 0),
(3, '轮胎更换（含动平衡）', 'Tire Replacement', 'LT-001', '', '<p>单条轮胎更换、动平衡、胎压检测。</p>', '<p>Tire replacement with balancing.</p>', 40.00, 60.00, '不含轮胎材料', 40, 0, 1, '轮胎', '中级以上', 1, 0, 5, 1, NOW(), NOW(), 0),
(4, '冬夏胎换季寄存（4条）', 'Tire Storage (4 tires)', 'CT-001', '', '<p>换季寄存 4 条轮胎，独立编号、拍照留证，含一次免费取送。</p>', '<p>Seasonal tire storage for 4 tires.</p>', 0.00, 300.00, '每季，超期另计', 30, 0, 1, '轮胎', '不限', 1, 0, 6, 1, NOW(), NOW(), 0),
(5, '精致洗车', 'Premium Wash', 'MR-001', '', '<p>外观精洗、轮毂清洁、内饰吸尘。</p>', '<p>Premium exterior and interior wash.</p>', 30.00, 68.00, NULL, 40, 0, 1, '美容', '不限', 1, 0, 7, 1, NOW(), NOW(), 0),
(5, '打蜡镀膜', 'Wax & Coating', 'MR-002', '', '<p>全车打蜡+漆面镀膜，提升光泽与防护。</p>', '<p>Full car wax and coating.</p>', 150.00, 480.00, NULL, 120, 0, 1, '美容', '高级以上', 1, 0, 8, 1, NOW(), NOW(), 0),
(6, '年检代办', 'Annual Inspection Service', 'DZ-001', '', '<p>代客年检，含上门接送车。</p>', '<p>Annual inspection agent service.</p>', 50.00, 300.00, NULL, 120, 1, 1, '机修', '不限', 1, 0, 9, 1, NOW(), NOW(), 0),
(6, '道路救援', 'Roadside Assistance', 'DZ-002', '', '<p>市区 30 分钟极速救援，搭电、换胎、拖车。</p>', '<p>Roadside assistance.</p>', 0.00, 100.00, '起步价', 60, 1, 1, NULL, '不限', 1, 0, 10, 1, NOW(), NOW(), 0);

-- ---------------- 官网 CMS ----------------
INSERT INTO `cms_banner` (platform, title, title_en, image, link, sort, status, create_time, update_time, deleted) VALUES
('home', '透明报价 · 专业技师', 'Transparent Price, Professional Technicians', '', '', 1, 1, NOW(), NOW(), 0),
('home', '换季存胎寄存 · 免费取送', 'Seasonal Tire Storage', '', '', 2, 1, NOW(), NOW(), 0),
('www', '哈尔滨一站式汽车服务', 'One-stop Auto Service in Harbin', '', '', 1, 1, NOW(), NOW(), 0);

INSERT INTO `cms_brand_story` (title, title_en, cover, content, content_en, sort, status, create_time, update_time, deleted) VALUES
('710号汽车之家的故事', 'Our Story', '', '<p>710号汽车之家成立于哈尔滨，是一家社区型一站式汽车服务门店。</p>', '<p>Founded in Harbin, 710 Auto Home is a community-based one-stop auto service store.</p>', 1, 1, NOW(), NOW(), 0);

INSERT INTO `cms_case` (case_name, case_name_en, case_type, cover, before_images, after_images, content, content_en, view_count, sort, status, create_time, update_time, deleted) VALUES
('德系车发动机大修', 'Engine Overhaul', '维修', '', '[]', '[]', '<p>烧机油严重，拆解检查后更换活塞环。</p>', '', 0, 1, 1, NOW(), NOW(), 0),
('全车镀膜施工', 'Full Car Coating', '美容', '', '[]', '[]', '<p>漆面抛光后镀晶，光泽持久。</p>', '', 0, 2, 1, NOW(), NOW(), 0);

INSERT INTO `cms_article_category` (id, parent_id, category_name, category_name_en, sort, status, create_time, update_time, deleted) VALUES
(1, 0, '保养科普', 'Maintenance Tips', 1, 1, NOW(), NOW(), 0),
(2, 0, '换季存胎', 'Seasonal Tire', 2, 1, NOW(), NOW(), 0);

INSERT INTO `cms_article` (category_id, title, title_en, cover, summary, summary_en, content, content_en, author, seo_title, seo_keywords, seo_description, view_count, is_top, status, publish_time, create_time, update_time, deleted) VALUES
(1, '机油多久换一次？', 'How often to change engine oil?', '', '不同机油类型更换周期不同。', 'Oil change intervals.', '<p>全合成机油建议 8000-10000 公里更换。</p>', '', '710技师', '机油更换周期', '机油,保养', '机油更换周期科普', 0, 1, 1, NOW(), NOW(), NOW(), 0),
(2, '换季存胎要注意什么？', 'Tire storage tips', '', '存胎前清洗、编号、避免阳光直射。', 'Tire storage tips.', '<p>存胎前应清洗干净并做编号管理。</p>', '', '710技师', '存胎寄存注意事项', '存胎,换季', '存胎寄存注意事项', 0, 0, 1, NOW(), NOW(), NOW(), 0);

-- ---------------- 会员营销 ----------------
INSERT INTO `member_level` (id, level_name, level_code, min_points, discount, status, sort, create_time, update_time, deleted) VALUES
(1, '普通会员', 'NORMAL', 0, 1.00, 1, 1, NOW(), NOW(), 0),
(2, '银卡会员', 'SILVER', 1000, 0.95, 1, 2, NOW(), NOW(), 0),
(3, '金卡会员', 'GOLD', 5000, 0.90, 1, 3, NOW(), NOW(), 0);

INSERT INTO `coupon` (coupon_name, coupon_type, face_value, min_amount, discount, total_count, received_count, used_count, valid_type, start_time, end_time, valid_days, scope, description, status, create_time, update_time, deleted) VALUES
('新人满减券', '满减', 50.00, 300.00, NULL, 1000, 0, 0, '领取后N天', NULL, NULL, 30, '全店通用', '满300减50', 1, NOW(), NOW(), 0),
('工时抵扣券', '工时抵扣', 100.00, 200.00, NULL, 500, 0, 0, '领取后N天', NULL, NULL, 60, '工时费', '可抵扣100元工时费', 1, NOW(), NOW(), 0),
('洗车券', '洗车券', 68.00, 0.00, NULL, 2000, 0, 0, '领取后N天', NULL, NULL, 15, '精致洗车', '免费精致洗车一次', 1, NOW(), NOW(), 0);

INSERT INTO `recharge_rule` (rule_name, recharge_amount, gift_amount, gift_points, status, sort, create_time, update_time, deleted) VALUES
('充1000送100', 1000.00, 100.00, 100, 1, 1, NOW(), NOW(), 0),
('充2000送300', 2000.00, 300.00, 300, 1, 2, NOW(), NOW(), 0),
('充5000送1000', 5000.00, 1000.00, 1000, 1, 3, NOW(), NOW(), 0);

INSERT INTO `maintenance_package` (package_name, package_name_en, cover, description, price, original_price, times, valid_days, items, status, sort, create_time, update_time, deleted) VALUES
('保养年卡（4次小保养）', 'Annual Maintenance Card', '', '全年4次小保养，含机油机滤与工时。', 1280.00, 1520.00, 4, 365, '["小保养","全车检查"]', 1, 1, NOW(), NOW(), 0),
('洗车季卡（12次）', 'Wash Season Card', '', '12次精致洗车。', 600.00, 816.00, 12, 180, '["精致洗车"]', 1, 2, NOW(), NOW(), 0);

INSERT INTO `marketing_activity` (activity_name, activity_type, cover, description, start_time, end_time, rules, status, create_time, update_time, deleted) VALUES
('换季存胎8折', '节日', '', '换季期间存胎寄存8折优惠。', NOW(), DATE_ADD(NOW(), INTERVAL 90 DAY), '{"discount":0.8}', 1, NOW(), NOW(), 0),
('3人拼团洗车', '拼团', '', '3人成团，洗车立减20元。', NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), '{"groupSize":3,"discount":20}', 1, NOW(), NOW(), 0);

-- ---------------- 配件库存 ----------------
INSERT INTO `part_category` (id, parent_id, category_name, sort, status, create_time, update_time, deleted) VALUES
(1, 0, '机油', 1, 1, NOW(), NOW(), 0),
(2, 0, '滤清器', 2, 1, NOW(), NOW(), 0),
(3, 0, '轮胎', 3, 1, NOW(), NOW(), 0),
(4, 0, '电瓶', 4, 1, NOW(), NOW(), 0),
(5, 0, '刹车片', 5, 1, NOW(), NOW(), 0);

INSERT INTO `part_info` (part_code, part_name, category_id, brand, spec, unit, purchase_price, sale_price, stock, warn_stock, supplier, status, create_time, update_time, deleted) VALUES
('P-OIL-001', '全合成机油 5W-30 4L', 1, '美孚', '5W-30/4L', '桶', 180.00, 380.00, 30, 10, '哈尔滨汽配城', 1, NOW(), NOW(), 0),
('P-FIL-001', '机油滤清器', 2, '曼牌', '通用', '个', 25.00, 60.00, 50, 15, '哈尔滨汽配城', 1, NOW(), NOW(), 0),
('P-TIR-001', '朝阳轮胎 205/55R16', 3, '朝阳', '205/55R16', '条', 260.00, 420.00, 20, 8, '轮胎直供', 1, NOW(), NOW(), 0),
('P-BAT-001', '免维护电瓶 60Ah', 4, '骆驼', '60Ah', '个', 320.00, 520.00, 12, 5, '蓄电池直供', 1, NOW(), NOW(), 0),
('P-BRK-001', '前刹车片一套', 5, '博世', '通用', '套', 120.00, 260.00, 25, 10, '哈尔滨汽配城', 1, NOW(), NOW(), 0);

-- ---------------- 供应商 & 送货单（供应商端示例数据） ----------------
-- 供应商档案：user_id 绑定 sys_user 账号，供应商登录后据此识别身份
INSERT INTO `part_supplier` (id, user_id, supplier_name, contact_name, phone, address, remark, status, create_time, update_time, deleted) VALUES
(1, 5, '哈尔滨汽配城', '王经理', '13800000001', '哈尔滨市道里区汽配城 A 区 12 号', '机油/滤清器/刹车片主供', 1, NOW(), NOW(), 0),
(2, NULL, '轮胎直供', '刘经理', '13800000002', '哈尔滨市香坊区轮胎大世界', '轮胎供货商', 1, NOW(), NOW(), 0),
(3, NULL, '蓄电池直供', '赵经理', '13800000003', '哈尔滨市南岗区蓄电池批发市场', '电瓶供货商', 1, NOW(), NOW(), 0);

INSERT INTO `supplier_delivery` (id, delivery_no, supplier_id, supplier_name, store_id, item_count, total_amount, expect_date, status, submit_time, confirm_user_id, confirm_time, confirm_remark, remark, create_time, update_time, deleted) VALUES
(1, 'SH20260101000001', 1, '哈尔滨汽配城', 1, 2, 410.00, DATE_FORMAT(NOW(), '%Y-%m-%d'), '已入库', NOW(), 1, NOW(), '数量核对无误', '常规补货', NOW(), NOW(), 0),
(2, 'SH20260101000002', 1, '哈尔滨汽配城', 1, 1, 180.00, DATE_FORMAT(DATE_ADD(NOW(), INTERVAL 2 DAY), '%Y-%m-%d'), '待确认', NOW(), NULL, NULL, NULL, '机油补货', NOW(), NOW(), 0);

INSERT INTO `supplier_delivery_item` (delivery_id, part_id, part_name, spec, unit, quantity, unit_price, amount, remark, create_time, update_time, deleted) VALUES
(1, 1, '全合成机油 5W-30 4L', '5W-30/4L', '桶', 1, 180.00, 180.00, NULL, NOW(), NOW(), 0),
(1, 2, '机油滤清器', '通用', '个', 5, 46.00, 230.00, '促销价', NOW(), NOW(), 0),
(2, 1, '全合成机油 5W-30 4L', '5W-30/4L', '桶', 1, 180.00, 180.00, NULL, NOW(), NOW(), 0);

-- ---------------- 公众号回复规则 & 模板消息 ----------------
INSERT INTO `wechat_reply_rule` (rule_type, keyword, match_type, reply_type, content, status, sort, create_time, update_time, deleted) VALUES
('关注', NULL, NULL, '文本', '欢迎关注710号汽车之家！回复【预约】在线预约，【价格】查看透明报价，【存胎】办理换季寄存。', 1, 1, NOW(), NOW(), 0),
('关键词', '预约', '半匹配', '文本', '点击菜单【在线预约】即可预约到店，或回复您的车型与需求。', 1, 2, NOW(), NOW(), 0),
('关键词', '价格', '半匹配', '文本', '所有服务项目已明码标价，请在菜单【服务项目】中查看工时费与价格。', 1, 3, NOW(), NOW(), 0),
('关键词', '地址', '半匹配', '文本', '门店地址：哈尔滨市南岗区××路710号，导航搜索“710号汽车之家”。', 1, 4, NOW(), NOW(), 0),
('关键词', '存胎', '半匹配', '文本', '冬夏胎换季寄存，独立编号、拍照留证，回复【存胎】了解寄存规则与费用。', 1, 5, NOW(), NOW(), 0),
('关键词', '救援', '半匹配', '文本', '市区30分钟极速救援，请提供位置与车牌，或直接拨打 0451-8888710。', 1, 6, NOW(), NOW(), 0),
('默认', NULL, NULL, '文本', '感谢留言，客服会尽快回复。回复【预约】【价格】【地址】【存胎】【救援】获取帮助。', 1, 7, NOW(), NOW(), 0);

INSERT INTO `wechat_template_msg` (msg_type, template_id, title, content_template, status, remark, create_time, update_time, deleted) VALUES
('预约确认', '', '预约成功通知', '您的预约{{appointmentNo}}已确认，到店时间{{appointTime}}。', 1, '待后台填入微信模板ID', NOW(), NOW(), 0),
('接车', '', '车辆已接车', '您的爱车已接车，工单号{{orderNo}}。', 1, NULL, NOW(), NOW(), 0),
('施工进度', '', '施工进度通知', '工单{{orderNo}}状态更新：{{status}}。', 1, NULL, NOW(), NOW(), 0),
('完工待取车', '', '完工待取车', '工单{{orderNo}}已完工，请到店取车。', 1, NULL, NOW(), NOW(), 0),
('结算完成', '', '结算完成通知', '工单{{orderNo}}已结算，实收{{paidAmount}}元。', 1, NULL, NOW(), NOW(), 0),
('存胎到期提醒', '', '存胎到期提醒', '寄存单{{storageNo}}将于{{endDate}}到期，请及时取胎。', 1, NULL, NOW(), NOW(), 0),
('退款', '', '退款结果通知', '退款单{{refundNo}}状态：{{status}}。', 1, NULL, NOW(), NOW(), 0);

-- ---------------- 多端可视化菜单 ----------------
INSERT INTO `cms_menu` (platform, parent_id, menu_name, menu_name_en, menu_type, url, page_path, key_value, icon, sort, visible, status, create_time, update_time, deleted) VALUES
-- PC 官网导航
('public', 0, '首页', 'Home', 'view', '/', NULL, NULL, NULL, 1, 1, 1, NOW(), NOW(), 0),
('public', 0, '服务项目', 'Services', 'view', '/services', NULL, NULL, NULL, 2, 1, 1, NOW(), NOW(), 0),
('public', 0, '门店介绍', 'About', 'view', '/about', NULL, NULL, NULL, 3, 1, 1, NOW(), NOW(), 0),
('public', 0, '施工案例', 'Cases', 'view', '/cases', NULL, NULL, NULL, 4, 1, 1, NOW(), NOW(), 0),
('public', 0, '养护知识', 'Blog', 'view', '/articles', NULL, NULL, NULL, 5, 1, 1, NOW(), NOW(), 0),
('public', 0, '联系我们', 'Contact', 'view', '/contact', NULL, NULL, NULL, 6, 1, 1, NOW(), NOW(), 0),
-- 公众号菜单
('mp', 0, '在线预约', 'Book', 'miniprogram', NULL, 'pages/appointment/create', NULL, NULL, 1, 1, 1, NOW(), NOW(), 0),
('mp', 0, '服务与价格', 'Services', 'view', 'https://example.com/services', NULL, NULL, NULL, 2, 1, 1, NOW(), NOW(), 0),
('mp', 0, '我的爱车', 'My Car', 'click', NULL, NULL, 'MY_CAR', NULL, 3, 1, 1, NOW(), NOW(), 0),
-- 小程序 tabBar
('miniapp', 0, '首页', 'Home', 'view', NULL, 'pages/index/index', NULL, NULL, 1, 1, 1, NOW(), NOW(), 0),
('miniapp', 0, '服务', 'Services', 'view', NULL, 'pages/service/list', NULL, NULL, 2, 1, 1, NOW(), NOW(), 0),
('miniapp', 0, '存胎', 'Storage', 'view', NULL, 'pages/tire/list', NULL, NULL, 3, 1, 1, NOW(), NOW(), 0),
('miniapp', 0, '我的', 'Mine', 'view', NULL, 'pages/member/index', NULL, NULL, 4, 1, 1, NOW(), NOW(), 0),
-- H5 底部导航
('h5', 0, '首页', 'Home', 'view', '/', NULL, NULL, NULL, 1, 1, 1, NOW(), NOW(), 0),
('h5', 0, '服务', 'Services', 'view', '/services', NULL, NULL, NULL, 2, 1, 1, NOW(), NOW(), 0),
('h5', 0, '我的', 'Mine', 'view', '/mine', NULL, NULL, NULL, 3, 1, 1, NOW(), NOW(), 0);

-- ---------------- 数据字典（所有下拉统一由字典维护，禁止硬编码；幂等可重复执行） ----------------
DELETE FROM `sys_dict_data` WHERE `dict_type` IN (
  'tech_type','tech_level','station_type','shift','pay_type','gender',
  'work_order_status','appointment_status','member_source','coupon_type',
  'balance_type','points_type','tire_type','item_type');

DELETE FROM `sys_dict_type` WHERE `dict_type` IN (
  'tech_type','tech_level','station_type','shift','pay_type','gender',
  'work_order_status','appointment_status','member_source','coupon_type',
  'balance_type','points_type','tire_type','item_type');

INSERT INTO `sys_dict_type` (dict_type, dict_name, status, remark, create_time, update_time, deleted) VALUES
('tech_type', '工种', 1, '技师工种', NOW(), NOW(), 0),
('tech_level', '技师等级', 1, '技师等级', NOW(), NOW(), 0),
('station_type', '工位类型', 1, '工位类型', NOW(), NOW(), 0),
('shift', '排班班次', 1, '技师排班班次', NOW(), NOW(), 0),
('pay_type', '结算方式', 1, '工单结算支付方式', NOW(), NOW(), 0),
('gender', '性别', 1, '会员性别', NOW(), NOW(), 0),
('work_order_status', '工单状态', 1, '工单状态机', NOW(), NOW(), 0),
('appointment_status', '预约状态', 1, '预约单状态', NOW(), NOW(), 0),
('member_source', '会员来源', 1, '会员注册来源', NOW(), NOW(), 0),
('coupon_type', '优惠券类型', 1, '优惠券类型', NOW(), NOW(), 0),
('balance_type', '储值流水类型', 1, '会员储值变动类型', NOW(), NOW(), 0),
('points_type', '积分流水类型', 1, '会员积分变动类型', NOW(), NOW(), 0),
('tire_type', '轮胎类型', 1, '存胎轮胎类型', NOW(), NOW(), 0),
('item_type', '工单明细类型', 1, '工单用料类型', NOW(), NOW(), 0);

INSERT INTO `sys_dict_data` (dict_type, dict_label, dict_value, dict_sort, css_class, status, remark, create_time, update_time, deleted) VALUES
-- 工种
('tech_type', '机修', '机修', 1, NULL, 1, NULL, NOW(), NOW(), 0),
('tech_type', '钣喷', '钣喷', 2, NULL, 1, NULL, NOW(), NOW(), 0),
('tech_type', '美容', '美容', 3, NULL, 1, NULL, NOW(), NOW(), 0),
('tech_type', '轮胎', '轮胎', 4, NULL, 1, NULL, NOW(), NOW(), 0),
('tech_type', '电工', '电工', 5, NULL, 1, NULL, NOW(), NOW(), 0),
('tech_type', '通用', '通用', 9, NULL, 1, NULL, NOW(), NOW(), 0),
-- 技师等级
('tech_level', '初级', '初级', 1, NULL, 1, NULL, NOW(), NOW(), 0),
('tech_level', '中级', '中级', 2, NULL, 1, NULL, NOW(), NOW(), 0),
('tech_level', '高级', '高级', 3, NULL, 1, NULL, NOW(), NOW(), 0),
('tech_level', '专家', '专家', 4, NULL, 1, NULL, NOW(), NOW(), 0),
-- 工位类型
('station_type', '机修', '机修', 1, NULL, 1, NULL, NOW(), NOW(), 0),
('station_type', '保养', '保养', 2, NULL, 1, NULL, NOW(), NOW(), 0),
('station_type', '轮胎', '轮胎', 3, NULL, 1, NULL, NOW(), NOW(), 0),
('station_type', '美容', '美容', 4, NULL, 1, NULL, NOW(), NOW(), 0),
-- 班次
('shift', '早班', '早班', 1, NULL, 1, NULL, NOW(), NOW(), 0),
('shift', '中班', '中班', 2, NULL, 1, NULL, NOW(), NOW(), 0),
('shift', '晚班', '晚班', 3, NULL, 1, NULL, NOW(), NOW(), 0),
('shift', '休息', '休息', 9, NULL, 1, NULL, NOW(), NOW(), 0),
-- 结算方式
('pay_type', '微信', '微信', 1, NULL, 1, NULL, NOW(), NOW(), 0),
('pay_type', '储值', '储值', 2, NULL, 1, NULL, NOW(), NOW(), 0),
('pay_type', '现金', '现金', 3, NULL, 1, NULL, NOW(), NOW(), 0),
('pay_type', '混合', '混合', 4, NULL, 1, NULL, NOW(), NOW(), 0),
-- 性别
('gender', '未知', '0', 1, NULL, 1, NULL, NOW(), NOW(), 0),
('gender', '男', '1', 2, NULL, 1, NULL, NOW(), NOW(), 0),
('gender', '女', '2', 3, NULL, 1, NULL, NOW(), NOW(), 0),
-- 工单状态
('work_order_status', '待接车', '待接车', 1, NULL, 1, NULL, NOW(), NOW(), 0),
('work_order_status', '待派工', '待派工', 2, NULL, 1, NULL, NOW(), NOW(), 0),
('work_order_status', '施工中', '施工中', 3, NULL, 1, NULL, NOW(), NOW(), 0),
('work_order_status', '待质检', '待质检', 4, NULL, 1, NULL, NOW(), NOW(), 0),
('work_order_status', '待结算', '待结算', 5, NULL, 1, NULL, NOW(), NOW(), 0),
('work_order_status', '已离店', '已离店', 6, NULL, 1, NULL, NOW(), NOW(), 0),
('work_order_status', '已评价', '已评价', 7, NULL, 1, NULL, NOW(), NOW(), 0),
-- 预约状态
('appointment_status', '待确认', '待确认', 1, NULL, 1, NULL, NOW(), NOW(), 0),
('appointment_status', '已确认', '已确认', 2, NULL, 1, NULL, NOW(), NOW(), 0),
('appointment_status', '待接车', '待接车', 3, NULL, 1, NULL, NOW(), NOW(), 0),
('appointment_status', '已完成', '已完成', 4, NULL, 1, NULL, NOW(), NOW(), 0),
('appointment_status', '已取消', '已取消', 5, NULL, 1, NULL, NOW(), NOW(), 0),
-- 会员来源
('member_source', '公众号', 'mp', 1, NULL, 1, NULL, NOW(), NOW(), 0),
('member_source', '小程序', 'miniapp', 2, NULL, 1, NULL, NOW(), NOW(), 0),
('member_source', 'H5', 'h5', 3, NULL, 1, NULL, NOW(), NOW(), 0),
('member_source', '后台录入', 'admin', 4, NULL, 1, NULL, NOW(), NOW(), 0),
-- 优惠券类型
('coupon_type', '满减', '满减', 1, NULL, 1, NULL, NOW(), NOW(), 0),
('coupon_type', '折扣', '折扣', 2, NULL, 1, NULL, NOW(), NOW(), 0),
('coupon_type', '工时抵扣', '工时抵扣', 3, NULL, 1, NULL, NOW(), NOW(), 0),
('coupon_type', '洗车券', '洗车券', 4, NULL, 1, NULL, NOW(), NOW(), 0),
-- 储值流水类型
('balance_type', '充值', '充值', 1, NULL, 1, NULL, NOW(), NOW(), 0),
('balance_type', '消费', '消费', 2, NULL, 1, NULL, NOW(), NOW(), 0),
('balance_type', '退款', '退款', 3, NULL, 1, NULL, NOW(), NOW(), 0),
('balance_type', '赠送', '赠送', 4, NULL, 1, NULL, NOW(), NOW(), 0),
-- 积分流水类型
('points_type', '获取', '获取', 1, NULL, 1, NULL, NOW(), NOW(), 0),
('points_type', '消耗', '消耗', 2, NULL, 1, NULL, NOW(), NOW(), 0),
-- 轮胎类型
('tire_type', '冬胎', '冬胎', 1, NULL, 1, NULL, NOW(), NOW(), 0),
('tire_type', '夏胎', '夏胎', 2, NULL, 1, NULL, NOW(), NOW(), 0),
('tire_type', '四季胎', '四季胎', 3, NULL, 1, NULL, NOW(), NOW(), 0),
-- 工单明细类型
('item_type', '服务', '服务', 1, NULL, 1, NULL, NOW(), NOW(), 0),
('item_type', '配件', '配件', 2, NULL, 1, NULL, NOW(), NOW(), 0);

-- ---------------- 数据字典补充（退款状态 / 检查项）----------------
DELETE FROM `sys_dict_data` WHERE `dict_type` IN ('refund_status','check_item');
DELETE FROM `sys_dict_type` WHERE `dict_type` IN ('refund_status','check_item');

INSERT INTO `sys_dict_type` (dict_type, dict_name, status, remark, create_time, update_time, deleted) VALUES
('refund_status', '退款状态', 1, '退款单审核状态', NOW(), NOW(), 0),
('check_item', '检查项', 1, '接车检查项（可后台维护）', NOW(), NOW(), 0);

INSERT INTO `sys_dict_data` (dict_type, dict_label, dict_value, dict_sort, css_class, status, remark, create_time, update_time, deleted) VALUES
('refund_status', '待审核', '待审核', 1, NULL, 1, NULL, NOW(), NOW(), 0),
('refund_status', '已通过', '已通过', 2, NULL, 1, NULL, NOW(), NOW(), 0),
('refund_status', '已拒绝', '已拒绝', 3, NULL, 1, NULL, NOW(), NOW(), 0),
('refund_status', '已退款', '已退款', 4, NULL, 1, NULL, NOW(), NOW(), 0),
('check_item', '机油', '机油', 1, NULL, 1, NULL, NOW(), NOW(), 0),
('check_item', '机油滤芯', '机油滤芯', 2, NULL, 1, NULL, NOW(), NOW(), 0),
('check_item', '空气滤芯', '空气滤芯', 3, NULL, 1, NULL, NOW(), NOW(), 0),
('check_item', '空调滤芯', '空调滤芯', 4, NULL, 1, NULL, NOW(), NOW(), 0),
('check_item', '汽油滤芯', '汽油滤芯', 5, NULL, 1, NULL, NOW(), NOW(), 0),
('check_item', '刹车片', '刹车片', 6, NULL, 1, NULL, NOW(), NOW(), 0),
('check_item', '刹车油', '刹车油', 7, NULL, 1, NULL, NOW(), NOW(), 0),
('check_item', '防冻液', '防冻液', 8, NULL, 1, NULL, NOW(), NOW(), 0),
('check_item', '火花塞', '火花塞', 9, NULL, 1, NULL, NOW(), NOW(), 0),
('check_item', '电瓶', '电瓶', 10, NULL, 1, NULL, NOW(), NOW(), 0),
('check_item', '变速箱油', '变速箱油', 11, NULL, 1, NULL, NOW(), NOW(), 0),
('check_item', '轮胎', '轮胎', 12, NULL, 1, NULL, NOW(), NOW(), 0),
('check_item', '四轮定位', '四轮定位', 13, NULL, 1, NULL, NOW(), NOW(), 0),
('check_item', '灯光', '灯光', 14, NULL, 1, NULL, NOW(), NOW(), 0),
('check_item', '雨刷', '雨刷', 15, NULL, 1, NULL, NOW(), NOW(), 0),
('check_item', '玻璃水', '玻璃水', 16, NULL, 1, NULL, NOW(), NOW(), 0);

-- ---------------- 数据字典补充（券状态 / 活动类型 / 券有效期类型）----------------
DELETE FROM `sys_dict_data` WHERE `dict_type` IN ('member_coupon_status','activity_type','coupon_valid_type');
DELETE FROM `sys_dict_type` WHERE `dict_type` IN ('member_coupon_status','activity_type','coupon_valid_type');

INSERT INTO `sys_dict_type` (dict_type, dict_name, status, remark, create_time, update_time, deleted) VALUES
('member_coupon_status', '会员券状态', 1, '已领优惠券状态', NOW(), NOW(), 0),
('activity_type', '活动类型', 1, '营销活动类型', NOW(), NOW(), 0),
('coupon_valid_type', '券有效期类型', 1, '优惠券有效期计算方式', NOW(), NOW(), 0);

INSERT INTO `sys_dict_data` (dict_type, dict_label, dict_value, dict_sort, css_class, status, remark, create_time, update_time, deleted) VALUES
('member_coupon_status', '未使用', '未使用', 1, NULL, 1, NULL, NOW(), NOW(), 0),
('member_coupon_status', '已使用', '已使用', 2, NULL, 1, NULL, NOW(), NOW(), 0),
('member_coupon_status', '已过期', '已过期', 3, NULL, 1, NULL, NOW(), NOW(), 0),
('activity_type', '拼团', '拼团', 1, NULL, 1, NULL, NOW(), NOW(), 0),
('activity_type', '节日', '节日', 2, NULL, 1, NULL, NOW(), NOW(), 0),
('activity_type', '充值赠', '充值赠', 3, NULL, 1, NULL, NOW(), NOW(), 0),
('activity_type', '新人', '新人', 4, NULL, 1, NULL, NOW(), NOW(), 0),
('coupon_valid_type', '固定日期', '固定', 1, NULL, 1, NULL, NOW(), NOW(), 0),
('coupon_valid_type', '领取后N天', '领取后N天', 2, NULL, 1, NULL, NOW(), NOW(), 0);

-- ---------------- 数据字典补充（存胎状态 / 轮胎位置 / 库存状态）----------------
DELETE FROM `sys_dict_data` WHERE `dict_type` IN ('tire_storage_status','tire_position','stock_status');
DELETE FROM `sys_dict_type` WHERE `dict_type` IN ('tire_storage_status','tire_position','stock_status');

INSERT INTO `sys_dict_type` (dict_type, dict_name, status, remark, create_time, update_time, deleted) VALUES
('tire_storage_status', '存胎状态', 1, '存胎寄存单状态', NOW(), NOW(), 0),
('tire_position', '轮胎位置', 1, '轮胎安装位置', NOW(), NOW(), 0),
('stock_status', '库存状态', 1, '货位库存状态', NOW(), NOW(), 0);

INSERT INTO `sys_dict_data` (dict_type, dict_label, dict_value, dict_sort, css_class, status, remark, create_time, update_time, deleted) VALUES
('tire_storage_status', '已寄存', '已寄存', 1, NULL, 1, NULL, NOW(), NOW(), 0),
('tire_storage_status', '寄存中', '寄存中', 2, NULL, 1, NULL, NOW(), NOW(), 0),
('tire_storage_status', '待取胎', '待取胎', 3, NULL, 1, NULL, NOW(), NOW(), 0),
('tire_storage_status', '已取胎', '已取胎', 4, NULL, 1, NULL, NOW(), NOW(), 0),
('tire_storage_status', '超期', '超期', 5, NULL, 1, NULL, NOW(), NOW(), 0),
('tire_position', '左前', '左前', 1, NULL, 1, NULL, NOW(), NOW(), 0),
('tire_position', '右前', '右前', 2, NULL, 1, NULL, NOW(), NOW(), 0),
('tire_position', '左后', '左后', 3, NULL, 1, NULL, NOW(), NOW(), 0),
('tire_position', '右后', '右后', 4, NULL, 1, NULL, NOW(), NOW(), 0),
('tire_position', '备胎', '备胎', 5, NULL, 1, NULL, NOW(), NOW(), 0),
('stock_status', '在库', '在库', 1, NULL, 1, NULL, NOW(), NOW(), 0),
('stock_status', '已出库', '已出库', 2, NULL, 1, NULL, NOW(), NOW(), 0);
