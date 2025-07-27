
package com.hu.oneclick.model.domain.dto;

import lombok.Data;

/**
 * 测试用例执行详情DTO
 */
@Data
public class TestCaseExecutionDetailDto {
    private Long testCaseId;
    private Long runCaseId;
    private String testCaseTitle;
    private String testCaseVersion;
    private Integer runStatus;
    private String runStatusText;
    private Integer runCount;
}
