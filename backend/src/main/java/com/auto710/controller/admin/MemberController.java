package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.Member;
import com.auto710.service.MemberService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】会员 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "会员管理")
@RestController
@RequestMapping("/api/admin/member")
public class MemberController extends BaseController<MemberService, Member> {

    public MemberController(MemberService service) {
        super(service);
    }
}
