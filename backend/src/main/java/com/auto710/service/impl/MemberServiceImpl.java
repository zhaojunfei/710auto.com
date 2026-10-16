package com.auto710.service.impl;

import com.auto710.entity.Member;
import com.auto710.mapper.MemberMapper;
import com.auto710.service.MemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 会员 Service 实现
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {
}
