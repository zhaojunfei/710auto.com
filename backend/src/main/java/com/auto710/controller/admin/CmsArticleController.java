package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.CmsArticle;
import com.auto710.service.CmsArticleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】养护知识文章 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "养护知识文章管理")
@RestController
@RequestMapping("/api/admin/cms-article")
public class CmsArticleController extends BaseController<CmsArticleService, CmsArticle> {

    public CmsArticleController(CmsArticleService service) {
        super(service);
    }
}
