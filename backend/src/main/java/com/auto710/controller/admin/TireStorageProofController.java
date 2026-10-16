package com.auto710.controller.admin;

import com.auto710.common.web.BaseController;
import com.auto710.entity.TireStorageProof;
import com.auto710.service.TireStorageProofService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【后台】寄存凭证 接口
 * <p>710号汽车之家 数字化多端平台</p>
 */
@Tag(name = "寄存凭证管理")
@RestController
@RequestMapping("/api/admin/tire-storage-proof")
public class TireStorageProofController extends BaseController<TireStorageProofService, TireStorageProof> {

    public TireStorageProofController(TireStorageProofService service) {
        super(service);
    }
}
