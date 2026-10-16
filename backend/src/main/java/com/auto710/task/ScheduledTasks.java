package com.auto710.task;

import com.auto710.biz.AppointmentBizService;
import com.auto710.biz.CouponBizService;
import com.auto710.biz.StatsBizService;
import com.auto710.biz.TireStorageBizService;
import com.auto710.biz.InventoryBizService;
import com.auto710.entity.Appointment;
import com.auto710.entity.PartInfo;
import com.auto710.service.AppointmentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务：存胎换季提醒、超期扫描、优惠券过期、预约超时释放、每日经营日报、低库存预警
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final TireStorageBizService tireStorageBizService;
    private final CouponBizService couponBizService;
    private final AppointmentBizService appointmentBizService;
    private final AppointmentService appointmentService;
    private final StatsBizService statsBizService;
    private final InventoryBizService inventoryBizService;

    /**
     * 每日 08:00 存胎换季到期提醒（提前 7 天）
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void tireRemind() {
        int count = tireStorageBizService.remindBatch(7);
        log.info("[定时任务] 存胎到期提醒已推送 {} 条", count);
    }

    /**
     * 每日 00:30 存胎超期扫描与超期保管费结算
     */
    @Scheduled(cron = "0 30 0 * * ?")
    public void tireOverdue() {
        int count = tireStorageBizService.scanOverdue();
        log.info("[定时任务] 存胎超期扫描 {} 条", count);
    }

    /**
     * 每小时优惠券过期扫描
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void couponExpire() {
        int count = couponBizService.expireScan();
        log.info("[定时任务] 优惠券过期处理 {} 张", count);
    }

    /**
     * 每 10 分钟释放超时未到店预约
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void releaseTimeoutAppointment() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(2);
        List<Appointment> list = appointmentService.list(new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getStatus, "待确认")
                .lt(Appointment::getAppointTime, threshold));
        list.forEach(a -> appointmentBizService.cancel(a.getId(), "超时未到店自动释放"));
        if (!list.isEmpty()) {
            log.info("[定时任务] 释放超时预约 {} 条", list.size());
        }
    }

    /**
     * 每日 23:30 生成经营日报
     */
    @Scheduled(cron = "0 30 23 * * ?")
    public void dailyReport() {
        statsBizService.generateDaily(LocalDate.now().toString());
        log.info("[定时任务] 每日经营统计已生成");
    }

    /**
     * 每日 09:00 低库存预警
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void stockWarning() {
        List<PartInfo> warnings = inventoryBizService.warningList();
        if (!warnings.isEmpty()) {
            log.warn("[定时任务] 低库存预警 {} 项", warnings.size());
        }
    }
}
