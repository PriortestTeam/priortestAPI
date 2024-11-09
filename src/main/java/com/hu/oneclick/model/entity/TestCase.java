package com.hu.oneclick.model.entity;

import cn.zhxu.bs.bean.DbIgnore;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hu.oneclick.model.base.AssignBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 测试用例
 *
 * @author xiaohai
 * @date 2023/03/06
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("测试用例")
@TableName("test_case")
public class TestCase extends AssignBaseEntity implements Serializable {

    private static final long serialVersionUID = 114802398790239711L;

    /**
     * 关联项目id
     */
    @ApiModelProperty("关联项目id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long projectId;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String title;

    /**
     * 优先级
     */
    @ApiModelProperty("优先级")
    private String priority;

    /**
     * 故事id
     */
    @ApiModelProperty("故事id")
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
     * reportTo
     */
    @ApiModelProperty("reportTo")
    private String reportTo;

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

    @ApiModelProperty("测试用例步骤集合")
    @TableField(exist = false)
    @DbIgnore
    private List<TestCaseStep> testCaseStepList;

}
