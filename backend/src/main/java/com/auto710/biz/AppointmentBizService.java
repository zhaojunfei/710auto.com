package com.auto710.biz;

import com.auto710.common.api.ResultCode;
import com.auto710.common.constant.AppointmentStatus;
import com.auto710.common.constant.WorkOrderStatus;
import com.auto710.common.exception.BusinessException;
import com.auto710.common.util.EncryptUtil;
import com.auto710.common.util.IdGenUtil;
import com.auto710.entity.Appointment;
import com.auto710.entity.WorkOrder;
import com.auto710.entity.WorkOrderLog;
import com.auto710.msg.MessageService;
import com.auto710.service.AppointmentService;
import com.auto710.service.WorkOrderLogService;
import com.auto710.service.WorkOrderService;
import com.auto710.service.WorkStationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 预约业务服务：线上预约 → 确认 → 到店接车 → 转工单；含工位产能过载防护、超时释放
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentBizService {

    private final AppointmentService appointmentService;
    private final WorkStationService workStationService;
    private final WorkOrderService workOrderService;
    private final WorkOrderLogService workOrderLogService;
    private final MessageService messageService;

    /**
     * 创建预约（防重提交由注解在 Controller 层控制）
     */
    @Transactional(rollbackFor = Exception.class)
    public Appointment create(Appointment appointment) {
        if (appointment.getAppointTime() == null) {
            throw new BusinessException("请选择到店时间");
        }
        assertCapacity(appointment.getStoreId(), appointment.getAppointTime());

        appointment.setAppointmentNo(IdGenUtil.appointmentNo());
        appointment.setStatus(AppointmentStatus.WAIT_CONFIRM);
        // 车牌 / 手机号加密由 AppointmentServiceImpl 写入钩子统一处理（含后台维护路径）
        appointmentService.save(appointment);

        Map<String, String> data = new LinkedHashMap<>();
        data.put("appointmentNo", appointment.getAppointmentNo());
        data.put("itemName", appointment.getItemName());
        data.put("appointTime", String.valueOf(appointment.getAppointTime()));
        messageService.sendTemplate(appointment.getMemberId(), MessageService.MSG_TYPE_APPOINT_CONFIRM, data);

        messageService.pushOrderTimeout(appointment.getId());
        return appointment;
    }

    /**
     * 确认预约
     */
    public boolean confirm(Long id) {
        Appointment appointment = require(id);
        if (!AppointmentStatus.WAIT_CONFIRM.equals(appointment.getStatus())) {
            throw new BusinessException("当前状态不可确认");
        }
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setConfirmTime(LocalDateTime.now());
        // 不能用 Map.of：预约单号等字段可能为空，Map.of 遇 null 会抛 NPE
        Map<String, String> data = new LinkedHashMap<>();
        data.put("appointmentNo", appointment.getAppointmentNo());
        data.put("itemName", appointment.getItemName());
        data.put("status", AppointmentStatus.CONFIRMED);
        messageService.sendTemplate(appointment.getMemberId(), MessageService.MSG_TYPE_APPOINT_CONFIRM, data);
        return appointmentService.updateById(appointment);
    }

    /**
     * 取消预约
     */
    public boolean cancel(Long id, String reason) {
        Appointment appointment = require(id);
        if (AppointmentStatus.FINISHED.equals(appointment.getStatus())) {
            throw new BusinessException("已完成预约不可取消");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancelReason(reason);
        return appointmentService.updateById(appointment);
    }

    /**
     * 到店接车：预约转工单
     */
    @Transactional(rollbackFor = Exception.class)
    public WorkOrder receive(Long appointmentId, WorkOrder workOrder) {
        Appointment appointment = require(appointmentId);
        appointment.setStatus(AppointmentStatus.WAIT_RECEIVE);
        appointmentService.updateById(appointment);

        workOrder.setAppointmentId(appointmentId);
        workOrder.setMemberId(appointment.getMemberId());
        workOrder.setStoreId(appointment.getStoreId());
        workOrder.setPlateNo(appointment.getPlateNo());
        workOrder.setContactName(appointment.getContactName());
        workOrder.setContactPhone(appointment.getContactPhone());
        workOrder.setOrderNo(IdGenUtil.workOrderNo());
        workOrder.setStatus(WorkOrderStatus.WAIT_RECEIVE);
        workOrder.setReceiveTime(LocalDateTime.now());
        workOrderService.save(workOrder);

        WorkOrderLog orderLog = new WorkOrderLog();
        orderLog.setWorkOrderId(workOrder.getId());
        orderLog.setToStatus(WorkOrderStatus.WAIT_RECEIVE);
        orderLog.setOperatorName("前台接车");
        orderLog.setRemark("预约到店接车，生成工单");
        workOrderLogService.save(orderLog);
        return workOrder;
    }

    /**
     * 工位产能过载防护：同一日期预约数不得超过工位同时开工上限总和
     */
    public void assertCapacity(Long storeId, LocalDateTime appointTime) {
        int capacity = workStationService.list(new LambdaQueryWrapper<com.auto710.entity.WorkStation>()
                        .eq(storeId != null, com.auto710.entity.WorkStation::getStoreId, storeId)
                        .eq(com.auto710.entity.WorkStation::getStatus, 1))
                .stream().mapToInt(w -> w.getCapacity() == null ? 1 : w.getCapacity()).sum();
        if (capacity <= 0) {
            capacity = 4;
        }
        LocalDateTime dayStart = LocalDateTime.of(appointTime.toLocalDate(), LocalTime.MIN);
        LocalDateTime dayEnd = LocalDateTime.of(appointTime.toLocalDate(), LocalTime.MAX);
        Long count = appointmentService.count(new LambdaQueryWrapper<Appointment>()
                .eq(storeId != null, Appointment::getStoreId, storeId)
                .between(Appointment::getAppointTime, dayStart, dayEnd)
                .notIn(Appointment::getStatus, List.of(AppointmentStatus.CANCELLED, AppointmentStatus.FINISHED)));
        if (count != null && count >= capacity) {
            throw new BusinessException(ResultCode.CAPACITY_FULL);
        }
    }

    /**
     * 当日可预约余量
     */
    public int remainCapacity(Long storeId, LocalDate date) {
        int capacity = workStationService.list(new LambdaQueryWrapper<com.auto710.entity.WorkStation>()
                        .eq(storeId != null, com.auto710.entity.WorkStation::getStoreId, storeId)
                        .eq(com.auto710.entity.WorkStation::getStatus, 1))
                .stream().mapToInt(w -> w.getCapacity() == null ? 1 : w.getCapacity()).sum();
        if (capacity <= 0) {
            capacity = 4;
        }
        Long count = appointmentService.count(new LambdaQueryWrapper<Appointment>()
                .between(Appointment::getAppointTime,
                        LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX))
                .notIn(Appointment::getStatus, List.of(AppointmentStatus.CANCELLED, AppointmentStatus.FINISHED)));
        return Math.max(0, capacity - (count == null ? 0 : count.intValue()));
    }

    private Appointment require(Long id) {
        Appointment appointment = appointmentService.getById(id);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }
        return appointment;
    }
}
