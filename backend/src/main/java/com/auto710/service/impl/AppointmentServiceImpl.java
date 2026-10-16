package com.auto710.service.impl;

import com.auto710.entity.Appointment;
import com.auto710.mapper.AppointmentMapper;
import com.auto710.service.AppointmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 预约单 Service 实现
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements AppointmentService {

    @Override
    public boolean save(Appointment entity) {
        encode(entity, true);
        return super.save(entity);
    }

    @Override
    public boolean updateById(Appointment entity) {
        encode(entity, false);
        return super.updateById(entity);
    }

    /**
     * 新增补全单号/默认状态；车牌与手机号加密入库（已是密文则跳过，避免二次加密）。
     * 后台「预约管理」与会员端下单共用同一 Service，保证两条写入路径行为一致。
     */
    private void encode(Appointment entity, boolean insert) {
        if (insert && cn.hutool.core.util.StrUtil.isBlank(entity.getAppointmentNo())) {
            entity.setAppointmentNo(com.auto710.common.util.IdGenUtil.appointmentNo());
        }
        if (insert && cn.hutool.core.util.StrUtil.isBlank(entity.getStatus())) {
            entity.setStatus(com.auto710.common.constant.AppointmentStatus.WAIT_CONFIRM);
        }
        if (!com.auto710.common.util.EncryptUtil.isEncrypted(entity.getPlateNo())) {
            entity.setPlateNo(com.auto710.common.util.EncryptUtil.encrypt(entity.getPlateNo()));
        }
        if (!com.auto710.common.util.EncryptUtil.isEncrypted(entity.getContactPhone())) {
            entity.setContactPhone(com.auto710.common.util.EncryptUtil.encrypt(entity.getContactPhone()));
        }
    }
}
