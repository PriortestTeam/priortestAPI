package com.hu.oneclick.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hu.oneclick.model.base.AssignIdEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 测试用例步骤
 *
 * @author xiaohai
 * @date 2023/03/06
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("测试用例步骤")
@TableName("test_case_step")
@Component
public class TestCaseStep extends AssignIdEntity implements Serializable {

    private static final long serialVersionUID = -2725298116058101604L;

    /**
     * 关联testcase id
     */
    @ApiModelProperty("关联testcase id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long testCaseId;

    /**
     * 步骤
     */
    @ApiModelProperty("步骤")
    private String testStep;

    /**
     * 预期结果
     */
    @ApiModelProperty("预期结果")
    private String expectedResult;

    /**
     * 测试数据
     */
    @ApiModelProperty("测试数据")
    private String testData;

    /**
     * remarks
     */
    @ApiModelProperty("remarks")
    private String remarks;

    /**
     * test_step_id
     */
    @ApiModelProperty("test_step_id")
    private Long testStepId;

    /**
     * teststep_expand
     */
    @ApiModelProperty("teststep_expand")
    private String teststepExpand;

    /**
     * 执行条件
     */
    @ApiModelProperty("执行条件")
    private String teststepCondition;

    @ApiModelProperty("status_code")
    private int statusCode;
}
