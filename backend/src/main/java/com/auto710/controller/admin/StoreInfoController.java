package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.StoreInfo;
import com.auto710.service.StoreInfoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】门店信息 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "门店信息管理")
@RestController
@RequestMapping("/api/admin/store-info")
public class StoreInfoController extends BaseController<StoreInfoService, StoreInfo> {

    public StoreInfoController(StoreInfoService service) {
        super(service);
    }
}
