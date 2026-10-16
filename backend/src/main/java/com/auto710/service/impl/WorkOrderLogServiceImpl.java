package com.auto710.service.impl;

import com.auto710.entity.WorkOrderLog;
import com.auto710.mapper.WorkOrderLogMapper;
import com.auto710.service.WorkOrderLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 工单状态流转日志 Service 实现
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Service
public class WorkOrderLogServiceImpl extends ServiceImpl<WorkOrderLogMapper, WorkOrderLog> implements WorkOrderLogService {
}
