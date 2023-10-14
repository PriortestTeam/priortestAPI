package com.hu.oneclick.model.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 测试周期计划关联添加DTO
 *
 * @author xiaohai
 * @date 2023/08/25
 */
@Setter
@Getter
@ApiModel("测试周期计划关联添加DTO")
public class TestCyclePlanSaveDto implements Serializable {

    private static final long serialVersionUID = 5515234928549567892L;

    @ApiModelProperty("测试周期id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull(message = "测试周期id不能为空")
    private Long testCycleId;

    @ApiModelProperty(value = "jenkins任务名")
    @NotBlank(message = "jenkins任务名")
    private String jenkinsJobName;

    @ApiModelProperty(value = "cron表达式")
    @NotBlank(message = "cron表达式不能为空")
    private String cronExpression;

}
