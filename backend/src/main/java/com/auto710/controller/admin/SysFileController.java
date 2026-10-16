package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.SysFile;
import com.auto710.service.SysFileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】文件上传记录 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "文件上传记录管理")
@RestController
@RequestMapping("/api/admin/sys-file")
public class SysFileController extends BaseController<SysFileService, SysFile> {

    public SysFileController(SysFileService service) {
        super(service);
    }
}
