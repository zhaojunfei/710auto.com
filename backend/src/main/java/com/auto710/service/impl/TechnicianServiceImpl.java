package com.auto710.service.impl;

import com.auto710.entity.Technician;
import com.auto710.mapper.TechnicianMapper;
import com.auto710.service.TechnicianService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 技师 Service 实现
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Service
public class TechnicianServiceImpl extends ServiceImpl<TechnicianMapper, Technician> implements TechnicianService {

    @org.springframework.beans.factory.annotation.Autowired
    private com.auto710.service.SysUserService sysUserService;

    @Override
    public boolean save(Technician entity) {
        bindSysUser(entity);
        return super.save(entity);
    }

    @Override
    public boolean updateById(Technician entity) {
        bindSysUser(entity);
        return super.updateById(entity);
    }

    /**
     * 关联系统用户：<b>只绑定，不自动创建。</b>
     * <p>规则：
     * <ol>
     *   <li>表单已选择「关联系统用户」时，直接使用所选账号；</li>
     *   <li>未选择但填写了手机号时，尝试匹配<b>已存在</b>的系统账号（用户名或手机号 = 该手机号）并绑定；</li>
     *   <li>匹配不到则保持未绑定，由管理员先到「系统管理 - 用户」创建账号，再回到本表单选择关联。</li>
     * </ol>
     * 本方法不会向 sys_user / sys_user_role 写入任何数据。</p>
     */
    private void bindSysUser(Technician entity) {
        if (entity.getUserId() != null || cn.hutool.core.util.StrUtil.isBlank(entity.getPhone())) {
            return;
        }
        String phone = entity.getPhone().trim();
        com.auto710.entity.SysUser user = sysUserService.getOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.auto710.entity.SysUser>()
                        .and(w -> w.eq(com.auto710.entity.SysUser::getUsername, phone)
                                .or().eq(com.auto710.entity.SysUser::getPhone, phone))
                        .last("limit 1"));
        if (user != null) {
            entity.setUserId(user.getId());
        }
    }
}
