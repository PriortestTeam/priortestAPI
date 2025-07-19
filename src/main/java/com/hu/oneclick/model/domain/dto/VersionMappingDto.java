package com.hu.oneclick.model.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Schema(description = "版本映射Dto")
@Data
public class VersionMappingDto implements Serializable {

    private Long id;

    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @NotNull(message = "发布版本ID不能为空")
    private Long releaseId;

    @NotNull(message = "发布版本不能为空")
    private String releaseVersion;

    private String env;
    private String envVersion;
    private String remark;

    // 用于批量操作
    @Schema(description = "动态环境版本映射")
    // 动态环境版本映射，key为环境名称(dev/stg/online等)，value为该环境的版本列表
    private Map<String, List<String>> envVersions;
}