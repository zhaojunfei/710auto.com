package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.TireStorageItem;
import com.auto710.service.TireStorageItemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】寄存轮胎明细 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "寄存轮胎明细管理")
@RestController
@RequestMapping("/api/admin/tire-storage-item")
public class TireStorageItemController extends BaseController<TireStorageItemService, TireStorageItem> {

    public TireStorageItemController(TireStorageItemService service) {
        super(service);
    }
}
