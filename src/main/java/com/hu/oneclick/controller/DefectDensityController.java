
package com.hu.oneclick.controller;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.DefectDensityRequestDto;
import com.hu.oneclick.model.domain.dto.DefectDensityResponseDto;
import com.hu.oneclick.server.service.DefectDensityService;
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
                    .buildResult("计算缺陷密度失败：" + e.getMessage());
        }
    }
}
