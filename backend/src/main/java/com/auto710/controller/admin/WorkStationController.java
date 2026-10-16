package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.WorkStation;
import com.auto710.service.WorkStationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】工位 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "工位管理")
@RestController
@RequestMapping("/api/admin/work-station")
public class WorkStationController extends BaseController<WorkStationService, WorkStation> {

    public WorkStationController(WorkStationService service) {
        super(service);
    }
}
