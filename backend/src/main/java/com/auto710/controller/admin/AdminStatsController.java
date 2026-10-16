package com.auto710.controller.admin;

import com.auto710.biz.StatsBizService;
import com.auto710.common.api.Result;
import com.auto710.entity.StatDaily;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * 【后台】数据统计看板
 */
@Tag(name = "数据统计")
@RestController
@RequestMapping("/api/admin/stat")
@RequiredArgsConstructor
public class AdminStatsController {

    private final StatsBizService statsBizService;

    @Operation(summary = "首页概览")
    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        return Result.ok(statsBizService.overview());
    }

    @Operation(summary = "经营看板：营收/项目排行/技师产值/留存")
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard(@RequestParam(defaultValue = "30") int days) {
        return Result.ok(statsBizService.dashboard(days));
    }

    @Operation(summary = "生成某日统计")
    @PostMapping("/generate")
    public Result<StatDaily> generate(@RequestParam(required = false) String date) {
        return Result.ok(statsBizService.generateDaily(date == null ? LocalDate.now().toString() : date));
    }
}
