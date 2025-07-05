package com.hu.oneclick.model.domain.dto;

import lombok.Data;

@Data


public class ExecuteRunTestCaseDto {
    private String testCaseId;
    private String testCycleId;
    private String projectId;
    private boolean runCountIndicator;
}
}
