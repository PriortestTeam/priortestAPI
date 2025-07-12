
package com.hu.oneclick.controller;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.*;
import com.hu.oneclick.server.service.ChartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chart")
@Tag(name = "图表接口", description = "项目进度和统计图表相关接口")
public class ChartController {

    @Autowired
    private ChartService chartService;

    @Operation(summary = "获取项目进度仪表板")
    @GetMapping("/dashboard/{projectId}")
    public Resp<ProjectProgressDto> getProjectDashboard(@PathVariable String projectId) {
        return chartService.getProjectDashboard(projectId);
    }

    @Operation(summary = "获取甘特图数据")
    @GetMapping("/gantt/{projectId}")
    public Resp<List<GanttChartDto>> getGanttChart(@PathVariable String projectId) {
        return chartService.getGanttChart(projectId);
    }

    @Operation(summary = "获取燃尽图数据")
    @GetMapping("/burndown/{projectId}")
    public Resp<List<BurndownChartDto>> getBurndownChart(
            @PathVariable String projectId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return chartService.getBurndownChart(projectId, startDate, endDate);
    }

    @Operation(summary = "获取测试执行趋势图")
    @GetMapping("/execution-trend/{projectId}")
    public Resp<ChartDataDto> getTestExecutionTrend(
            @PathVariable String projectId,
            @RequestParam(required = false, defaultValue = "30") String dateRange) {
        return chartService.getTestExecutionTrend(projectId, dateRange);
    }

    @Operation(summary = "获取测试结果分布图")
    @GetMapping("/result-distribution/{projectId}")
    public Resp<ChartDataDto> getTestResultDistribution(@PathVariable String projectId) {
        return chartService.getTestResultDistribution(projectId);
    }

    @Operation(summary = "获取缺陷统计图表")
    @GetMapping("/defect-statistics/{projectId}")
    public Resp<ChartDataDto> getDefectStatistics(@PathVariable String projectId) {
        return chartService.getDefectStatistics(projectId);
    }
}
