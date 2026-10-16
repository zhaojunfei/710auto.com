package com.auto710.storage.impl;

import com.auto710.storage.StorageConfig;

/**
 * MinIO 存储驱动（S3 协议，默认 pathStyle）
 */
public class MinioStorageDriver extends S3StorageDriver {

    @Override
    public String type() {
        return "minio";
    }

    @Override
    public String name() {
        return "MinIO";
    }

    @Override
    protected boolean defaultPathStyle() {
        return true;
    }

    @Override
    public void init(StorageConfig config) {
        if (config.getPathStyle() == null) {
            config.setPathStyle(true);
        }
        super.init(config);
    }
}
