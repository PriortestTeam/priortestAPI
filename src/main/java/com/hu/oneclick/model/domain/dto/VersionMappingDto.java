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

    // 用于批量操作 - 多环境多版本
    @Schema(description = "动态环境版本映射 - 用于批量创建")
    private Map<String, List<String>> envVersions;

    // 用于单环境追加操作 - 单环境多版本
    @Schema(description = "单环境版本列表 - 用于追加操作")
    private List<String> versions;
}