package com.hu.oneclick.quartz.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 定时任务变更DTO
 *
 * @author xiaohai
 * @date 2023/07/10
 */
@Setter
@Getter
@ApiModel("定时任务操作DTO")
public class JobOperateDto implements Serializable {

    private static final long serialVersionUID = 3308577719580670615L;

    @ApiModelProperty(value = "任务名称")
    @NotBlank(message = "任务名称不能为空")
    private String jobName;

    @ApiModelProperty(value = "任务组名")
    private String jobGroupName = "DEFAULT";

}
