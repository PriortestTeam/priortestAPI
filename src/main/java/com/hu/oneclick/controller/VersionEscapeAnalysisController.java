` tags.

```java
package com.hu.oneclick.controller;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.VersionEscapeAnalysisRequestDto;
import com.hu.oneclick.model.domain.dto.VersionEscapeAnalysisResponseDto;
import com.hu.oneclick.server.service.VersionEscapeAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;

/**
 * 版本缺陷逃逸率分析控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/versionEscapeAnalysis")
@Tag(name = "版本缺陷逃逸率分析", description = "分析指定版本的缺陷逃逸情况和质量指标")
public class VersionEscapeAnalysisController {

    @Resource
    private VersionEscapeAnalysisService versionEscapeAnalysisService;

    @Operation(
        summary = "分析版本缺陷逃逸率", 
        description = "分析指定版本引入的缺陷在该版本和后续版本中的发现情况，计算缺陷逃逸率和测试有效性"
    )
    @PostMapping("/analyze")
    public Resp<VersionEscapeAnalysisResponseDto> analyzeVersionEscapeRate(
            @Valid @RequestBody VersionEscapeAnalysisRequestDto requestDto) {
        try {
            log.info("开始分析版本缺陷逃逸率，项目：{}，版本：{}", 
                    requestDto.getProjectId(), requestDto.getAnalysisVersion());

            VersionEscapeAnalysisResponseDto responseDto = 
                    versionEscapeAnalysisService.analyzeVersionEscapeRate(requestDto);

            log.info("版本缺陷逃逸率分析完成，版本：{}，逃逸率：{}%，质量等级：{}", 
                    requestDto.getAnalysisVersion(), 
                    responseDto.getEscapeRateStats().getEscapeRate(),
                    responseDto.getEscapeRateStats().getQualityLevel());

            return new Resp.Builder<VersionEscapeAnalysisResponseDto>()
                    .setData(responseDto)
                    .ok();

        } catch (Exception e) {
            log.error("分析版本缺陷逃逸率失败", e);
            return new Resp.Builder<VersionEscapeAnalysisResponseDto>()
                    .buildResult(500,"分析版本缺陷逃逸率失败：" + e.getMessage());
        }
    }

    @Operation(
        summary = "快速分析版本逃逸率",
        description = "通过URL参数快速分析指定版本的缺陷逃逸率"
    )
    @GetMapping("/quick/{projectId}/{analysisVersion}")
    public Resp<VersionEscapeAnalysisResponseDto> quickAnalyze(
            @Parameter(description = "项目ID") @PathVariable String projectId,
            @Parameter(description = "要分析的版本号") @PathVariable String analysisVersion,
            @Parameter(description = "是否包含遗留缺陷分析") @RequestParam(defaultValue = "true") Boolean includeLegacy,
            @Parameter(description = "是否按严重程度分组") @RequestParam(defaultValue = "true") Boolean groupBySeverity) {

        try {
            VersionEscapeAnalysisRequestDto requestDto = new VersionEscapeAnalysisRequestDto();
            requestDto.setProjectId(projectId);
            requestDto.setAnalysisVersion(analysisVersion);
            requestDto.setIncludeLegacyAnalysis(includeLegacy);
            requestDto.setGroupBySeverity(groupBySeverity);
            requestDto.setGroupByFoundVersion(true);

            return analyzeVersionEscapeRate(requestDto);

        } catch (Exception e) {
            log.error("快速分析版本缺陷逃逸率失败", e);
            return new Resp.Builder<VersionEscapeAnalysisResponseDto>()
                    .buildResult(500,"快速分析失败：" + e.getMessage());
        }
    }

    @Operation(
        summary = "获取逃逸率趋势",
        description = "获取多个版本的逃逸率趋势对比数据"
    )
    @GetMapping("/trend/{projectId}")
    public Resp<Object> getEscapeRateTrend(
            @Parameter(description = "项目ID") @PathVariable String projectId,
            @Parameter(description = "版本列表") @RequestParam List<String> versions) {

        try {
            Object trendData = versionEscapeAnalysisService.getEscapeRateTrend(projectId, versions);

            return new Resp.Builder<Object>()
                    .setData(trendData)
                    .ok();

        } catch (Exception e) {
            log.error("获取逃逸率趋势失败", e);
            return new Resp.Builder<Object>()
                    .buildResult(500,"获取趋势数据失败：" + e.getMessage());
        }
    }

    @Operation(
        summary = "导出逃逸率分析报告",
        description = "导出详细的版本缺陷逃逸率分析报告"
    )
    @PostMapping("/export")
    public Resp<String> exportAnalysisReport(
            @Valid @RequestBody VersionEscapeAnalysisRequestDto requestDto) {

        try {
            String reportPath = versionEscapeAnalysisService.exportEscapeAnalysisReport(requestDto);

            return new Resp.Builder<String>()
                    .setData(reportPath)
                    .ok();

        } catch (Exception e) {
            log.error("导出逃逸率分析报告失败", e);
            return new Resp.Builder<String>()
                    .buildResult(500,"导出报告失败：" + e.getMessage());
        }
    }
}
```Okay, continuing to apply the replacements from the changes:
```java