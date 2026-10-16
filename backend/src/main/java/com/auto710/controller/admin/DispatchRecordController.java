package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.DispatchRecord;
import com.auto710.service.DispatchRecordService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】派工记录 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "派工记录管理")
@RestController
@RequestMapping("/api/admin/dispatch-record")
public class DispatchRecordController extends BaseController<DispatchRecordService, DispatchRecord> {

    public DispatchRecordController(DispatchRecordService service) {
        super(service);
    }
}
