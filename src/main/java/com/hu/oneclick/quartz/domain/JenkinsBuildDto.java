package com.hu.oneclick.quartz.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "Jenkins构建DTO")
public class JenkinsBuildDto {

    @Schema(description = "Job名称", required = true)
    private String jobName;

    @Schema(description = "构建参数")
    private Map<String, String> param;

}