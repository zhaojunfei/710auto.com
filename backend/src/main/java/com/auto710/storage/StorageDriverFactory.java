package com.auto710.storage;

import com.auto710.common.exception.BusinessException;
import com.auto710.storage.impl.AliyunOssStorageDriver;
import com.auto710.storage.impl.LocalDiskStorageDriver;
import com.auto710.storage.impl.MinioStorageDriver;
import com.auto710.storage.impl.S3StorageDriver;
import com.auto710.storage.impl.TencentCosStorageDriver;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 存储驱动工厂
 */
public final class StorageDriverFactory {

    private StorageDriverFactory() {
    }

    public static StorageDriver create(String type) {
        return switch (type) {
            case "local" -> new LocalDiskStorageDriver();
            case "s3" -> new S3StorageDriver();
            case "oss" -> new AliyunOssStorageDriver();
            case "cos" -> new TencentCosStorageDriver();
            case "minio" -> new MinioStorageDriver();
            default -> throw new BusinessException("不支持的存储驱动: " + type);
        };
    }

    /**
     * 支持的驱动清单（供后台 5 张卡片单选渲染）
     */
    public static Map<String, String> supported() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("local", "本地磁盘");
        map.put("s3", "AWS S3");
        map.put("oss", "阿里云 OSS");
        map.put("cos", "腾讯云 COS");
        map.put("minio", "MinIO");
        return map;
    }
}
