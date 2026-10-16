package com.auto710.storage.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.auto710.common.exception.BusinessException;
import com.auto710.storage.StorageConfig;
import com.auto710.storage.StorageDriver;
import com.auto710.storage.StorageKeyUtil;
import com.auto710.storage.StorageResult;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

/**
 * 本地磁盘存储驱动
 */
public class LocalDiskStorageDriver implements StorageDriver {

    private StorageConfig config;
    private File baseDir;

    @Override
    public String type() {
        return "local";
    }

    @Override
    public String name() {
        return "本地磁盘";
    }

    @Override
    public void init(StorageConfig config) {
        this.config = config;
        this.baseDir = new File(config.getBasePath() == null ? "./data/upload" : config.getBasePath());
        if (!baseDir.exists() && !baseDir.mkdirs()) {
            throw new BusinessException("本地存储目录创建失败: " + baseDir.getAbsolutePath());
        }
    }

    @Override
    public StorageResult upload(byte[] bytes, String originalName, String contentType, String dir) {
        String key = StorageKeyUtil.buildKey(originalName, dir);
        File target = new File(baseDir, key);
        FileUtil.mkParentDirs(target);
        FileUtil.writeBytes(bytes, target);
        return StorageResult.of(key, url(key), originalName, bytes.length, type());
    }

    @Override
    public StorageResult upload(InputStream inputStream, String originalName, String contentType, String dir) {
        return upload(IoUtil.readBytes(inputStream), originalName, contentType, dir);
    }

    @Override
    public InputStream download(String key) {
        File file = new File(baseDir, key);
        if (!file.exists()) {
            throw new BusinessException("文件不存在: " + key);
        }
        return FileUtil.getInputStream(file);
    }

    @Override
    public void delete(String key) {
        FileUtil.del(new File(baseDir, key));
    }

    @Override
    public boolean exists(String key) {
        return new File(baseDir, key).exists();
    }

    @Override
    public String url(String key) {
        String prefix = config.getUrlPrefix() == null ? "" : config.getUrlPrefix();
        return prefix + (prefix.endsWith("/") ? "" : "/") + key;
    }

    @Override
    public boolean testConnection() {
        try {
            String key = "test/connection-" + System.currentTimeMillis() + ".txt";
            byte[] data = new byte[1024];
            String testDir = "test";
            StorageResult result = upload(data, key, "text/plain", "test");
            boolean ok = exists(result.getKey());
            delete(result.getKey());
            return ok;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void close() {
        // 无需释放
    }
}
