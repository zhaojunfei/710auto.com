package com.auto710.storage;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 存储 Key 生成工具
 */
public final class StorageKeyUtil {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private StorageKeyUtil() {
    }

    public static String buildKey(String originalName, String dir) {
        String ext = "";
        if (StrUtil.isNotBlank(originalName) && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
            ext = ext.replaceAll("[^a-zA-Z0-9.]", "");
        }
        String prefix = StrUtil.isBlank(dir) ? "" : StrUtil.strip(dir, "/") + "/";
        return prefix + LocalDate.now().format(DATE) + "/" + IdUtil.fastSimpleUUID() + ext;
    }
}
