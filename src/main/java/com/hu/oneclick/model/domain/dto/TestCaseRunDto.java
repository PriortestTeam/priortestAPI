package com.hu.oneclick.model.domain.dto;

import lombok.Data;

/**
 * runTestCase参数
 *
 * @author Johnson
 * @date 2023年12月21日 11:50
 */
@Data
public class TestCaseRunDto {
    private Long testCaseId;
    private Long testCycleId;
    private String projectId;
    private int statusCode;
    private Long testCaseStepId;
}
