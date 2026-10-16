package com.auto710.storage;

import cn.hutool.core.util.StrUtil;
import com.auto710.common.util.EncryptUtil;
import com.auto710.entity.SysConfig;
import com.auto710.service.SysConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 存储配置服务：从 sys_config 读写当前驱动配置，密钥加密存储
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StorageConfigService {

    public static final String KEY_DRIVER = "storage.driver";
    public static final String KEY_CONFIG = "storage.config";

    private final SysConfigService configService;
    private final ObjectMapper objectMapper;

    @Value("${auto710.storage.local.base-path:./data/upload}")
    private String defaultBasePath;

    @Value("${auto710.storage.local.url-prefix:http://127.0.0.1:8080/upload/local}")
    private String defaultUrlPrefix;

    /**
     * 当前驱动类型
     */
    public String currentDriver() {
        SysConfig config = getConfig(KEY_DRIVER);
        return config == null || StrUtil.isBlank(config.getConfigValue()) ? "local" : config.getConfigValue();
    }

    /**
     * 当前存储配置（密钥已解密）
     */
    public StorageConfig current() {
        SysConfig config = getConfig(KEY_CONFIG);
        StorageConfig result;
        if (config == null || StrUtil.isBlank(config.getConfigValue())) {
            result = defaultLocal();
        } else {
            try {
                result = objectMapper.readValue(config.getConfigValue(), StorageConfig.class);
            } catch (Exception e) {
                log.warn("存储配置解析失败，回退本地磁盘: {}", e.getMessage());
                result = defaultLocal();
            }
        }
        if (StrUtil.isNotBlank(result.getSecretKey())) {
            result.setSecretKey(EncryptUtil.decrypt(result.getSecretKey()));
        }
        if (StrUtil.isBlank(result.getType())) {
            result.setType(currentDriver());
        }
        return result;
    }

    /**
     * 当前配置（掩码，供页面展示）
     */
    public StorageConfig currentMasked() {
        return current().copyMasked();
    }

    /**
     * 保存配置
     */
    public void save(StorageConfig storageConfig) {
        StorageConfig persist = new StorageConfig();
        persist.setType(storageConfig.getType());
        persist.setEndpoint(storageConfig.getEndpoint());
        persist.setRegion(storageConfig.getRegion());
        persist.setBucket(storageConfig.getBucket());
        persist.setDomain(storageConfig.getDomain());
        persist.setBasePath(StrUtil.blankToDefault(storageConfig.getBasePath(), defaultBasePath));
        persist.setUrlPrefix(StrUtil.blankToDefault(storageConfig.getUrlPrefix(), defaultUrlPrefix));
        persist.setPathStyle(storageConfig.getPathStyle());
        persist.setAccessKey(storageConfig.getAccessKey());
        persist.setSecretKey(StrUtil.isBlank(storageConfig.getSecretKey())
                ? null : EncryptUtil.encrypt(storageConfig.getSecretKey()));
        try {
            saveConfig(KEY_CONFIG, objectMapper.writeValueAsString(persist), "对象存储配置", "storage", "json");
            saveConfig(KEY_DRIVER, storageConfig.getType(), "当前存储驱动", "storage", "string");
        } catch (Exception e) {
            throw new com.auto710.common.exception.BusinessException("存储配置保存失败: " + e.getMessage());
        }
    }

    private StorageConfig defaultLocal() {
        StorageConfig config = new StorageConfig();
        config.setType("local");
        config.setBasePath(defaultBasePath);
        config.setUrlPrefix(defaultUrlPrefix);
        return config;
    }

    private SysConfig getConfig(String key) {
        return configService.getOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, key));
    }

    private void saveConfig(String key, String value, String name, String group, String type) {
        SysConfig config = getConfig(key);
        if (config == null) {
            config = new SysConfig();
            config.setConfigKey(key);
        }
        config.setConfigValue(value);
        config.setConfigName(name);
        config.setConfigGroup(group);
        config.setConfigType(type);
        config.setEncrypted("secret".equals(type) ? 1 : 0);
        config.setUpdateTime(LocalDateTime.now());
        configService.saveOrUpdate(config);
    }
}
