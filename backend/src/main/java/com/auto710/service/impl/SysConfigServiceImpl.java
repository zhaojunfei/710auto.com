package com.auto710.service.impl;

import com.auto710.entity.SysConfig;
import com.auto710.mapper.SysConfigMapper;
import com.auto710.service.SysConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 系统全局配置 Service 实现
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {
}
