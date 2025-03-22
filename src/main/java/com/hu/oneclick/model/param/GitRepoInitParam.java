package com.hu.oneclick.model.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@ApiModel("Git仓库初始化")
public class GitRepoInitParam {
    @ApiModelProperty(value = "仓库名称", required = true)
    @NotEmpty(message = "仓库名称不可为空")
    private String repoName;

    @ApiModelProperty(value = "项目ID", required = true)
    @NotEmpty(message = "项目ID不可为空")
    private String projectId;

    @ApiModelProperty(value = "项目名称", required = true)
    @NotEmpty(message = "项目名称不可为空")
    private String projectName;
}
