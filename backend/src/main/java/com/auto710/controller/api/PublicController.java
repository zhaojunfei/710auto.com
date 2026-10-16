package com.auto710.controller.api;

import com.auto710.common.api.PageResult;
import com.auto710.common.api.Result;
import com.auto710.config.ConfigService;
import com.auto710.entity.*;
import com.auto710.i18n.I18nService;
import com.auto710.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import cn.hutool.core.util.StrUtil;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 【前台公开接口】PC官网 / H5 / 小程序 共用
 * <p>读取后台配置内容、菜单、服务项目、文章、案例、Banner；提交在线留言。</p>
 */
@Tag(name = "前台公开接口")
@RestController
@RequestMapping({"/api/public", "/api/www", "/api/h5", "/api/miniapp"})
@RequiredArgsConstructor
public class PublicController {

    private final ConfigService configService;
    private final I18nService i18nService;
    private final CmsMenuService menuService;
    private final CmsBannerService bannerService;
    private final ServiceCategoryService categoryService;
    private final ServiceItemService itemService;
    private final CmsArticleService articleService;
    private final CmsCaseService caseService;
    private final CmsBrandStoryService brandStoryService;
    private final StoreInfoService storeService;
    private final CmsMessageService messageService;
    private final MaintenancePackageService packageService;

    @Operation(summary = "站点公开配置")
    @GetMapping("/site")
    public Result<Map<String, String>> site() {
        return Result.ok(configService.siteConfig());
    }

    @Operation(summary = "启用语言列表")
    @GetMapping("/languages")
    public Result<List<SysLanguage>> languages() {
        return Result.ok(i18nService.enabledLanguages());
    }

    @Operation(summary = "多端菜单")
    @GetMapping("/menu")
    public Result<List<CmsMenu>> menu(@RequestParam(defaultValue = "public") String platform) {
        return Result.ok(menuService.list(new LambdaQueryWrapper<CmsMenu>()
                .eq(CmsMenu::getPlatform, platform)
                .eq(CmsMenu::getStatus, 1)
                .orderByAsc(CmsMenu::getSort)));
    }

    @Operation(summary = "轮播图")
    @GetMapping("/banners")
    public Result<List<CmsBanner>> banners(@RequestParam(defaultValue = "home") String platform) {
        return Result.ok(bannerService.list(new LambdaQueryWrapper<CmsBanner>()
                .eq(CmsBanner::getPlatform, platform)
                .eq(CmsBanner::getStatus, 1)
                .orderByAsc(CmsBanner::getSort)));
    }

    @Operation(summary = "服务分类")
    @GetMapping("/service-categories")
    public Result<List<ServiceCategory>> categories() {
        return Result.ok(categoryService.list(new LambdaQueryWrapper<ServiceCategory>()
                .eq(ServiceCategory::getStatus, 1)
                .orderByAsc(ServiceCategory::getSort)));
    }

    @Operation(summary = "服务项目分页")
    @GetMapping("/service-items")
    public Result<PageResult<ServiceItem>> serviceItems(@RequestParam(defaultValue = "1") long page,
                                                        @RequestParam(defaultValue = "10") long size,
                                                        @RequestParam(required = false) Long categoryId,
                                                        @RequestParam(required = false) String keyword) {
        Page<ServiceItem> p = new Page<>(page, size);
        LambdaQueryWrapper<ServiceItem> wrapper = new LambdaQueryWrapper<ServiceItem>()
                .eq(ServiceItem::getStatus, 1)
                .eq(categoryId != null, ServiceItem::getCategoryId, categoryId)
                .like(StrUtil.isNotBlank(keyword), ServiceItem::getItemName, keyword)
                .orderByAsc(ServiceItem::getSort);
        return Result.ok(PageResult.of(itemService.page(p, wrapper)));
    }

    @Operation(summary = "服务项目详情")
    @GetMapping("/service-items/{id}")
    public Result<ServiceItem> serviceItem(@PathVariable Long id) {
        ServiceItem item = itemService.getById(id);
        if (item != null) {
            item.setViewCount((item.getViewCount() == null ? 0 : item.getViewCount()) + 1);
            itemService.updateById(item);
        }
        return Result.ok(item);
    }

