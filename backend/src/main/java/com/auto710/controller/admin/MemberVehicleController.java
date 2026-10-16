package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.MemberVehicle;
import com.auto710.service.MemberVehicleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】会员车辆 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "会员车辆管理")
@RestController
@RequestMapping("/api/admin/member-vehicle")
public class MemberVehicleController extends BaseController<MemberVehicleService, MemberVehicle> {

    public MemberVehicleController(MemberVehicleService service) {
        super(service);
    }
}
