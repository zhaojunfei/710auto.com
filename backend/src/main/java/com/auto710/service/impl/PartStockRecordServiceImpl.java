package com.auto710.service.impl;

import com.auto710.entity.PartStockRecord;
import com.auto710.mapper.PartStockRecordMapper;
import com.auto710.service.PartStockRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 配件出入库记录 Service 实现
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Service
public class PartStockRecordServiceImpl extends ServiceImpl<PartStockRecordMapper, PartStockRecord> implements PartStockRecordService {
}
