package com.hu.oneclick.model.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 测试用例步骤DTO
 *
 * @author xiaohai
 * @date 2023/03/06
 */
@Setter
@Getter
@ApiModel("测试用例步骤DTO")
public class TestCaseStepSaveDto implements Serializable {

    private static final long serialVersionUID = 3696131947672453413L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty("关联testcase id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull(message = "关联testcase id不能为空")
    private Long testCaseId;

    @ApiModelProperty("步骤")
    private String testStep;

    @ApiModelProperty("预期结果")
    private String expectedResult;

    @ApiModelProperty("测试数据")
    private String testData;

    @ApiModelProperty("remarks")
    private String remarks;

    @ApiModelProperty("test_step_id")
    private Long testStepId;

    @ApiModelProperty("teststep_expand")
    private String teststepExpand;

}
