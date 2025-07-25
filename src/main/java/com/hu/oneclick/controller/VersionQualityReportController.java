package com.hu.oneclick.controller;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.server.service.VersionQualityReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 版本质量分析报表控制器
 */
@RestController
@RequestMapping("/versionQualityReport")
@Tag(name = "版本质量分析报表", description = "版本质量分析报表相关接口")
@Component
public class VersionQualityReportController {

    @Autowired
    private VersionQualityReportService versionQualityReportService;

    @GetMapping("/storyCoverage")
    @Operation(summary = "获取故事覆盖率", description = "获取指定项目和版本的故事覆盖率统计")
    public Resp<Map<String, Object>> getStoryCoverage(
            @Parameter(description = "项目ID") @RequestParam Long projectId,
            @Parameter(description = "版本号") @RequestParam String version) {
        return versionQualityReportService.getStoryCoverage(projectId, version);
    }

    @PostMapping("/storyCoverage")
    @Operation(summary = "获取故事覆盖率 (POST)", description = "通过POST方法获取指定项目和版本的故事覆盖率统计")
    public Resp<Map<String, Object>> getStoryCoveragePost(@RequestBody Map<String, Object> request) {
        String projectIdStr = String.valueOf(request.get("projectId"));
        Long projectId = Long.valueOf(projectIdStr);
        String majorVersion = (String) request.get("majorVersion");
        @SuppressWarnings("unchecked")
        List<String> includeVersions = (List<String>) request.get("includeVersions");
        
        // 调用新的service方法来处理复杂的查询逻辑
        return versionQualityReportService.getStoryCoverageWithVersions(projectId, majorVersion, includeVersions);
    }

    @GetMapping("/getQualityOverview/{projectId}")
    @Operation(summary = "获取项目版本质量总览")
    public Resp<Map<String, Object>> getQualityOverview(@PathVariable String projectId) {
        return versionQualityReportService.getQualityOverview(projectId);
    }

    @GetMapping("/getDefectDensity/{projectId}/{releaseVersion}")
    @Operation(summary = "获取版本缺陷密度分析")
    public Resp<Map<String, Object>> getDefectDensity(
            @PathVariable String projectId, 
            @PathVariable String releaseVersion) {
        return versionQualityReportService.getDefectDensity(projectId, releaseVersion);
    }

    @GetMapping("/getTestCoverage/{projectId}/{releaseVersion}")
    @Operation(summary = "获取版本测试覆盖率分析")
    public Resp<Map<String, Object>> getTestCoverage(
            @PathVariable String projectId, 
            @PathVariable String releaseVersion) {
        return versionQualityReportService.getTestCoverage(projectId, releaseVersion);
    }

    @GetMapping("/getDefectDistribution/{projectId}/{releaseVersion}")
    @Operation(summary = "获取版本缺陷分布分析")
    public Resp<Map<String, Object>> getDefectDistribution(
            @PathVariable String projectId, 
            @PathVariable String releaseVersion) {
        return versionQualityReportService.getDefectDistribution(projectId, releaseVersion);
    }

    @GetMapping("/getExecutionRate/{projectId}/{releaseVersion}")
    @Operation(summary = "获取版本测试执行率分析")
    public Resp<Map<String, Object>> getExecutionRate(
            @PathVariable String projectId, 
            @PathVariable String releaseVersion) {
        return versionQualityReportService.getExecutionRate(projectId, releaseVersion);
    }

    @GetMapping("/getVersionComparison/{projectId}")
    @Operation(summary = "获取版本质量对比分析")
    public Resp<Map<String, Object>> getVersionComparison(
            @PathVariable String projectId,
            @RequestParam(required = false) String startVersion,
            @RequestParam(required = false) String endVersion) {
        return versionQualityReportService.getVersionComparison(projectId, startVersion, endVersion);
    }
}