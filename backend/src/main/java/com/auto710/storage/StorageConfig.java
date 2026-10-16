package com.auto710.storage;

import lombok.Data;

/**
 * 对象存储配置
 */
@Data
public class StorageConfig {

    /** 驱动类型 local / s3 / oss / cos / minio */
    private String type;

    /** 服务地址 Endpoint */
    private String endpoint;

    /** 区域 Region */
    private String region;

    /** AccessKey ID */
    private String accessKey;

    /** AccessKey Secret */
    private String secretKey;

    /** 存储桶 */
    private String bucket;

    /** 外网访问域名 */
    private String domain;

    /** 本地磁盘存储路径 */
    private String basePath;

    /** 本地磁盘访问前缀 */
    private String urlPrefix;

    /** 是否使用 pathStyle（MinIO 建议 true） */
    private Boolean pathStyle;

    /** 测试连接用 */
    public StorageConfig copyMasked() {
        StorageConfig c = new StorageConfig();
        c.setType(type);
        c.setEndpoint(endpoint);
        c.setRegion(region);
        c.setBucket(bucket);
        c.setDomain(domain);
        c.setBasePath(basePath);
        c.setUrlPrefix(urlPrefix);
        c.setPathStyle(pathStyle);
        c.setAccessKey(mask(accessKey));
        c.setSecretKey(mask(secretKey));
        return c;
    }

    private String mask(String value) {
        if (value == null || value.length() <= 8) {
            return value == null ? null : "****";
        }
        return value.substring(0, 4) + "****" + value.substring(value.length() - 4);
    }
}
