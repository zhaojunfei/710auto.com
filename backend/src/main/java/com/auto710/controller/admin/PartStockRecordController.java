package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.PartStockRecord;
import com.auto710.service.PartStockRecordService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】配件出入库记录 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "配件出入库记录管理")
@RestController
@RequestMapping("/api/admin/part-stock-record")
public class PartStockRecordController extends BaseController<PartStockRecordService, PartStockRecord> {

    public PartStockRecordController(PartStockRecordService service) {
        super(service);
    }
}
