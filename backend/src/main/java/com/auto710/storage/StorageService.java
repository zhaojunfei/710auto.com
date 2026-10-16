package com.auto710.storage;

import cn.hutool.core.util.StrUtil;
import com.auto710.common.exception.BusinessException;
import com.auto710.entity.SysFile;
import com.auto710.service.SysFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 对象存储门面服务：业务统一调用，按当前驱动上传/删除/取址
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    private final StorageConfigService storageConfigService;
    private final SysFileService sysFileService;

    private volatile StorageDriver driver;
    private volatile String driverType;

    /**
     * 获取当前驱动实例（配置变更后自动重建）
     */
    public StorageDriver driver() {
        String currentType = storageConfigService.currentDriver();
        if (driver == null || !StrUtil.equals(driverType, currentType)) {
            synchronized (this) {
                if (driver == null || !StrUtil.equals(driverType, currentType)) {
                    if (driver != null) {
                        try {
                            driver.close();
                        } catch (Exception ignored) {
                        }
                    }
                    StorageConfig config = storageConfigService.current();
                    StorageDriver newDriver = StorageDriverFactory.create(config.getType());
                    newDriver.init(config);
                    driver = newDriver;
                    driverType = config.getType();
                    log.info("对象存储驱动已加载: {}", config.getType());
                }
            }
        }
        return driver;
    }

    /**
     * 上传并记录文件
     */
    public StorageResult upload(MultipartFile file, String dir, String bizType) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        try {
            byte[] bytes = file.getBytes();
            StorageResult result = driver().upload(bytes, file.getOriginalFilename(),
                    file.getContentType(), dir);
            saveRecord(result, bizType);
            return result;
        } catch (IOException e) {
            throw new BusinessException("文件读取失败: " + e.getMessage());
        }
    }

    /**
     * 上传字节数组（富文本粘贴图片等）
     */
    public StorageResult uploadBytes(byte[] bytes, String originalName, String contentType, String dir, String bizType) {
        StorageResult result = driver().upload(bytes, originalName, contentType, dir);
        saveRecord(result, bizType);
        return result;
    }

    public void delete(String key) {
        driver().delete(key);
    }

    public String url(String key) {
        return driver().url(key);
    }

    /**
     * 测试指定驱动配置连接（配置页「连接测试」）
     */
    public boolean testConnection(StorageConfig config) {
        StorageDriver testDriver = StorageDriverFactory.create(config.getType());
        try {
            testDriver.init(config);
            return testDriver.testConnection();
        } catch (Exception e) {
            log.warn("存储连接测试失败: {}", e.getMessage());
            return false;
        } finally {
            try {
                testDriver.close();
            } catch (Exception ignored) {
            }
        }
    }

    private void saveRecord(StorageResult result, String bizType) {
        try {
            SysFile record = new SysFile();
            record.setFileName(result.getKey());
            record.setOriginalName(result.getOriginalName());
            record.setFileUrl(result.getUrl());
            record.setFileType(StrUtil.subAfter(result.getOriginalName(), ".", true));
            record.setFileSize(result.getSize());
            record.setStorageDriver(result.getDriver());
            record.setBucket(storageConfigService.current().getBucket());
            record.setBizType(bizType);
            sysFileService.save(record);
        } catch (Exception e) {
            log.warn("文件记录保存失败: {}", e.getMessage());
        }
    }
}
