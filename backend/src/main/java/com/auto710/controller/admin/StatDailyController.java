package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.StatDaily;
import com.auto710.service.StatDailyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】每日经营统计 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "每日经营统计管理")
@RestController
@RequestMapping("/api/admin/stat-daily")
public class StatDailyController extends BaseController<StatDailyService, StatDaily> {

    public StatDailyController(StatDailyService service) {
        super(service);
    }
}
