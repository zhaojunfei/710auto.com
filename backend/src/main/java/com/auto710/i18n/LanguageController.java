package com.auto710.i18n;

import com.auto710.common.api.Result;
import com.auto710.entity.SysLanguage;
import com.auto710.service.SysLanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 多语言配置接口
 */
@Tag(name = "多语言配置")
@RestController
@RequiredArgsConstructor
public class LanguageController {

    private final I18nService i18nService;
    private final SysLanguageService languageService;

    @Operation(summary = "启用语言列表（前台语言切换器）")
    @GetMapping("/api/public/languages")
    public Result<List<SysLanguage>> publicLanguages() {
        return Result.ok(i18nService.enabledLanguages());
    }

    @Operation(summary = "后台语言列表")
    @GetMapping("/api/admin/language/list-all")
    public Result<List<SysLanguage>> all() {
        return Result.ok(languageService.list());
    }

    @Operation(summary = "启用/停用语言")
    @PutMapping("/api/admin/language/{id}/status")
    public Result<Boolean> status(@PathVariable Long id, @RequestParam Integer status) {
        SysLanguage language = new SysLanguage();
        language.setId(id);
        language.setStatus(status);
        return Result.ok(languageService.updateById(language));
    }

    @Operation(summary = "设为默认语言")
    @PutMapping("/api/admin/language/{id}/default")
    public Result<Boolean> setDefault(@PathVariable Long id) {
        SysLanguage current = languageService.getById(id);
        if (current == null) {
            return Result.fail("语言不存在");
        }
        List<SysLanguage> all = languageService.list();
        for (SysLanguage language : all) {
            language.setIsDefault(language.getId().equals(id) ? 1 : 0);
        }
        return Result.ok(languageService.updateBatchById(all));
    }

    @Operation(summary = "读取某业务某语言内容字段")
    @GetMapping("/api/admin/i18n/fields")
    public Result<Map<String, String>> fields(@RequestParam String bizType,
                                              @RequestParam Long bizId,
                                              @RequestParam String langCode) {
        return Result.ok(i18nService.getFields(bizType, bizId, langCode));
    }

    @Operation(summary = "保存某业务某语言内容字段")
    @PostMapping("/api/admin/i18n/fields")
    public Result<Boolean> saveFields(@RequestParam String bizType,
                                      @RequestParam Long bizId,
                                      @RequestParam String langCode,
                                      @RequestBody Map<String, String> fields) {
        i18nService.saveFields(bizType, bizId, langCode, fields);
        return Result.ok(true);
    }
}
