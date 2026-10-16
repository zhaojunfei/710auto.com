package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.TechnicianSchedule;
import com.auto710.service.TechnicianScheduleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】技师排班 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "技师排班管理")
@RestController
@RequestMapping("/api/admin/technician-schedule")
public class TechnicianScheduleController extends BaseController<TechnicianScheduleService, TechnicianSchedule> {

    public TechnicianScheduleController(TechnicianScheduleService service) {
        super(service);
    }
}
