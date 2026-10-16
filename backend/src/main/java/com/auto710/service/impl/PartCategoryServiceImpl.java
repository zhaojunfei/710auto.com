package com.auto710.service.impl;

import com.auto710.entity.PartCategory;
import com.auto710.mapper.PartCategoryMapper;
import com.auto710.service.PartCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 配件分类 Service 实现
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Service
public class PartCategoryServiceImpl extends ServiceImpl<PartCategoryMapper, PartCategory> implements PartCategoryService {
}
