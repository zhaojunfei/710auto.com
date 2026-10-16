package com.auto710.common.util;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 敏感数据加密工具（手机号 / 车牌 / VIN 使用 AES-128/ECB/PKCS5Padding）
 * <p>合规要求：车辆隐私、用户隐私字段加密存储。</p>
 */
@Component
public class EncryptUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static String aesKey;

    @Value("${auto710.security.aes-key}")
    public void setAesKey(String key) {
        aesKey = key != null && key.length() >= 16 ? key.substring(0, 16) : "710autoAesKey16!";
    }

    /**
     * 加密
     */
    public static String encrypt(String plain) {
        if (StrUtil.isBlank(plain)) {
            return plain;
        }
        try {
            SecretKeySpec keySpec = new SecretKeySpec(aesKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] bytes = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            throw new IllegalStateException("加密失败", e);
        }
    }

    /**
     * 解密
     */
    public static String decrypt(String cipherText) {
        if (StrUtil.isBlank(cipherText)) {
            return cipherText;
        }
        try {
            SecretKeySpec keySpec = new SecretKeySpec(aesKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // 历史明文数据直接返回
            return cipherText;
        }
    }
}
