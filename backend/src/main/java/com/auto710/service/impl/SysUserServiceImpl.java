package com.auto710.service.impl;

import com.auto710.entity.SysUser;
import com.auto710.mapper.SysUserMapper;
import com.auto710.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 系统用户 Service 实现
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public boolean save(SysUser entity) {
        encodeInsert(entity);
        return super.save(entity);
    }

    @Override
    public boolean updateById(SysUser entity) {
        encodeUpdate(entity);
        return super.updateById(entity);
    }

    /** 新增：默认密码 123456，明文自动 BCrypt */
    private void encodeInsert(SysUser entity) {
        String pwd = entity.getPassword();
        entity.setPassword(pwd == null || pwd.isBlank()
                ? cn.hutool.crypto.digest.BCrypt.hashpw("123456")
                : encode(pwd));
    }

    /** 修改：密码留空则不修改 */
    private void encodeUpdate(SysUser entity) {
        String pwd = entity.getPassword();
        entity.setPassword(pwd == null || pwd.isBlank() ? null : encode(pwd));
    }

    private String encode(String pwd) {
        return pwd.startsWith("$2a$") || pwd.startsWith("$2b$")
                ? pwd : cn.hutool.crypto.digest.BCrypt.hashpw(pwd);
    }
}
