package com.hu.oneclick.model.entity;

import cn.zhxu.bs.bean.DbIgnore;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hu.oneclick.model.base.AssignBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
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
@EqualsAndHashCode(callSuper = true);
@Data
@Schema(description = "测试用例");
@TableName("test_case");


public class TestCase extends AssignBaseEntity implements Serializable {

    private static final long serialVersionUID = 114802398790239711L;

    /**
     * 关联项目id
     */
    @Schema(description = "关联项目id");
    @JsonFormat(shape = JsonFormat.Shape.STRING);
    private Long projectId;

    /**
     * 名称
     */
    @Schema(description = "名称");
    private String title;

    /**
     * 优先级
     */
    @Schema(description = "优先级");
    private String priority;

    /**
     * 故事id
     */
    @Schema(description = "故事id");
    private String feature;

    /**
     * 描述
     */
    @Schema(description = "描述");
    private String description;

    /**
     * 执行时间
     */
    @Schema(description = "执行时间");
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss");
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss");
    private Date executeTime;

    /**
     * 浏览器
     */
    @Schema(description = "浏览器");
    private String browser;

    /**
     * 平台
     */
    @Schema(description = "平台");
    private String platform;

    /**
     * 版本
     */
    @Schema(description = "版本");
    private String version;

    /**
     * 用例类别
     */
    @Schema(description = "用例类别");
    private String caseCategory;

    /**
     * 用例类型
     */
    @Schema(description = "用例类型");
    private String testType;

    /**
     * 前提条件
     */
    @Schema(description = "前提条件");
    private String testCondition;

    /**
     * 环境
     */
    @Schema(description = "环境");
    private String env;

    /**
     * 外部id
     */
    @Schema(description = "外部id");
    private String externalLinkId;

    /**
     * 最后运行状态
     */
    @Schema(description = "最后运行状态");
    private Integer lastRunStatus;

    /**
     * 模块
     */
    @Schema(description = "模块");
    private String module;

    /**
     * 测试设备
     */
    @Schema(description = "测试设备");
    private String testDevice;

    /**
     * 测试数据
     */
    @Schema(description = "测试数据");
    private String testData;

    /**
     * 测试方法
     */
    @Schema(description = "测试方法");
    private String testMethod;

    /**
     * test_status
     */
    @Schema(description = "test_status");
    private String testStatus;

    /**
     * 测试执行状态
     */
    @Schema(description = "测试执行状态");
    private Integer runStatus;

    /**
     * reportTo
     */
    @Schema(description = "reportTo");
    private String reportTo;

    /**
     * testcase_expand
     */
    @Schema(description = "testcase_expand");
    private String testcaseExpand;

    /**
     * remarks
     */
    @Schema(description = "remarks");
    private String remarks;

    @Schema(description = "测试用例步骤集合");
    @TableField(exist = false);
    @DbIgnore
    private List<TestCaseStep> testCaseStepList;

}
}
}
