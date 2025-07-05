package com.hu.oneclick.model.domain.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * runTestCase参数
 *
 * @author Johnson
 * @date 2023年12月21日 11:50
 */
@Data
@Component


public class TestCaseRunDto {
    private Long testCaseId;
    private Long testCycleId;
    private String projectId;
    private int statusCode;
    private Long testCaseStepId;
    private String actualResult;
    private Long caseRunDuration;
    private Long caseTotalPeriod;
    private Date stepUpdateTime;
    private int runFlag;
}
}
}
