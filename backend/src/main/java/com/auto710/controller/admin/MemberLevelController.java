package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.MemberLevel;
import com.auto710.service.MemberLevelService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】会员等级 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "会员等级管理")
@RestController
@RequestMapping("/api/admin/member-level")
public class MemberLevelController extends BaseController<MemberLevelService, MemberLevel> {

    public MemberLevelController(MemberLevelService service) {
        super(service);
    }
}
