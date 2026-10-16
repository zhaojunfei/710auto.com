package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.MaintenancePackage;
import com.auto710.service.MaintenancePackageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】养护套餐 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "养护套餐管理")
@RestController
@RequestMapping("/api/admin/maintenance-package")
public class MaintenancePackageController extends BaseController<MaintenancePackageService, MaintenancePackage> {

    public MaintenancePackageController(MaintenancePackageService service) {
        super(service);
    }
}
