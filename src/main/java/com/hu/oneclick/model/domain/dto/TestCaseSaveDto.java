package com.hu.oneclick.model.domain.dto;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 测试用例dto
 *
 * @author xiaohai
 * @date 2023/03/06
 */
@Setter
@Getter
@ApiModel("测试用例DTO")
//@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TestCaseSaveDto implements Serializable {

    private static final long serialVersionUID = -806606802497649838L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
     * 关联项目id
     */
    @ApiModelProperty("关联项目id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    @NotBlank(message = "名称不能为空")
    private String title;

    /**
     * 优先级
     */
    @ApiModelProperty("优先级")
    @NotBlank(message = "优先级不能为空")
    private String priority;

    /**
     * 故事id
     */
    @ApiModelProperty("故事id")
    @NotBlank(message = "故事id不能为空")
    private String feature;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;

    /**
     * 执行时间
     */
    @ApiModelProperty("执行时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date executeTime;

    /**
     * 浏览器
     */
    @ApiModelProperty("浏览器")
    private String browser;

    /**
     * 平台
     */
    @ApiModelProperty("平台")
    private String platform;

    /**
     * 版本
     */
    @ApiModelProperty("版本")
    private String version;

    /**
     * 用例类别
     */
    @ApiModelProperty("用例类别")
    private String caseCategory;

    /**
     * 用例类型
     */
    @ApiModelProperty("用例类型")
    private String testType;

    /**
     * 前提条件
     */
    @ApiModelProperty("前提条件")
    private String testCondition;

    /**
     * 环境
     */
    @ApiModelProperty("环境")
    private String env;

    /**
     * 外部id
     */
    @ApiModelProperty("外部id")
    private String externalLinkId;

    /**
     * 最后运行状态
     */
    @ApiModelProperty("最后运行状态")
    private Integer lastRunStatus;

    /**
     * 模块
     */
    @ApiModelProperty("模块")
    private String module;

    /**
     * 测试设备
     */
    @ApiModelProperty("测试设备")
    private String testDevice;

    /**
     * 测试数据
     */
    @ApiModelProperty("测试数据")
    private String testData;

    /**
     * 测试方法
     */
    @ApiModelProperty("测试方法")
    private String testMethod;

    /**
     * test_status
     */
    @ApiModelProperty("test_status")
    private String testStatus;

    /**
     * 测试执行状态
     */
    @ApiModelProperty("测试执行状态")
    private Integer runStatus;

    /**
     * testcase_expand
     */
    @ApiModelProperty("testcase_expand")
    private String testcaseExpand;

    /**
     * remarks
     */
    @ApiModelProperty("remarks")
    private String remarks;

    /**
     * 修改者
     */
    @ApiModelProperty("修改者")
    private String updateUserId;

    /**
     * 创建者
     */
    @ApiModelProperty("创建者")
    private Long userId;

    /**
     * 自定义字段值
     */
    @ApiModelProperty("自定义字段值")
    private JSONObject customFieldDatas;

    /**
     * reportTo
     */
    @ApiModelProperty("reportTo")
    private String reportTo;

}
