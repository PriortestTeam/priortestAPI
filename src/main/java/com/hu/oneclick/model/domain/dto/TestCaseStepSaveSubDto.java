package com.hu.oneclick.model.domain.dto;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 测试用例步骤子步骤DTO
 *
 * @author xiaohai
 * @date 2023/04/09
 */
@Setter
@Getter
@ApiModel("测试用例步骤子步骤DTO")
public class TestCaseStepSaveSubDto implements Serializable {

    private static final long serialVersionUID = 2326317164248935852L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "主键id")
    private Long id;

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

    @ApiModelProperty("执行条件")
    private String teststepCondition;

    @ApiModelProperty("自定义字段值")
    private JSONObject customFieldDatas;

}
