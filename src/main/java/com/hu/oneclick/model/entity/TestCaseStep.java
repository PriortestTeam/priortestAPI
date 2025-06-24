package com.hu.oneclick.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hu.oneclick.model.base.AssignIdEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema"测试用例步骤"
@TableName("test_case_step")
@Component
public class TestCaseStep extends AssignIdEntity implements Serializable {

    private static final long serialVersionUID = -2725298116058101604L;

    /**
     * 关联testcase id
     */
    @Schema(description = "关联testcase id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long testCaseId;

    /**
     * 步骤
     */
    @Schema(description = "步骤")
    private String testStep;

    /**
     * 预期结果
     */
    @Schema(description = "预期结果")
    private String expectedResult;

    /**
     * 测试数据
     */
    @Schema(description = "测试数据")
    private String testData;

    /**
     * remarks
     */
    @Schema(description = "remarks")
    private String remarks;

    /**
     * test_step_id
     */
    @Schema(description = "test_step_id")
    private Long testStepId;

    /**
     * teststep_expand
     */
    @Schema(description = "teststep_expand")
    private String teststepExpand;

    /**
     * 执行条件
     */
    @Schema(description = "执行条件")
    private String teststepCondition;

    @Schema(description = "status_code")
    private int statusCode;
}
