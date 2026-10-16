package com.auto710.controller.admin;

import com.auto710.common.annotation.AuditLog;
import com.auto710.common.api.Result;
import com.auto710.config.ConfigService;
import com.auto710.entity.SysConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 【后台】系统全局参数可视化配置
 */
@Tag(name = "系统参数配置")
@RestController
@RequestMapping("/api/admin/config")
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService configService;

    @Operation(summary = "配置分组列表")
    @GetMapping("/groups")
    public Result<List<String>> groups() {
        return Result.ok(configService.groups());
    }

    @Operation(summary = "按分组读取配置")
    @GetMapping("/group/{group}")
    public Result<List<SysConfig>> group(@PathVariable String group) {
        List<SysConfig> configs = configService.listByGroup(group);
        configs.forEach(c -> {
            if (c.getEncrypted() != null && c.getEncrypted() == 1 && c.getConfigValue() != null) {
                c.setConfigValue("****");
            }
        });
        return Result.ok(configs);
    }

    @Operation(summary = "保存单项配置（实时生效）")
    @PostMapping("/save")
    @AuditLog(module = "系统参数", operation = "保存配置")
    public Result<Boolean> save(@RequestBody SysConfig config) {
        configService.save(config);
        return Result.ok(true);
    }

    @Operation(summary = "批量保存配置")
    @PostMapping("/save-batch")
    @AuditLog(module = "系统参数", operation = "批量保存配置")
    public Result<Boolean> saveBatch(@RequestBody List<SysConfig> configs) {
        configService.saveBatch(configs);
        return Result.ok(true);
    }

    @Operation(summary = "按分组保存配置")
    @PostMapping("/save-group/{group}")
    @AuditLog(module = "系统参数", operation = "保存分组配置")
    public Result<Boolean> saveGroup(@PathVariable String group, @RequestBody Map<String, String> values) {
        configService.saveMap(group, values);
        return Result.ok(true);
    }

    @Operation(summary = "前台站点公开配置")
    @GetMapping("/site")
    public Result<Map<String, String>> site() {
        return Result.ok(configService.siteConfig());
    }
}
