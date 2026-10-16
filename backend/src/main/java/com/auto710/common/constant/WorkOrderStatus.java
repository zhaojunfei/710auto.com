package com.auto710.common.constant;

/**
 * 工单状态常量（状态机）
 * <p>待接车 → 待派工 → 施工中 → 待质检 → 待结算 → 已离店 → 已评价</p>
 */
public final class WorkOrderStatus {

    private WorkOrderStatus() {
    }

    public static final String WAIT_RECEIVE = "待接车";
    public static final String WAIT_DISPATCH = "待派工";
    public static final String WORKING = "施工中";
    public static final String WAIT_CHECK = "待质检";
    public static final String WAIT_PAY = "待结算";
    public static final String DELIVERED = "已离店";
    public static final String EVALUATED = "已评价";
    public static final String CANCELLED = "已取消";

    /** 看板列顺序 */
    public static final String[] KANBAN = {WAIT_RECEIVE, WAIT_DISPATCH, WORKING, WAIT_CHECK, WAIT_PAY, DELIVERED, EVALUATED};

    /**
     * 校验状态流转是否合法
     */
    public static boolean canTransfer(String from, String to) {
        if (from == null) {
            return WAIT_RECEIVE.equals(to);
        }
        return switch (from) {
            case WAIT_RECEIVE -> WAIT_DISPATCH.equals(to) || CANCELLED.equals(to);
            case WAIT_DISPATCH -> WORKING.equals(to) || WAIT_RECEIVE.equals(to);
            case WORKING -> WAIT_CHECK.equals(to) || WAIT_DISPATCH.equals(to);
            case WAIT_CHECK -> WAIT_PAY.equals(to) || WORKING.equals(to);
            case WAIT_PAY -> DELIVERED.equals(to);
            case DELIVERED -> EVALUATED.equals(to);
            default -> false;
        };
    }
}
