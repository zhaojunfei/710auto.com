package com.auto710.storage;

import lombok.Data;

/**
 * 文件上传结果
 */
@Data
public class StorageResult {

    /** 对象 Key */
    private String key;

    /** 访问地址 */
    private String url;

    /** 原始文件名 */
    private String originalName;

    /** 文件大小 */
    private long size;

    /** 驱动类型 */
    private String driver;

    public static StorageResult of(String key, String url, String originalName, long size, String driver) {
        StorageResult r = new StorageResult();
        r.setKey(key);
        r.setUrl(url);
        r.setOriginalName(originalName);
        r.setSize(size);
        r.setDriver(driver);
        return r;
    }
}
