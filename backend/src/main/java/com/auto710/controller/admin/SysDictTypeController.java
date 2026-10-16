package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.SysDictType;
import com.auto710.service.SysDictTypeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】字典类型 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "字典类型管理")
@RestController
@RequestMapping("/api/admin/sys-dict-type")
public class SysDictTypeController extends BaseController<SysDictTypeService, SysDictType> {

    public SysDictTypeController(SysDictTypeService service) {
        super(service);
    }
}
