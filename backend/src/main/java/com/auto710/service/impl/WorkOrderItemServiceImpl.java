package com.auto710.service.impl;

import com.auto710.entity.WorkOrderItem;
import com.auto710.mapper.WorkOrderItemMapper;
import com.auto710.service.WorkOrderItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 工单明细 Service 实现
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Service
public class WorkOrderItemServiceImpl extends ServiceImpl<WorkOrderItemMapper, WorkOrderItem> implements WorkOrderItemService {
}
