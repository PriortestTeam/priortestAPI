package com.hu.oneclick.controller;

import com.hu.oneclick.common.page.BaseController;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.VersionDto;
import com.hu.oneclick.model.domain.dto.VersionRequestDto;
import com.hu.oneclick.server.service.VersionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/release/versionManagement")
@Tag(name = "版本api", description = "版本api相关接口")
public class VersionController extends BaseController {

    @Autowired
    VersionService versionService;

    @Operation(summary="增加"))
    @PostMapping("releaseCreation")
    public Resp releaseCreation(@Valid @RequestBody VersionRequestDto releaseCreationDto) {
        var id = versionService.releaseCreation(releaseCreationDto);
        return new Resp.Builder().setData(id).ok();
    }

    @Operation(summary="修改"))
    @PutMapping("releaseModification")
    public Resp releaseModification(@Valid @RequestBody VersionRequestDto releaseModification) {
        versionService.releaseModification(releaseModification);
        return new Resp.Builder().ok();
    }

    @Operation(summary="查询"))
    @GetMapping("getVersion")
    public Resp<VersionDto> getVersion(@RequestParam Long id) {
        VersionDto versionDto = versionService.getVersion(id);
        return new Resp.Builder<VersionDto>().setData(versionDto).ok();
    }


    @Operation(summary="列表查询"))
    @PostMapping("getVersionList")
    public Resp<List<VersionDto>> getVersionList(@RequestBody VersionRequestDto releaseModification) {
        List<VersionDto> versionDto = versionService.getVersionList(releaseModification);
        return new Resp.Builder().setData(versionDto).ok();
    }


}
