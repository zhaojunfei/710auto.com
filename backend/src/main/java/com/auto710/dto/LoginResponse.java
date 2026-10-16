package com.auto710.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 登录返回
 */
@Data
public class LoginResponse {

    private String token;
    private Long userId;
    private String username;
    private String realName;
    private String avatar;
    private String userType;
    private Long storeId;
    private boolean superAdmin;
    private List<String> roles;
    private List<String> permissions;
    private List<Map<String, Object>> menus;
}
