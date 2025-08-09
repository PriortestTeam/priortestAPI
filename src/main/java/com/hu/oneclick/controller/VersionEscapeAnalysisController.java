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
@RequestMapping("/api/versionQualityReport")
@Tag(name = "版本缺陷逃逸率分析", description = "分析指定版本的缺陷逃逸情况和质量指标")
public class VersionEscapeAnalysisController {

    @Resource
    private VersionEscapeAnalysisService versionEscapeAnalysisService;

    @Operation(
        summary = "分析版本缺陷逃逸率", 
        description = "分析指定版本引入的缺陷在该版本和后续版本中的发现情况，计算缺陷逃逸率和测试有效性"
    )
    @PostMapping("/escapeAnalysis")
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
                    .buildResult("500","分析版本缺陷逃逸率失败：" + e.getMessage());
        }
    }

    

    

    @Operation(
        summary = "导出逃逸率分析报告",
        description = "导出详细的版本缺陷逃逸率分析报告"
    )
    @PostMapping("/escapeAnalysis/export")
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
                    .buildResult("500","导出报告失败：" + e.getMessage());
        }
    }
}
