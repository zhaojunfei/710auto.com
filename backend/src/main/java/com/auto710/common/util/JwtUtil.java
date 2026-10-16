package com.auto710.common.util;

import com.auto710.common.context.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * JWT 工具
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${auto710.jwt.secret}")
    private String secret;

    @Value("${auto710.jwt.expire-minutes:720}")
    private long expireMinutes;

    @Value("${auto710.jwt.member-expire-minutes:43200}")
    private long memberExpireMinutes;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成后台/技师令牌
     */
    public String createToken(LoginUser user) {
        return create(user, expireMinutes);
    }

    /**
     * 生成会员令牌
     */
    public String createMemberToken(LoginUser user) {
        return create(user, memberExpireMinutes);
    }

    private String create(LoginUser user, long minutes) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + minutes * 60_000L);
        return Jwts.builder()
                .subject(String.valueOf(user.getUserId()))
                .claim("username", user.getUsername())
                .claim("realName", user.getRealName())
                .claim("userType", user.getUserType())
                .claim("roles", user.getRoles())
                .claim("storeId", user.getStoreId())
                .claim("superAdmin", user.isSuperAdmin())
                .issuedAt(now)
                .expiration(exp)
                .signWith(key())
                .compact();
    }

    @SuppressWarnings("unchecked")
    public LoginUser parse(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            LoginUser user = new LoginUser();
            user.setUserId(Long.valueOf(claims.getSubject()));
            user.setUsername(claims.get("username", String.class));
            user.setRealName(claims.get("realName", String.class));
            user.setUserType(claims.get("userType", String.class));
            user.setRoles((List<String>) claims.get("roles", List.class));
            Object storeId = claims.get("storeId");
            if (storeId != null) {
                user.setStoreId(Long.valueOf(String.valueOf(storeId)));
            }
            Boolean superAdmin = claims.get("superAdmin", Boolean.class);
            user.setSuperAdmin(superAdmin != null && superAdmin);
            return user;
        } catch (Exception e) {
            log.debug("JWT 解析失败: {}", e.getMessage());
            return null;
        }
    }
}
