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
}
