package com.auto710.storage;

import java.io.InputStream;

/**
 * 对象存储驱动统一接口
 * <p>支持：本地磁盘 / AWS S3 / 阿里云 OSS / 腾讯云 COS / MinIO，业务层统一调用，后台可视化切换。</p>
 */
public interface StorageDriver {

    /** 驱动类型 */
    String type();

    /** 驱动名称 */
    String name();

    /** 初始化驱动 */
    void init(StorageConfig config);

    /** 上传字节数组 */
    StorageResult upload(byte[] bytes, String originalName, String contentType, String dir);

    /** 上传流 */
    StorageResult upload(InputStream inputStream, String originalName, String contentType, String dir);

    /** 下载 */
    InputStream download(String key);

    /** 删除 */
    void delete(String key);

    /** 判断是否存在 */
    boolean exists(String key);

    /** 生成访问地址 */
    String url(String key);

    /** 连接测试：写入并读取 1KB 测试文件 */
    boolean testConnection();

    /** 关闭资源 */
    void close();
}
