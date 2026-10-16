package com.auto710.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 后台/技师登录请求
 */
@Data
public class LoginRequest {

    @NotBlank(message = "请输入账号")
    private String username;

    @NotBlank(message = "请输入密码")
    private String password;

    /** 验证码（开启后必填） */
    private String captcha;

    private String captchaKey;
}
