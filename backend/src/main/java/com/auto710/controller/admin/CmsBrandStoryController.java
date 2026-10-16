package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.CmsBrandStory;
import com.auto710.service.CmsBrandStoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】品牌故事 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "品牌故事管理")
@RestController
@RequestMapping("/api/admin/cms-brand-story")
public class CmsBrandStoryController extends BaseController<CmsBrandStoryService, CmsBrandStory> {

    public CmsBrandStoryController(CmsBrandStoryService service) {
        super(service);
    }
}
