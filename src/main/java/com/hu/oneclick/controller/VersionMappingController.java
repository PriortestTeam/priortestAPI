
package com.hu.oneclick.controller;

import com.hu.oneclick.common.page.BaseController;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.VersionMappingDto;
import com.hu.oneclick.server.service.VersionMappingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/release/versionMapping")
@Tag(name = "版本映射api", description = "版本映射相关接口")
public class VersionMappingController extends BaseController {

    @Autowired
    VersionMappingService versionMappingService;

    @Operation(summary="批量添加版本映射")
    @PostMapping("batchCreate")
    public Resp batchCreateMapping(@Valid @RequestBody VersionMappingDto mappingDto) {
        versionMappingService.batchCreateMapping(mappingDto);
        return new Resp.Builder().ok();
    }

    @Operation(summary="追加版本映射")
    @PostMapping("add")
    public Resp addMapping(@Valid @RequestBody VersionMappingDto mappingDto) {
        versionMappingService.addMapping(mappingDto);
        return new Resp.Builder().ok();
    }

    @Operation(summary="删除版本映射")
    @DeleteMapping("delete/{id}")
    public Resp deleteMapping(@PathVariable Long id) {
        versionMappingService.deleteMapping(id);
        return new Resp.Builder().ok();
    }

    @Operation(summary="根据发布版本查询映射")
    @GetMapping("getByRelease/{releaseId}")
    public Resp<List<VersionMappingDto>> getMappingByRelease(@PathVariable Long releaseId) {
        List<VersionMappingDto> mappings = versionMappingService.getMappingByRelease(releaseId);
        return new Resp.Builder<List<VersionMappingDto>>().setData(mappings).ok();
    }

    @Operation(summary="根据项目查询所有映射")
    @GetMapping("getByProject/{projectId}")
    public Resp<List<VersionMappingDto>> getMappingByProject(@PathVariable Long projectId) {
        List<VersionMappingDto> mappings = versionMappingService.getMappingByProject(projectId);
        return new Resp.Builder<List<VersionMappingDto>>().setData(mappings).ok();
    }
}
