package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.SupplierDelivery;
import com.auto710.service.SupplierDeliveryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】供应商送货单 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "供应商送货单管理")
@RestController
@RequestMapping("/api/admin/supplier-delivery")
public class SupplierDeliveryController extends BaseController<SupplierDeliveryService, SupplierDelivery> {

    public SupplierDeliveryController(SupplierDeliveryService service) {
        super(service);
    }
}
