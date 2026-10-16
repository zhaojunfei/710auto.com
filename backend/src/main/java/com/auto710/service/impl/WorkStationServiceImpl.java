package com.auto710.service.impl;

import com.auto710.entity.WorkStation;
import com.auto710.mapper.WorkStationMapper;
import com.auto710.service.WorkStationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 工位 Service 实现
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Service
public class WorkStationServiceImpl extends ServiceImpl<WorkStationMapper, WorkStation> implements WorkStationService {
}
