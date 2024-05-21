package com.hu.oneclick.model.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class IssueStatusVo {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("状态")
    private String issueStatus;


}
