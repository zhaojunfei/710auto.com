package com.auto710.common.util;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.date.DateUtil;

import java.util.Date;

/**
 * 单号生成工具
 */
public final class IdGenUtil {

    private IdGenUtil() {
    }

    public static String gen(String prefix) {
        return prefix + DateUtil.format(new Date(), "yyyyMMddHHmmss") + RandomUtil.randomNumbers(6);
    }

    /** 预约单号 */
    public static String appointmentNo() {
        return gen("YY");
    }

    /** 工单号 */
    public static String workOrderNo() {
        return gen("GD");
    }

    /** 存胎寄存单号 */
    public static String tireStorageNo() {
        return gen("CT");
    }

    /** 退款单号 */
    public static String refundNo() {
        return gen("TK");
    }
}
