package com.auto710.dto;

import lombok.Data;

/**
 * 微信登录请求（公众号 / 小程序 / H5）
 */
@Data
public class WxLoginRequest {

    /** 微信登录 code（真实环境换取 openid） */
    private String code;

    private String openid;

    private String unionid;

    private String nickname;

    private String avatar;

    /** 来源 mp / miniapp / h5 */
    private String source;
}
