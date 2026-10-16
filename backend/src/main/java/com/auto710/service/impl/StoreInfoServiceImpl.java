package com.auto710.service.impl;

import com.auto710.entity.StoreInfo;
import com.auto710.mapper.StoreInfoMapper;
import com.auto710.service.StoreInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 门店信息 Service 实现
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Service
public class StoreInfoServiceImpl extends ServiceImpl<StoreInfoMapper, StoreInfo> implements StoreInfoService {
}
