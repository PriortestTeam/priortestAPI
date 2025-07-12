
package com.hu.oneclick.model.domain.dto;

import lombok.Data;

@Data
public class ProjectProgressDto {
    private String projectId;
    private String projectName;
    private int totalTestCases;
    private int passedTestCases;
    private double completionPercentage;
    private int totalIssues;
    private int openIssues;
    private int resolvedIssues;
    private String status;
    private String lastUpdateTime;
}
