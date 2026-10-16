package com.auto710.controller.api;

import com.auto710.common.api.Result;
import com.auto710.storage.StorageResult;
import com.auto710.storage.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 【会员 / 技师端】文件上传（头像、存胎凭证、报价单等）
 */
@Tag(name = "前台文件上传")
@RestController
@RequestMapping({"/api/member/file", "/api/tech/file"})
@RequiredArgsConstructor
public class ApiFileController {

    private final StorageService storageService;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public Result<StorageResult> upload(@RequestParam("file") MultipartFile file,
                                        @RequestParam(value = "dir", required = false) String dir,
                                        @RequestParam(value = "bizType", required = false) String bizType) {
        return Result.ok(storageService.upload(file, dir, bizType));
    }
}
