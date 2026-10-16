package com.auto710.service.impl;

import com.auto710.entity.ServiceCategory;
import com.auto710.mapper.ServiceCategoryMapper;
import com.auto710.service.ServiceCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 服务分类 Service 实现
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Service
public class ServiceCategoryServiceImpl extends ServiceImpl<ServiceCategoryMapper, ServiceCategory> implements ServiceCategoryService {
}
