package com.auto710.service.impl;

import com.auto710.entity.PartInfo;
import com.auto710.mapper.PartInfoMapper;
import com.auto710.service.PartInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 配件耗材 Service 实现
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Service
public class PartInfoServiceImpl extends ServiceImpl<PartInfoMapper, PartInfo> implements PartInfoService {
}
