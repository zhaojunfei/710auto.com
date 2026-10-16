package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.PartSupplier;
import com.auto710.service.PartSupplierService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】配件供应商 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "配件供应商管理")
@RestController
@RequestMapping("/api/admin/part-supplier")
public class PartSupplierController extends BaseController<PartSupplierService, PartSupplier> {

    public PartSupplierController(PartSupplierService service) {
        super(service);
    }
}
