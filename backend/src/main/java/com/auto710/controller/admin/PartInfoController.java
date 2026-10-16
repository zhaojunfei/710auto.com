package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.PartInfo;
import com.auto710.service.PartInfoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】配件耗材 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "配件耗材管理")
@RestController
@RequestMapping("/api/admin/part-info")
public class PartInfoController extends BaseController<PartInfoService, PartInfo> {

    public PartInfoController(PartInfoService service) {
        super(service);
    }
}
