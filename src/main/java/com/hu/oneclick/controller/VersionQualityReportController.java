
package com.hu.oneclick.controller;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.server.service.VersionQualityReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 版本质量分析报表控制器
 */
@RestController
@RequestMapping("/api/versionQualityReport")
@Tag(name = "版本质量分析", description = "版本质量分析报表相关接口")
public class VersionQualityReportController {

    @Autowired
    private VersionQualityReportService versionQualityReportService;

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
package com.hu.oneclick.controller;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.server.service.VersionQualityReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/versionQualityReport")
@Tag(name = "版本质量报表", description = "版本质量评估相关接口")
@RequiredArgsConstructor
public class VersionQualityReportController {

    private final VersionQualityReportService versionQualityReportService;

    @GetMapping("/storyCoverage")
    @Operation(summary = "获取故事覆盖率", description = "获取指定项目和版本的故事覆盖率统计")
    public Resp<Map<String, Object>> getStoryCoverage(
            @Parameter(description = "项目ID", required = true)
            @RequestParam Long projectId,
            @Parameter(description = "版本号", required = true)
            @RequestParam String version) {
        return versionQualityReportService.getStoryCoverage(projectId, version);
    }
}
