package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.RefundRecord;
import com.auto710.service.RefundRecordService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】退款记录 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "退款记录管理")
@RestController
@RequestMapping("/api/admin/refund-record")
public class RefundRecordController extends BaseController<RefundRecordService, RefundRecord> {

    public RefundRecordController(RefundRecordService service) {
        super(service);
    }
}
