package com.auto710.common.util;

import cn.hutool.core.util.StrUtil;

/**
 * 数据脱敏工具
 */
public final class DesensitizeUtil {

    private DesensitizeUtil() {
    }

    /**
     * 手机号脱敏 138****8888
     */
    public static String phone(String phone) {
        if (StrUtil.isBlank(phone)) {
            return phone;
        }
        String plain = EncryptUtil.decrypt(phone);
        if (plain.length() < 7) {
            return "***";
        }
        return plain.substring(0, 3) + "****" + plain.substring(plain.length() - 4);
    }

    /**
     * 车牌脱敏 黑A12345 -> 黑A***45
     */
    public static String plate(String plate) {
        if (StrUtil.isBlank(plate)) {
            return plate;
        }
        String plain = EncryptUtil.decrypt(plate);
        if (plain.length() < 4) {
            return "***";
        }
        return plain.substring(0, 2) + "***" + plain.substring(plain.length() - 2);
    }

    /**
     * VIN 脱敏
     */
    public static String vin(String vin) {
        if (StrUtil.isBlank(vin)) {
            return vin;
        }
        String plain = EncryptUtil.decrypt(vin);
        if (plain.length() < 6) {
            return "***";
        }
        return plain.substring(0, 3) + "****" + plain.substring(plain.length() - 3);
    }
}
