package com.auto710.storage.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.auto710.storage.StorageConfig;
import com.auto710.storage.StorageDriver;
import com.auto710.storage.StorageKeyUtil;
import com.auto710.storage.StorageResult;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.net.URI;

/**
 * AWS S3 存储驱动（MinIO 复用本实现，仅 pathStyle 不同）
 */
public class S3StorageDriver implements StorageDriver {

    protected StorageConfig config;
    protected S3Client client;

    @Override
    public String type() {
        return "s3";
    }

    @Override
    public String name() {
        return "AWS S3";
    }

    protected boolean defaultPathStyle() {
        return false;
    }

    @Override
    public void init(StorageConfig config) {
        this.config = config;
        boolean pathStyle = config.getPathStyle() != null ? config.getPathStyle() : defaultPathStyle();
        var builder = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(config.getAccessKey(), config.getSecretKey())))
                .region(Region.of(StrUtil.blankToDefault(config.getRegion(), "us-east-1")))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(pathStyle)
                        .build());
        if (StrUtil.isNotBlank(config.getEndpoint())) {
            builder.endpointOverride(URI.create(config.getEndpoint()));
        }
        this.client = builder.build();
    }

    @Override
    public StorageResult upload(byte[] bytes, String originalName, String contentType, String dir) {
        String key = StorageKeyUtil.buildKey(originalName, dir);
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(config.getBucket())
                .key(key)
                .contentType(StrUtil.blankToDefault(contentType, "application/octet-stream"))
                .build();
        client.putObject(request, RequestBody.fromBytes(bytes));
        return StorageResult.of(key, url(key), originalName, bytes.length, type());
    }

    @Override
    public StorageResult upload(InputStream inputStream, String originalName, String contentType, String dir) {
        return upload(IoUtil.readBytes(inputStream), originalName, contentType, dir);
    }

    @Override
    public InputStream download(String key) {
        return client.getObjectAsBytes(GetObjectRequest.builder()
                .bucket(config.getBucket()).key(key).build()).asInputStream();
    }

    @Override
    public void delete(String key) {
        client.deleteObject(DeleteObjectRequest.builder()
                .bucket(config.getBucket()).key(key).build());
    }

    @Override
    public boolean exists(String key) {
        try {
            client.headObject(HeadObjectRequest.builder()
                    .bucket(config.getBucket()).key(key).build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            return false;
        }
    }

    @Override
    public String url(String key) {
        if (StrUtil.isNotBlank(config.getDomain())) {
            return StrUtil.strip(config.getDomain(), "/") + "/" + key;
        }
        if (StrUtil.isNotBlank(config.getEndpoint())) {
            return StrUtil.strip(config.getEndpoint(), "/") + "/" + config.getBucket() + "/" + key;
        }
        return "https://" + config.getBucket() + ".s3." + config.getRegion() + ".amazonaws.com/" + key;
    }

    @Override
    public boolean testConnection() {
        try {
            byte[] data = new byte[1024];
            StorageResult result = upload(data, "connection-test.txt", "text/plain", "test");
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
            client.close();
        }
    }
}
