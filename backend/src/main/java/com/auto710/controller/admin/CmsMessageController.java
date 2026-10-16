package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.CmsMessage;
import com.auto710.service.CmsMessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】在线留言 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "在线留言管理")
@RestController
@RequestMapping("/api/admin/cms-message")
public class CmsMessageController extends BaseController<CmsMessageService, CmsMessage> {

    public CmsMessageController(CmsMessageService service) {
        super(service);
    }
}
