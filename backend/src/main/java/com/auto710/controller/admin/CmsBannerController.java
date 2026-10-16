package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.CmsBanner;
import com.auto710.service.CmsBannerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】轮播图 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "轮播图管理")
@RestController
@RequestMapping("/api/admin/cms-banner")
public class CmsBannerController extends BaseController<CmsBannerService, CmsBanner> {

    public CmsBannerController(CmsBannerService service) {
        super(service);
    }
}
