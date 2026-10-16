package com.auto710.mapper;

import com.auto710.entity.Member;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员 Mapper
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Mapper
public interface MemberMapper extends BaseMapper<Member> {
}
