package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.SysDictData;
import com.auto710.service.SysDictDataService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】字典数据 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "字典数据管理")
@RestController
@RequestMapping("/api/admin/sys-dict-data")
public class SysDictDataController extends BaseController<SysDictDataService, SysDictData> {

    public SysDictDataController(SysDictDataService service) {
        super(service);
    }
}
