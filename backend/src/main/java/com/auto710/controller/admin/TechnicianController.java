package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.Technician;
import com.auto710.service.TechnicianService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】技师 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "技师管理")
@RestController
@RequestMapping("/api/admin/technician")
public class TechnicianController extends BaseController<TechnicianService, Technician> {

    public TechnicianController(TechnicianService service) {
        super(service);
    }
}
