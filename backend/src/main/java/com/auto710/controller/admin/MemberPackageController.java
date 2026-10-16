package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.MemberPackage;
import com.auto710.service.MemberPackageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】会员套餐 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "会员套餐管理")
@RestController
@RequestMapping("/api/admin/member-package")
public class MemberPackageController extends BaseController<MemberPackageService, MemberPackage> {

    public MemberPackageController(MemberPackageService service) {
        super(service);
    }
}
