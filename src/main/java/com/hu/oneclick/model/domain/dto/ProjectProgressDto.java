
package com.hu.oneclick.model.domain.dto;

import lombok.Data;
import java.util.Date;

@Data
public class ProjectProgressDto {
    private String projectId;
    private String projectTitle;
    private Double completionRate; // 完成度百分比
    private Integer totalTestCases; // 总测试用例数
    private Integer completedTestCases; // 已完成测试用例数
    private Integer totalTestCycles; // 总测试周期数
    private Integer completedTestCycles; // 已完成测试周期数
    private Date startDate; // 项目开始日期
    private Date endDate; // 项目结束日期
    private String status; // 项目状态
}
