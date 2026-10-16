package com.auto710.mapper;

import com.auto710.entity.WorkOrderLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工单状态流转日志 Mapper
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Mapper
public interface WorkOrderLogMapper extends BaseMapper<WorkOrderLog> {
}
