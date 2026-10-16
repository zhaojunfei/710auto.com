package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.CmsArticleCategory;
import com.auto710.service.CmsArticleCategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】文章分类 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "文章分类管理")
@RestController
@RequestMapping("/api/admin/cms-article-category")
public class CmsArticleCategoryController extends BaseController<CmsArticleCategoryService, CmsArticleCategory> {

    public CmsArticleCategoryController(CmsArticleCategoryService service) {
        super(service);
    }
}
