/**
 * 710号汽车之家 —— 后端代码生成器
 * 根据 sql/tables.js 中的表定义，生成：
 *   - entity 实体
 *   - mapper 映射
 *   - service 接口 + impl
 *   - admin CRUD 控制器
 *   - sql/schema.sql 建表脚本
 * 用法： node tools/generate.js
 */
const fs = require('fs');
const path = require('path');
const { TABLES } = require('./tables');

const BACKEND = path.resolve(__dirname, '..', 'backend', 'src', 'main', 'java', 'com', 'auto710');
const SQL_DIR = path.resolve(__dirname, '..', 'sql');

const DIRS = {
  entity: path.join(BACKEND, 'entity'),
  mapper: path.join(BACKEND, 'mapper'),
  service: path.join(BACKEND, 'service'),
  serviceImpl: path.join(BACKEND, 'service', 'impl'),
  controller: path.join(BACKEND, 'controller', 'admin'),
};
Object.values(DIRS).forEach((d) => fs.mkdirSync(d, { recursive: true }));
fs.mkdirSync(SQL_DIR, { recursive: true });

const pascal = (s) => s.split('_').map((w) => w.charAt(0).toUpperCase() + w.slice(1)).join('');
const camel = (s) => { const p = pascal(s); return p.charAt(0).toLowerCase() + p.slice(1); };
const kebab = (s) => s.replace(/_/g, '-');

const JAVA_TYPE = (sqlType) => {
  const t = sqlType.toLowerCase();
  if (t.startsWith('bigint')) return 'Long';
  if (t.startsWith('int') || t.startsWith('tinyint')) return 'Integer';
  if (t.startsWith('decimal') || t.startsWith('double')) return 'BigDecimal';
  if (t.startsWith('datetime') || t.startsWith('timestamp') || t.startsWith('date')) return 'LocalDateTime';
  return 'String';
};

const SQL_COLUMN = (f) => {
  const [name, type, comment] = f;
  let col = `  \`${name}\` ${type.toUpperCase()}`;
  if (!/text$/i.test(type)) col += ' DEFAULT NULL';
  return `${col} COMMENT '${comment}'`;
};

const header = (comment) => `/**\n * ${comment}\n * <p>710号汽车之家 数字化多端平台</p>\n */\n`;

let schemaSql = [
  '-- =====================================================================',
  '-- 710号汽车之家 数字化多端平台 数据库初始化脚本 (MySQL 8.0 / utf8mb4)',
  '-- 自动生成，请勿手工修改；修改表定义请编辑 tools/tables.js 后重新执行 node tools/generate.js',
  '-- =====================================================================',
  '',
  'CREATE DATABASE IF NOT EXISTS `auto_repair` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;',
  'USE `auto_repair`;',
  'SET NAMES utf8mb4;',
  '',
].join('\n');

let count = 0;
for (const t of TABLES) {
  const cls = pascal(t.t);
  const varName = camel(t.t);
  const fields = t.f;

  // ---------- Entity ----------
  const needBigDecimal = fields.some((f) => JAVA_TYPE(f[1]) === 'BigDecimal');
  // 实体固定包含 create_time / update_time，故始终需要 LocalDateTime 与 JsonFormat
  const needDateTime = true;
  const imports = [
    'import com.baomidou.mybatisplus.annotation.*;',
    needBigDecimal ? 'import java.math.BigDecimal;' : null,
    needDateTime ? 'import java.time.LocalDateTime;' : null,
    needDateTime ? 'import com.fasterxml.jackson.annotation.JsonFormat;' : null,
    'import lombok.Data;',
    'import java.io.Serializable;',
  ].filter(Boolean).join('\n');

  const fieldLines = fields.map((f) => {
    const [name, type, comment] = f;
    const jt = JAVA_TYPE(type);
    const ann = jt === 'LocalDateTime' ? '    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")\n' : '';
    return `${ann}    /** ${comment} */\n    private ${jt} ${camel(name)};`;
  }).join('\n\n');

  const entity = `package com.auto710.entity;

${imports}

${header(t.c + '（' + t.t + '）')}@Data
@TableName("${t.t}")
public class ${cls} implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

${fieldLines}

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除 0否 1是 */
    @TableLogic
    @TableField(select = false)
    private Integer deleted;
}
`;

  // ---------- Mapper ----------
  const mapper = `package com.auto710.mapper;

import com.auto710.entity.${cls};
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

${header(t.c + ' Mapper')}@Mapper
public interface ${cls}Mapper extends BaseMapper<${cls}> {
}
`;

  // ---------- Service ----------
  const service = `package com.auto710.service;

import com.auto710.entity.${cls};
import com.baomidou.mybatisplus.extension.service.IService;

${header(t.c + ' Service')}public interface ${cls}Service extends IService<${cls}> {
}
`;

  // ---------- ServiceImpl ----------
  const serviceImpl = `package com.auto710.service.impl;

import com.auto710.entity.${cls};
import com.auto710.mapper.${cls}Mapper;
import com.auto710.service.${cls}Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

${header(t.c + ' Service 实现')}@Service
public class ${cls}ServiceImpl extends ServiceImpl<${cls}Mapper, ${cls}> implements ${cls}Service {
${t.implExtra || ''}}
`;

  // ---------- Controller ----------
  const moduleName = t.c.replace(/（.*?）|\(.*?\)/g, '').trim();
  const controller = `package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.${cls};
import com.auto710.service.${cls}Service;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

${header('【后台】' + t.c + ' 接口')}@Tag(name = "${moduleName}管理")
@RestController
@RequestMapping("${t.api || '/api/admin/' + kebab(t.t)}")
public class ${cls}Controller extends BaseController<${cls}Service, ${cls}> {

    public ${cls}Controller(${cls}Service service) {
        super(service);
    }
}
`;

  fs.writeFileSync(path.join(DIRS.entity, cls + '.java'), entity);
  fs.writeFileSync(path.join(DIRS.mapper, cls + 'Mapper.java'), mapper);
  fs.writeFileSync(path.join(DIRS.service, cls + 'Service.java'), service);
  fs.writeFileSync(path.join(DIRS.serviceImpl, cls + 'ServiceImpl.java'), serviceImpl);
  fs.writeFileSync(path.join(DIRS.controller, cls + 'Controller.java'), controller);

  // ---------- SQL ----------
  const cols = ['  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT \'主键\''];
  fields.forEach((f) => cols.push(SQL_COLUMN(f)));
  cols.push("  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间'");
  cols.push("  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间'");
  cols.push("  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否1是'");
  cols.push('  PRIMARY KEY (`id`)');
  (t.uk || []).forEach((u) => cols.push(`  UNIQUE KEY \`uk_${t.t}_${u}\` (\`${u}\`)`));
  (t.idx || []).forEach((i) => cols.push(`  KEY \`idx_${t.t}_${i}\` (\`${i}\`)`));

  schemaSql += `-- ${t.c}\nDROP TABLE IF EXISTS \`${t.t}\`;\nCREATE TABLE \`${t.t}\` (\n${cols.join(',\n')}\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='${t.c}';\n\n`;
  count++;
}

fs.writeFileSync(path.join(SQL_DIR, 'schema.sql'), schemaSql);
console.log(`[generate] 已生成 ${count} 张表的实体/映射/服务/控制器，以及 sql/schema.sql`);