    @Operation(summary = "养护知识文章分页")
    @GetMapping("/articles")
    public Result<PageResult<CmsArticle>> articles(@RequestParam(defaultValue = "1") long page,
                                                   @RequestParam(defaultValue = "10") long size,
                                                   @RequestParam(required = false) Long categoryId) {
        Page<CmsArticle> p = new Page<>(page, size);
        LambdaQueryWrapper<CmsArticle> wrapper = new LambdaQueryWrapper<CmsArticle>()
                .eq(CmsArticle::getStatus, 1)
                .eq(categoryId != null, CmsArticle::getCategoryId, categoryId)
                .orderByDesc(CmsArticle::getIsTop)
                .orderByDesc(CmsArticle::getPublishTime);
        return Result.ok(PageResult.of(articleService.page(p, wrapper)));
    }

    @Operation(summary = "文章详情")
    @GetMapping("/articles/{id}")
    public Result<CmsArticle> article(@PathVariable Long id) {
        CmsArticle article = articleService.getById(id);
        if (article != null) {
            article.setViewCount((article.getViewCount() == null ? 0 : article.getViewCount()) + 1);
            articleService.updateById(article);
        }
        return Result.ok(article);
    }

    @Operation(summary = "施工案例")
    @GetMapping("/cases")
    public Result<List<CmsCase>> cases(@RequestParam(required = false) String caseType) {
        return Result.ok(caseService.list(new LambdaQueryWrapper<CmsCase>()
                .eq(CmsCase::getStatus, 1)
                .eq(StrUtil.isNotBlank(caseType), CmsCase::getCaseType, caseType)
                .orderByAsc(CmsCase::getSort)));
    }

    @Operation(summary = "品牌故事")
    @GetMapping("/brand-story")
    public Result<List<CmsBrandStory>> brandStory() {
        return Result.ok(brandStoryService.list(new LambdaQueryWrapper<CmsBrandStory>()
                .eq(CmsBrandStory::getStatus, 1)
                .orderByAsc(CmsBrandStory::getSort)));
    }

    @Operation(summary = "门店信息")
    @GetMapping("/store")
    public Result<StoreInfo> store() {
        return Result.ok(storeService.getOne(new LambdaQueryWrapper<StoreInfo>()
                .eq(StoreInfo::getStatus, 1).last("limit 1")));
    }

    @Operation(summary = "养护套餐")
    @GetMapping("/packages")
    public Result<List<MaintenancePackage>> packages() {
        return Result.ok(packageService.list(new LambdaQueryWrapper<MaintenancePackage>()
                .eq(MaintenancePackage::getStatus, 1)
                .orderByAsc(MaintenancePackage::getSort)));
    }

    @Operation(summary = "在线留言提交")
    @PostMapping("/message")
    public Result<Boolean> submitMessage(@RequestBody CmsMessage message) {
        message.setId(null);
        message.setStatus(0);
        return Result.ok(messageService.save(message));
    }

    @Operation(summary = "首页聚合数据")
    @GetMapping("/home")
    public Result<Map<String, Object>> home() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("site", configService.siteConfig());
        data.put("banners", banners("home").getData());
        data.put("categories", categories().getData());
        data.put("hotItems", itemService.list(new LambdaQueryWrapper<ServiceItem>()
                .eq(ServiceItem::getStatus, 1)
                .orderByDesc(ServiceItem::getViewCount)
                .last("limit 8")));
        data.put("cases", caseService.list(new LambdaQueryWrapper<CmsCase>()
                .eq(CmsCase::getStatus, 1).orderByDesc(CmsCase::getCreateTime).last("limit 6")));
        data.put("articles", articleService.list(new LambdaQueryWrapper<CmsArticle>()
                .eq(CmsArticle::getStatus, 1).orderByDesc(CmsArticle::getPublishTime).last("limit 5")));
        data.put("store", store().getData());
        data.put("languages", i18nService.enabledLanguages());
        return Result.ok(data);
    }
}
