package com.auto710.storage.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.auto710.storage.StorageConfig;
import com.auto710.storage.StorageDriver;
import com.auto710.storage.StorageKeyUtil;
import com.auto710.storage.StorageResult;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * 阿里云 OSS 存储驱动
 */
public class AliyunOssStorageDriver implements StorageDriver {

    private StorageConfig config;
    private OSS client;

    @Override
    public String type() {
        return "oss";
    }

    @Override
    public String name() {
        return "阿里云 OSS";
    }

    @Override
    public void init(StorageConfig config) {
        this.config = config;
        this.client = new OSSClientBuilder().build(config.getEndpoint(), config.getAccessKey(), config.getSecretKey());
    }

    @Override
    public StorageResult upload(byte[] bytes, String originalName, String contentType, String dir) {
        String key = StorageKeyUtil.buildKey(originalName, dir);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(bytes.length);
        metadata.setContentType(StrUtil.blankToDefault(contentType, "application/octet-stream"));
        client.putObject(config.getBucket(), key, new ByteArrayInputStream(bytes), metadata);
        return StorageResult.of(key, url(key), originalName, bytes.length, type());
    }

    @Override
    public StorageResult upload(InputStream inputStream, String originalName, String contentType, String dir) {
        return upload(IoUtil.readBytes(inputStream), originalName, contentType, dir);
    }

    @Override
    public InputStream download(String key) {
        return client.getObject(config.getBucket(), key).getObjectContent();
    }

    @Override
    public void delete(String key) {
        client.deleteObject(config.getBucket(), key);
    }

    @Override
    public boolean exists(String key) {
        return client.doesObjectExist(config.getBucket(), key);
    }

    @Override
    public String url(String key) {
        if (StrUtil.isNotBlank(config.getDomain())) {
            return StrUtil.strip(config.getDomain(), "/") + "/" + key;
        }
        String endpoint = StrUtil.strip(config.getEndpoint(), "/").replace("https://", "").replace("http://", "");
        return "https://" + config.getBucket() + "." + endpoint + "/" + key;
    }

    @Override
    public boolean testConnection() {
        try {
            StorageResult result = upload(new byte[1024], "connection-test.txt", "text/plain", "test");
            boolean ok = exists(result.getKey());
            delete(result.getKey());
            return ok;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void close() {
        if (client != null) {
            client.shutdown();
        }
    }
}
