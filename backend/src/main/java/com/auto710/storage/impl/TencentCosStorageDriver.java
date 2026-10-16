package com.auto710.storage.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.auto710.storage.StorageConfig;
import com.auto710.storage.StorageDriver;
import com.auto710.storage.StorageKeyUtil;
import com.auto710.storage.StorageResult;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * 腾讯云 COS 存储驱动
 */
public class TencentCosStorageDriver implements StorageDriver {

    private StorageConfig config;
    private COSClient client;

    @Override
    public String type() {
        return "cos";
    }

    @Override
    public String name() {
        return "腾讯云 COS";
    }

    @Override
    public void init(StorageConfig config) {
        this.config = config;
        COSCredentials credentials = new BasicCOSCredentials(config.getAccessKey(), config.getSecretKey());
        ClientConfig clientConfig = new ClientConfig(new Region(
                StrUtil.blankToDefault(config.getRegion(), "ap-guangzhou")));
        clientConfig.setHttpProtocol(config.getEndpoint() != null && config.getEndpoint().startsWith("http://")
                ? HttpProtocol.http : HttpProtocol.https);
        this.client = new COSClient(credentials, clientConfig);
    }

    @Override
    public StorageResult upload(byte[] bytes, String originalName, String contentType, String dir) {
        String key = StorageKeyUtil.buildKey(originalName, dir);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(bytes.length);
        metadata.setContentType(StrUtil.blankToDefault(contentType, "application/octet-stream"));
        PutObjectRequest request = new PutObjectRequest(config.getBucket(), key,
                new ByteArrayInputStream(bytes), metadata);
        client.putObject(request);
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
        return "https://" + config.getBucket() + ".cos." + config.getRegion() + ".myqcloud.com/" + key;
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
