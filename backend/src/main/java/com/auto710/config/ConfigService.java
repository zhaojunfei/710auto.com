package com.auto710.config;

import cn.hutool.core.util.StrUtil;
import com.auto710.common.util.EncryptUtil;
import com.auto710.entity.SysConfig;
import com.auto710.service.SysConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统全局参数服务：KV 结构 + 内存缓存 + 密钥加密存储
 * <p>站点名称、Logo、ICP、客服电话、营业时间、SEO、公众号、小程序、微信支付等均在此统一读取，
 * 后台修改后实时生效，无需改代码、无需重启。</p>
 */
@Service
@RequiredArgsConstructor
public class ConfigService {

    private final SysConfigService configService;
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    // ============ 常用键 ============
    public static final String SITE_NAME = "site.name";
    public static final String SITE_LOGO = "site.logo";
    public static final String SITE_FAVICON = "site.favicon";
    public static final String ICP_NO = "site.icp";
    public static final String ICP_OWNER = "site.icp.owner";
    public static final String SERVICE_PHONE = "site.service.phone";
    public static final String BUSINESS_HOURS = "site.business.hours";
    public static final String BRAND_INTRO = "site.brand.intro";
    public static final String SEO_KEYWORDS = "site.seo.keywords";
    public static final String SEO_DESCRIPTION = "site.seo.description";
    public static final String WECHAT_MP_APPID = "wechat.mp.appid";
    public static final String WECHAT_MP_SECRET = "wechat.mp.secret";
    public static final String WECHAT_MA_APPID = "wechat.ma.appid";
    public static final String WECHAT_MA_SECRET = "wechat.ma.secret";
    public static final String PAY_MCH_ID = "pay.mch.id";
    public static final String PAY_MCH_KEY = "pay.mch.key";
    public static final String SMS_ENABLED = "sms.enabled";

    public String get(String key) {
        return cache.computeIfAbsent(key, k -> {
            SysConfig config = configService.getOne(new LambdaQueryWrapper<SysConfig>()
                    .eq(SysConfig::getConfigKey, k).last("limit 1"));
            return config == null ? null : config.getConfigValue();
        });
    }

    public String get(String key, String defaultValue) {
        return StrUtil.blankToDefault(get(key), defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        String value = get(key);
        try {
            return value == null ? defaultValue : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public boolean getBool(String key, boolean defaultValue) {
        String value = get(key);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }

    /**
     * 读取并解密（用于支付/公众号密钥等）
     */
    public String getDecrypted(String key) {
        SysConfig config = configService.getOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, key).last("limit 1"));
        if (config == null) {
            return null;
        }
        return config.getEncrypted() != null && config.getEncrypted() == 1
                ? EncryptUtil.decrypt(config.getConfigValue())
                : config.getConfigValue();
    }

    /**
     * 按分组读取
     */
    public List<SysConfig> listByGroup(String group) {
        return configService.list(new LambdaQueryWrapper<SysConfig>()
                .eq(StrUtil.isNotBlank(group), SysConfig::getConfigGroup, group)
                .orderByAsc(SysConfig::getId));
    }

    /**
     * 分组名列表
     */
    public List<String> groups() {
        return configService.list().stream()
                .map(SysConfig::getConfigGroup)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .toList();
    }

    /**
     * 保存（自动加密 secret）
     */
    public void save(SysConfig config) {
        SysConfig exist = configService.getOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, config.getConfigKey()).last("limit 1"));
        Integer encrypted = config.getEncrypted() != null ? config.getEncrypted()
                : (exist != null && exist.getEncrypted() != null ? exist.getEncrypted() : 0);
        config.setEncrypted(encrypted);
        // 掩码值不覆盖
        boolean masked = config.getConfigValue() != null && config.getConfigValue().contains("****");
        if (masked && exist != null) {
            config.setConfigValue(exist.getConfigValue());
        } else if (encrypted == 1 && StrUtil.isNotBlank(config.getConfigValue())) {
            config.setConfigValue(EncryptUtil.encrypt(config.getConfigValue()));
        }
        if (exist != null) {
            config.setId(exist.getId());
        }
        configService.saveOrUpdate(config);
        cache.remove(config.getConfigKey());
    }

    public void saveBatch(List<SysConfig> configs) {
        configs.forEach(this::save);
    }

    public void saveMap(String group, Map<String, String> values) {
        values.forEach((key, value) -> {
            SysConfig config = new SysConfig();
            config.setConfigKey(key);
            config.setConfigValue(value);
            config.setConfigGroup(group);
            config.setConfigName(key);
            save(config);
        });
    }

    public void clearCache() {
        cache.clear();
    }

    /**
     * 前台站点公开配置
     */
    public Map<String, String> siteConfig() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("siteName", get(SITE_NAME, "710号汽车之家"));
        map.put("logo", get(SITE_LOGO, ""));
        map.put("favicon", get(SITE_FAVICON, ""));
        map.put("icp", get(ICP_NO, ""));
        map.put("icpOwner", get(ICP_OWNER, ""));
        map.put("servicePhone", get(SERVICE_PHONE, ""));
        map.put("businessHours", get(BUSINESS_HOURS, "08:30-18:30"));
        map.put("brandIntro", get(BRAND_INTRO, ""));
        map.put("seoKeywords", get(SEO_KEYWORDS, ""));
        map.put("seoDescription", get(SEO_DESCRIPTION, ""));
        return map;
    }
}
