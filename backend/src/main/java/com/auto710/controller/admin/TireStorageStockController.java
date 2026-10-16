package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.TireStorageStock;
import com.auto710.service.TireStorageStockService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】寄存库存 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "寄存库存管理")
@RestController
@RequestMapping("/api/admin/tire-storage-stock")
public class TireStorageStockController extends BaseController<TireStorageStockService, TireStorageStock> {

    public TireStorageStockController(TireStorageStockService service) {
        super(service);
    }
}
