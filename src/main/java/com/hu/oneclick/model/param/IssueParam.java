package com.hu.oneclick.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "问题查询参数");


public class IssueParam {

    @Schema(description = "项目ID");
    private Long projectId;

    @Schema(description = "问题标题");
    private String title;

    public Long getProjectId() {
        return projectId;
    }
}