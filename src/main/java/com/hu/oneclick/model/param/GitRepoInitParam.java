package com.hu.oneclick.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;

@Data
@Schema(description = "Git仓库初始化"))
public class GitRepoInitParam {
    @Schema(description = "仓库名称", required = true))
    @NotEmpty(message = "仓库名称不可为空")
    private String repoName;

    @Schema(description = "项目ID", required = true))
    @NotEmpty(message = "项目ID不可为空")
    private String projectId;

    @Schema(description = "项目名称", required = true))
    @NotEmpty(message = "项目名称不可为空")
    private String projectName;
}
