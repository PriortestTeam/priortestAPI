
package com.hu.oneclick.controller;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.FunctionExecutionRateRequestDto;
import com.hu.oneclick.model.domain.dto.FunctionExecutionRateResponseDto;
import com.hu.oneclick.server.service.FunctionExecutionRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

/**
 * 功能执行率报表控制器
 */
@Slf4j
@RestController
@RequestMapping("/versionQualityReport")
@Tag(name = "功能执行率报表", description = "功能执行率报表相关接口")
public class FunctionExecutionRateController {

    @Resource
    private FunctionExecutionRateService functionExecutionRateService;

    @Operation(summary = "获取功能执行率报表")
    @PostMapping("/functionExecutionRate")
    public Resp<FunctionExecutionRateResponseDto> getFunctionExecutionRate(
            @Valid @RequestBody FunctionExecutionRateRequestDto requestDto) {
        try {
            log.info("获取功能执行率报表，请求参数：{}", requestDto);
            
            FunctionExecutionRateResponseDto responseDto = functionExecutionRateService.getFunctionExecutionRate(requestDto);
            
            log.info("功能执行率报表查询成功，版本：{}，执行率：{}%", 
                    responseDto.getVersions(), responseDto.getExecutionRate());
            
            return new Resp.Builder<FunctionExecutionRateResponseDto>()
                    .setData(responseDto)
                    .ok();
                    
        } catch (Exception e) {
            log.error("获取功能执行率报表失败，原因：{}", e.getMessage(), e);
            return new Resp.Builder<FunctionExecutionRateResponseDto>()
                    .buildResult("获取功能执行率报表失败：" + e.getMessage());
        }
    }
}
