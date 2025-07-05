
package com.hu.oneclick.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Git仓库初始化参数")
public class GitRepoInitParam {
    
    @Schema(description = "仓库名称")
    private String repoName;
    
    @Schema(description = "项目ID")
    private String projectId;
    
    @Schema(description = "项目名称")
    private String projectName;
}
