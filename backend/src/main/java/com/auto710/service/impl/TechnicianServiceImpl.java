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
    @org.springframework.beans.factory.annotation.Autowired
    private com.auto710.service.SysRoleService sysRoleService;
    @org.springframework.beans.factory.annotation.Autowired
    private com.auto710.service.SysUserRoleService sysUserRoleService;

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
     * 添加 / 修改技师时，若未指定「关联系统用户」，则按手机号自动创建并绑定技师登录账号。
     * <p>绑定规则：用户名 = 手机号，初始密码 = 手机号后 6 位，角色 = TECHNICIAN，所属门店 = 技师门店；
     * 若手机号已存在同名系统账号则直接绑定，不重复创建。</p>
     */
    private void bindSysUser(Technician entity) {
        if (entity.getUserId() != null || cn.hutool.core.util.StrUtil.isBlank(entity.getPhone())) {
            return;
        }
        String phone = entity.getPhone().trim();
        com.auto710.entity.SysUser user = sysUserService.getOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.auto710.entity.SysUser>()
                        .eq(com.auto710.entity.SysUser::getUsername, phone).last("limit 1"));
        if (user == null) {
            user = new com.auto710.entity.SysUser();
            user.setUsername(phone);
            user.setRealName(entity.getTechName());
            user.setPhone(phone);
            user.setStoreId(entity.getStoreId());
            user.setStatus(1);
            user.setRemark("技师「" + entity.getTechName() + "」自动创建");
            user.setPassword(last6(phone));
            sysUserService.save(user);
            com.auto710.entity.SysRole role = sysRoleService.getOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.auto710.entity.SysRole>()
                            .eq(com.auto710.entity.SysRole::getRoleCode, "TECHNICIAN").last("limit 1"));
            if (role != null) {
                com.auto710.entity.SysUserRole ur = new com.auto710.entity.SysUserRole();
                ur.setUserId(user.getId());
                ur.setRoleId(role.getId());
                sysUserRoleService.save(ur);
            }
        }
        entity.setUserId(user.getId());
    }

    private String last6(String phone) {
        return phone.length() <= 6 ? phone : phone.substring(phone.length() - 6);
    }
}
