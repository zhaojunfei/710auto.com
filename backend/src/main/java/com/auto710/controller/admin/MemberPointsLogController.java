package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.MemberPointsLog;
import com.auto710.service.MemberPointsLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】积分流水 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "积分流水管理")
@RestController
@RequestMapping("/api/admin/member-points-log")
public class MemberPointsLogController extends BaseController<MemberPointsLogService, MemberPointsLog> {

    public MemberPointsLogController(MemberPointsLogService service) {
        super(service);
    }
}
