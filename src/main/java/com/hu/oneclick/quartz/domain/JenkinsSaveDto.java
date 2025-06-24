package com.hu.oneclick.quartz.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "Jenkins保存DTO")
public class JenkinsSaveDto {

    @NotBlank(message = "Job名称不能为空")
    @Schema(description = "Job名称", required = true)
    private String jobName;

    @NotBlank(message = "XML配置不能为空")
    @Schema(description = "Job的XML配置", required = true)
    private String xml;

}