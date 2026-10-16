package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.Appointment;
import com.auto710.service.AppointmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】预约单 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "预约单管理")
@RestController
@RequestMapping("/api/admin/appointment")
public class AppointmentController extends BaseController<AppointmentService, Appointment> {

    public AppointmentController(AppointmentService service) {
        super(service);
    }
}
