package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.ServicePrice;
import com.auto710.service.ServicePriceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】价格配置 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "价格配置管理")
@RestController
@RequestMapping("/api/admin/service-price")
public class ServicePriceController extends BaseController<ServicePriceService, ServicePrice> {

    public ServicePriceController(ServicePriceService service) {
        super(service);
    }
}
