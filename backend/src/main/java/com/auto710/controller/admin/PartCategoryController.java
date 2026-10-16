package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.PartCategory;
import com.auto710.service.PartCategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】配件分类 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "配件分类管理")
@RestController
@RequestMapping("/api/admin/part-category")
public class PartCategoryController extends BaseController<PartCategoryService, PartCategory> {

    public PartCategoryController(PartCategoryService service) {
        super(service);
    }
}
