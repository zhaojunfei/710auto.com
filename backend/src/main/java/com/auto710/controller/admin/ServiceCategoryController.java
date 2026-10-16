package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.ServiceCategory;
import com.auto710.service.ServiceCategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】服务分类 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "服务分类管理")
@RestController
@RequestMapping("/api/admin/service-category")
public class ServiceCategoryController extends BaseController<ServiceCategoryService, ServiceCategory> {

    public ServiceCategoryController(ServiceCategoryService service) {
        super(service);
    }
}
