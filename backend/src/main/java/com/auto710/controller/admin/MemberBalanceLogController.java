package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.MemberBalanceLog;
import com.auto710.service.MemberBalanceLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】储值流水 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "储值流水管理")
@RestController
@RequestMapping("/api/admin/member-balance-log")
public class MemberBalanceLogController extends BaseController<MemberBalanceLogService, MemberBalanceLog> {

    public MemberBalanceLogController(MemberBalanceLogService service) {
        super(service);
    }
}
