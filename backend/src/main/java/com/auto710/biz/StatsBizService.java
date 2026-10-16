package com.auto710.biz;

import com.auto710.common.constant.AppointmentStatus;
import com.auto710.entity.*;
import com.auto710.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据统计业务服务：UV/PV、预约量、工单量、营收、毛利、存胎量、会员储值
 */
@Service
@RequiredArgsConstructor
public class StatsBizService {

    private final StatDailyService statDailyService;
    private final WorkOrderService workOrderService;
    private final AppointmentService appointmentService;
    private final MemberService memberService;
    private final TireStorageOrderService tireStorageOrderService;
    private final MemberBalanceLogService balanceLogService;
    private final WorkOrderEvaluateService evaluateService;
    private final ServiceItemService serviceItemService;

    /**
     * 首页看板概览
     */
    public Map<String, Object> overview() {
        LocalDateTime dayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime dayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("appointmentCount", appointmentService.count(new LambdaQueryWrapper<Appointment>()
                .between(Appointment::getCreateTime, dayStart, dayEnd)));
        List<WorkOrder> todayOrders = workOrderService.list(new LambdaQueryWrapper<WorkOrder>()
                .between(WorkOrder::getCreateTime, dayStart, dayEnd));
        data.put("orderCount", todayOrders.size());
        BigDecimal revenue = todayOrders.stream()
                .map(o -> o.getPaidAmount() == null ? BigDecimal.ZERO : o.getPaidAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        data.put("revenue", revenue);
        data.put("memberCount", memberService.count(new LambdaQueryWrapper<Member>()
                .between(Member::getCreateTime, dayStart, dayEnd)));
        data.put("tireCount", tireStorageOrderService.count(new LambdaQueryWrapper<TireStorageOrder>()
                .between(TireStorageOrder::getCreateTime, dayStart, dayEnd)));

        // 待处理工单
        data.put("pendingCount", workOrderService.count(new LambdaQueryWrapper<WorkOrder>()
                .in(WorkOrder::getStatus, List.of("待接车", "待派工", "施工中", "待质检", "待结算"))));

        // 平均评分
        List<WorkOrderEvaluate> evaluates = evaluateService.list();
        double avgScore = evaluates.stream()
                .filter(e -> e.getScore() != null)
                .mapToInt(WorkOrderEvaluate::getScore)
                .average().orElse(5.0);
        data.put("avgScore", Math.round(avgScore * 10) / 10.0);
        return data;
    }

    /**
     * 营收 / 服务项目排行 / 技师产值 / 客户留存
     */
    public Map<String, Object> dashboard(int days) {
        LocalDateTime begin = LocalDateTime.now().minusDays(days);
        List<WorkOrder> orders = workOrderService.list(new LambdaQueryWrapper<WorkOrder>()
                .ge(WorkOrder::getCreateTime, begin));

        Map<String, Object> data = new LinkedHashMap<>();
        BigDecimal revenue = orders.stream()
                .map(o -> o.getPaidAmount() == null ? BigDecimal.ZERO : o.getPaidAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        data.put("revenue", revenue);
        data.put("orderCount", orders.size());
        data.put("avgOrderAmount", orders.isEmpty() ? BigDecimal.ZERO
                : revenue.divide(BigDecimal.valueOf(orders.size()), 2, java.math.RoundingMode.HALF_UP));

        // 各服务项目营收
        Map<String, BigDecimal> itemRevenue = new LinkedHashMap<>();
        for (WorkOrder order : orders) {
            BigDecimal amount = order.getTotalAmount() == null ? BigDecimal.ZERO : order.getTotalAmount();
            itemRevenue.merge(order.getPlateNo() == null ? "其他" : order.getServiceItems(), amount, BigDecimal::add);
        }
        data.put("itemRevenue", itemRevenue);

        // 技师产值
        Map<Long, BigDecimal> techRevenue = new LinkedHashMap<>();
        for (WorkOrder order : orders) {
            if (order.getTechnicianId() != null) {
                techRevenue.merge(order.getTechnicianId(),
                        order.getTotalAmount() == null ? BigDecimal.ZERO : order.getTotalAmount(),
                        BigDecimal::add);
            }
        }
        data.put("techRevenue", techRevenue);

        // 客户留存：复购会员占比
        long totalMembers = memberService.count();
        long repeatMembers = orders.stream().map(WorkOrder::getMemberId).distinct().count();
        data.put("retention", totalMembers == 0 ? 0 : Math.round(repeatMembers * 100.0 / totalMembers));

        data.put("serviceItemCount", serviceItemService.count());
        return data;
    }

    /**
     * 生成每日统计（定时任务调用）
     */
    public StatDaily generateDaily(String date) {
        StatDaily existing = statDailyService.getOne(new LambdaQueryWrapper<StatDaily>()
                .eq(StatDaily::getStatDate, date).last("limit 1"));
        StatDaily stat = existing == null ? new StatDaily() : existing;
        stat.setStatDate(date);

        LocalDate day = LocalDate.parse(date);
        LocalDateTime dayStart = LocalDateTime.of(day, LocalTime.MIN);
        LocalDateTime dayEnd = LocalDateTime.of(day, LocalTime.MAX);

        List<WorkOrder> orders = workOrderService.list(new LambdaQueryWrapper<WorkOrder>()
                .between(WorkOrder::getCreateTime, dayStart, dayEnd));
        stat.setOrderCount(orders.size());
        stat.setRevenue(orders.stream()
                .map(o -> o.getPaidAmount() == null ? BigDecimal.ZERO : o.getPaidAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        stat.setAppointmentCount((int) appointmentService.count(new LambdaQueryWrapper<Appointment>()
                .between(Appointment::getCreateTime, dayStart, dayEnd)));
        stat.setMemberCount((int) memberService.count(new LambdaQueryWrapper<Member>()
                .between(Member::getCreateTime, dayStart, dayEnd)));
        stat.setTireCount((int) tireStorageOrderService.count(new LambdaQueryWrapper<TireStorageOrder>()
                .between(TireStorageOrder::getCreateTime, dayStart, dayEnd)));
        stat.setRechargeAmount(balanceLogService.list(new LambdaQueryWrapper<MemberBalanceLog>()
                        .eq(MemberBalanceLog::getType, "充值")
                        .between(MemberBalanceLog::getCreateTime, dayStart, dayEnd))
                .stream().map(l -> l.getChangeAmount() == null ? BigDecimal.ZERO : l.getChangeAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        stat.setUv(0);
        stat.setPv(0);
        stat.setGrossProfit(BigDecimal.ZERO);

        statDailyService.saveOrUpdate(stat);
        return stat;
    }
}
