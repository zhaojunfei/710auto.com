package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.CmsCase;
import com.auto710.service.CmsCaseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】施工案例 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "施工案例管理")
@RestController
@RequestMapping("/api/admin/cms-case")
public class CmsCaseController extends BaseController<CmsCaseService, CmsCase> {

    public CmsCaseController(CmsCaseService service) {
        super(service);
    }
}
