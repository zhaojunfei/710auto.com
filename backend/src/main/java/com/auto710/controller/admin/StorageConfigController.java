package com.auto710.controller.admin;

import com.auto710.common.annotation.AuditLog;
import com.auto710.common.api.Result;
import com.auto710.storage.StorageConfig;
import com.auto710.storage.StorageConfigService;
import com.auto710.storage.StorageDriverFactory;
import com.auto710.storage.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 【后台】对象存储配置（重点 UI：先选驱动 → 动态表单 → 连接测试 → 保存并应用）
 */
@Tag(name = "对象存储配置")
@RestController
@RequestMapping("/api/admin/storage")
@RequiredArgsConstructor
public class StorageConfigController {

    private final StorageConfigService storageConfigService;
    private final StorageService storageService;

    @Operation(summary = "支持的存储驱动列表（5 张卡片）")
    @GetMapping("/drivers")
    public Result<Map<String, String>> drivers() {
        return Result.ok(StorageDriverFactory.supported());
    }

    @Operation(summary = "当前存储配置（密钥掩码）")
    @GetMapping("/config")
    public Result<StorageConfig> config() {
        return Result.ok(storageConfigService.currentMasked());
    }

    @Operation(summary = "连接测试：实际上传 1KB 测试文件并读取")
    @PostMapping("/test")
    public Result<Boolean> test(@RequestBody StorageConfig config) {
        StorageConfig current = storageConfigService.current();
        fillSecret(config, current);
        boolean ok = storageService.testConnection(config);
        return ok ? Result.ok(true) : Result.fail("连接测试失败，请检查 Endpoint / AK / SK / Bucket 是否正确");
    }

    @Operation(summary = "保存并应用（切换驱动）")
    @PostMapping("/save")
    @AuditLog(module = "对象存储", operation = "保存配置")
    public Result<Boolean> save(@RequestBody StorageConfig config) {
        fillSecret(config, storageConfigService.current());
        storageConfigService.save(config);
        // 触发驱动重建
        storageService.driver();
        return Result.ok(true);
    }

    /**
     * 当前端回传掩码密钥时，使用数据库中的真实密钥
     */
    private void fillSecret(StorageConfig config, StorageConfig current) {
        if (config.getSecretKey() == null || config.getSecretKey().contains("****")) {
            config.setSecretKey(current.getSecretKey());
        }
        if (config.getType() == null) {
            config.setType(current.getType());
        }
    }
}
