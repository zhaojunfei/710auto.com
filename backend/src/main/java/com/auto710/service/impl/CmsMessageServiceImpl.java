package com.auto710.service.impl;

import com.auto710.entity.CmsMessage;
import com.auto710.mapper.CmsMessageMapper;
import com.auto710.service.CmsMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 在线留言 Service 实现
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Service
public class CmsMessageServiceImpl extends ServiceImpl<CmsMessageMapper, CmsMessage> implements CmsMessageService {
}
