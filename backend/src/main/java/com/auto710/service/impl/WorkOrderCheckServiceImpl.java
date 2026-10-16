package com.auto710.service.impl;

import com.auto710.entity.WorkOrderCheck;
import com.auto710.mapper.WorkOrderCheckMapper;
import com.auto710.service.WorkOrderCheckService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 接车检查/报价 Service 实现
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Service
public class WorkOrderCheckServiceImpl extends ServiceImpl<WorkOrderCheckMapper, WorkOrderCheck> implements WorkOrderCheckService {
}
