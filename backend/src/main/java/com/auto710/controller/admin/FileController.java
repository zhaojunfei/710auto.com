package com.auto710.controller.admin;

import com.auto710.common.api.Result;
import com.auto710.storage.StorageResult;
import com.auto710.storage.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 【后台】文件上传（图片 / 附件 / 富文本粘贴图片，统一走对象存储）
 */
@Tag(name = "文件上传")
@RestController
@RequestMapping("/api/admin/file")
@RequiredArgsConstructor
public class FileController {

    private final StorageService storageService;

    @Operation(summary = "单文件上传")
    @PostMapping("/upload")
    public Result<StorageResult> upload(@RequestParam("file") MultipartFile file,
                                        @RequestParam(value = "dir", required = false) String dir,
                                        @RequestParam(value = "bizType", required = false) String bizType) {
        return Result.ok(storageService.upload(file, dir, bizType));
    }

    @Operation(summary = "多文件上传")
    @PostMapping("/uploads")
    public Result<List<StorageResult>> uploads(@RequestParam("files") MultipartFile[] files,
                                               @RequestParam(value = "dir", required = false) String dir,
                                               @RequestParam(value = "bizType", required = false) String bizType) {
        List<StorageResult> results = new ArrayList<>();
        for (MultipartFile file : files) {
            results.add(storageService.upload(file, dir, bizType));
        }
        return Result.ok(results);
    }

    @Operation(summary = "删除文件")
    @DeleteMapping
    public Result<Boolean> delete(@RequestParam String key) {
        storageService.delete(key);
        return Result.ok(true);
    }
}
