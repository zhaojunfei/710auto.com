package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.ServiceItem;
import com.auto710.service.ServiceItemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】服务项目 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "服务项目管理")
@RestController
@RequestMapping("/api/admin/service-item")
public class ServiceItemController extends BaseController<ServiceItemService, ServiceItem> {

    public ServiceItemController(ServiceItemService service) {
        super(service);
    }
}
