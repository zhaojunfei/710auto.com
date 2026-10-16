package com.auto710.common.constant;

/**
 * 存胎寄存状态常量
 */
public final class TireStorageStatus {

    private TireStorageStatus() {
    }

    public static final String STORED = "已寄存";
    public static final String STORING = "寄存中";
    public static final String WAIT_PICK = "待取胎";
    public static final String PICKED = "已取胎";
    public static final String OVERDUE = "超期";
}
