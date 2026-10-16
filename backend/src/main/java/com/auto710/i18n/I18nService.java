package com.auto710.i18n;

import com.auto710.entity.SysI18nContent;
import com.auto710.entity.SysLanguage;
import com.auto710.service.SysI18nContentService;
import com.auto710.service.SysLanguageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 多语言 i18n 服务：语言包加载、内容按语言取字段
 */
@Service
@RequiredArgsConstructor
public class I18nService {

    public static final String DEFAULT_LANG = "zh-CN";

    private final SysLanguageService languageService;
    private final SysI18nContentService contentService;

    /**
     * 启用中的语言
     */
    public List<SysLanguage> enabledLanguages() {
        return languageService.list(new LambdaQueryWrapper<SysLanguage>()
                .eq(SysLanguage::getStatus, 1)
                .orderByAsc(SysLanguage::getSort));
    }

    public String defaultLangCode() {
        SysLanguage language = languageService.getOne(new LambdaQueryWrapper<SysLanguage>()
                .eq(SysLanguage::getIsDefault, 1).last("limit 1"));
        return language == null ? DEFAULT_LANG : language.getLangCode();
    }

    /**
     * 取某业务某语言下的全部字段
     */
    public Map<String, String> getFields(String bizType, Long bizId, String langCode) {
        List<SysI18nContent> list = contentService.list(new LambdaQueryWrapper<SysI18nContent>()
                .eq(SysI18nContent::getBizType, bizType)
                .eq(SysI18nContent::getBizId, bizId)
                .eq(SysI18nContent::getLangCode, langCode));
        Map<String, String> map = new LinkedHashMap<>();
        list.forEach(c -> map.put(c.getFieldKey(), c.getFieldValue()));
        return map;
    }

    /**
     * 取单个字段
     */
    public String get(String bizType, Long bizId, String langCode, String fieldKey) {
        SysI18nContent content = contentService.getOne(new LambdaQueryWrapper<SysI18nContent>()
                .eq(SysI18nContent::getBizType, bizType)
                .eq(SysI18nContent::getBizId, bizId)
                .eq(SysI18nContent::getLangCode, langCode)
                .eq(SysI18nContent::getFieldKey, fieldKey)
                .last("limit 1"));
        return content == null ? null : content.getFieldValue();
    }

    /**
     * 保存某业务某语言下的字段（覆盖）
     */
    public void saveFields(String bizType, Long bizId, String langCode, Map<String, String> fields) {
        if (fields == null || fields.isEmpty()) {
            return;
        }
        contentService.remove(new LambdaQueryWrapper<SysI18nContent>()
                .eq(SysI18nContent::getBizType, bizType)
                .eq(SysI18nContent::getBizId, bizId)
                .eq(SysI18nContent::getLangCode, langCode));
        List<SysI18nContent> contents = fields.entrySet().stream().map(e -> {
            SysI18nContent content = new SysI18nContent();
            content.setBizType(bizType);
            content.setBizId(bizId);
            content.setLangCode(langCode);
            content.setFieldKey(e.getKey());
            content.setFieldValue(e.getValue());
            return content;
        }).collect(Collectors.toList());
        contentService.saveBatch(contents);
    }

    /**
     * 机器翻译初翻（对接第三方翻译接口占位，返回原文供人工校对）
     */
    public String machineTranslate(String text, String from, String to) {
        return text;
    }
}
