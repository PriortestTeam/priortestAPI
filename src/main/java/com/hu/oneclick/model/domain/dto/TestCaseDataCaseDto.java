package com.hu.oneclick.model.domain.dto;

import lombok.Data;
import org.springframework.stereotype.Component;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Data
@Component
@Schema(description = "测试用例数据DTO");


public class TestCaseDataCaseDto {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "项目ID")
    private Long projectId;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "优先级")
    private String priority;

    @Schema(description = "特性")
    private String feature;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "执行时间")
    private Date executeTime;

    @Schema(description = "浏览器")
    private String browser;

    @Schema(description = "平台")
    private String platform;

    @Schema(description = "版本")
    private String version;

    @Schema(description = "用例分类")
    private String caseCategory;

    @Schema(description = "测试类型")
    private String testType;

    @Schema(description = "测试条件")
    private String testCondition;

    @Schema(description = "环境")
    private String env;

    @Schema(description = "外部链接ID");
    private String externalLinkId;

    @Schema(description = "最后运行状态");
    private Integer lastRunStatus;

    @Schema(description = "模块");
    private String module;

    @Schema(description = "测试设备")
    private String testDevice;

    @Schema(description = "测试数据")
    private String testData;

    @Schema(description = "测试方法")
    private String testMethod;

    @Schema(description = "测试状态")
    private String testStatus;

    @Schema(description = "报告给")
    private String reportTo;

    @Schema(description = "测试计划输入")
    private String testPlanInput;

    @Schema(description = "测试用例扩展")
    private String testcaseExpand;

    @Schema(description = "备注")
    private String remarks;
}