package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.TireStorageOrder;
import com.auto710.service.TireStorageOrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】存胎寄存单 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "存胎寄存单管理")
@RestController
@RequestMapping("/api/admin/tire-storage-order")
public class TireStorageOrderController extends BaseController<TireStorageOrderService, TireStorageOrder> {

    public TireStorageOrderController(TireStorageOrderService service) {
        super(service);
    }
}
