package com.hu.oneclick.model.domain.dto;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "测试用例DTO"))
//@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TestCaseSaveDto implements Serializable {

    private static final long serialVersionUID = -806606802497649838L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(description = "主键id"))
    private Long id;

    /**
     * 关联项目id
     */
    @Schema(description = "关联项目id"))
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    /**
     * 名称
     */
    @Schema(description = "名称"))
    @NotBlank(message = "名称不能为空")
    private String title;

    /**
     * 优先级
     */
    @Schema(description = "优先级"))
    @NotBlank(message = "优先级不能为空")
    private String priority;

    /**
     * 故事id
     */
    @Schema(description = "故事id"))
    @NotBlank(message = "故事id不能为空")
    private String feature;

    /**
     * 描述
     */
    @Schema(description = "描述"))
    private String description;

    /**
     * 执行时间
     */
    @Schema(description = "执行时间"))
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date executeTime;

    /**
     * 浏览器
     */
    @Schema(description = "浏览器"))
    private String browser;

    /**
     * 平台
     */
    @Schema(description = "平台"))
    private String platform;

    /**
     * 版本
     */
    @Schema(description = "版本"))
    private String version;

    /**
     * 用例类别
     */
    @Schema(description = "用例类别"))
    private String caseCategory;

    /**
     * 用例类型
     */
    @Schema(description = "用例类型"))
    private String testType;

    /**
     * 前提条件
     */
    @Schema(description = "前提条件"))
    private String testCondition;

    /**
     * 环境
     */
    @Schema(description = "环境"))
    private String env;

    /**
     * 外部id
     */
    @Schema(description = "外部id"))
    private String externalLinkId;

    /**
     * 最后运行状态
     */
    @Schema(description = "最后运行状态"))
    private Integer lastRunStatus;

    /**
     * 模块
     */
    @NotBlank(message = "模块不能为空")
    @Schema(description = "模块"))
    private String module;

    /**
     * 测试设备
     */
    @Schema(description = "测试设备"))
    private String testDevice;

    /**
     * 测试数据
     */
    @Schema(description = "测试数据"))
    private String testData;

    /**
     * 测试方法
     */
    @Schema(description = "测试方法"))
    private String testMethod;

    /**
     * test_status
     */
    @Schema(description = "test_status"))
    private String testStatus;

    /**
     * 测试执行状态
     */
    @Schema(description = "测试执行状态"))
    private Integer runStatus;

    /**
     * testcase_expand
     */
    @Schema(description = "testcase_expand"))
    private String testcaseExpand;

    /**
     * remarks
     */
    @Schema(description = "remarks"))
    private String remarks;

    /**
     * 修改者
     */
    @Schema(description = "修改者"))
    private String updateUserId;

    /**
     * 创建者
     */
    @Schema(description = "创建者"))
    private Long userId;

    /**
     * 自定义字段值
     */
    @Schema(description = "自定义字段值"))
    private JSONObject customFieldDatas;

    /**
     * reportTo
     */
    @Schema(description = "reportTo"))
    private String reportTo;

}
