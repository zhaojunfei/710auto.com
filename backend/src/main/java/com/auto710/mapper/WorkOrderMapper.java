package com.auto710.mapper;

import com.auto710.entity.WorkOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 维修工单 Mapper
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Mapper
public interface WorkOrderMapper extends BaseMapper<WorkOrder> {
}
