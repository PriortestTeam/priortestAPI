package com.hu.oneclick.controller;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.DefectDensityRequestDto;
import com.hu.oneclick.model.domain.dto.DefectDensityResponseDto;
import com.hu.oneclick.model.domain.dto.VersionEscapeAnalysisRequestDto;
import com.hu.oneclick.model.domain.dto.VersionEscapeAnalysisResponseDto;
import com.hu.oneclick.server.service.DefectDensityService;
import com.hu.oneclick.server.service.VersionEscapeAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

/**
 * 缺陷密度报表控制器
 */
@Slf4j
@RestController
@RequestMapping("/versionQualityReport")
@Tag(name = "缺陷密度报表", description = "缺陷密度计算相关接口")
public class DefectDensityController {

    @Resource
    private DefectDensityService defectDensityService;

    @Resource
    private VersionEscapeAnalysisService versionEscapeAnalysisService;

    @Operation(summary = "计算缺陷密度", description = "根据指定条件计算缺陷密度，并返回缺陷详情和关联测试用例信息")
    @PostMapping("/defectDensity")
    public Resp<DefectDensityResponseDto> calculateDefectDensity(
            @Valid @RequestBody DefectDensityRequestDto requestDto) {
        try {
            log.info("计算缺陷密度，请求参数：{}", requestDto);

            DefectDensityResponseDto responseDto = defectDensityService.calculateDefectDensity(requestDto);

            log.info("缺陷密度计算成功，项目：{}，版本：{}，密度：{}%，质量等级：{}",
                    requestDto.getProjectId(), requestDto.getMajorVersion(),
                    responseDto.getDefectDensity(), responseDto.getQualityLevel());

            return new Resp.Builder<DefectDensityResponseDto>()
                    .setData(responseDto)
                    .ok();

        } catch (Exception e) {
            log.error("计算缺陷密度失败，原因：{}", e.getMessage(), e);
            return new Resp.Builder<DefectDensityResponseDto>()
                    .buildResult("500","计算缺陷密度失败：" + e.getMessage());
        }
    }

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
}