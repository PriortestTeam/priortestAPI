package com.hu.oneclick.model.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class IssueStatusDto implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "主键id")
    private Long id;


    @ApiModelProperty("状态")
//    @NotBlank(message = "状态不能为空")
    private String issueStatus;

    @ApiModelProperty("关联测试用例")
//    @NotBlank(message = "关联测试用例不能为空")
    private String verifiedResult;

    @ApiModelProperty("缺陷修改版本号")
//    @NotBlank(message = "缺陷修改版本号不能为空")
    private String fixVersion;
}
