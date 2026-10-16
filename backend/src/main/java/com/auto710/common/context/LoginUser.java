package com.auto710.common.context;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 登录用户信息（后台用户 / 技师 / 会员统一载体）
 */
@Data
public class LoginUser implements Serializable {

    /** 用户ID（后台为 sys_user.id，会员为 member.id） */
    private Long userId;
    /** 登录账号 */
    private String username;
    /** 姓名 */
    private String realName;
    /** 用户类型 ADMIN / TECHNICIAN / MEMBER */
    private String userType;
    /** 角色编码集合 */
    private List<String> roles;
    /** 权限标识集合 */
    private List<String> permissions;
    /** 所属门店 */
    private Long storeId;
    /** 是否超级管理员 */
    private boolean superAdmin;

    public boolean hasRole(String roleCode) {
        return roles != null && roles.contains(roleCode);
    }
}
