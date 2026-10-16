package com.auto710.common.constant;

/**
 * 预约状态常量
 */
public final class AppointmentStatus {

    private AppointmentStatus() {
    }

    public static final String WAIT_CONFIRM = "待确认";
    public static final String CONFIRMED = "已确认";
    public static final String WAIT_RECEIVE = "待接车";
    public static final String FINISHED = "已完成";
    public static final String CANCELLED = "已取消";
}
