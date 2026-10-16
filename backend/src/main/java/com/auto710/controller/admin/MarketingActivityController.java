package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.MarketingActivity;
import com.auto710.service.MarketingActivityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】营销活动 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "营销活动管理")
@RestController
@RequestMapping("/api/admin/marketing-activity")
public class MarketingActivityController extends BaseController<MarketingActivityService, MarketingActivity> {

    public MarketingActivityController(MarketingActivityService service) {
        super(service);
    }
}
