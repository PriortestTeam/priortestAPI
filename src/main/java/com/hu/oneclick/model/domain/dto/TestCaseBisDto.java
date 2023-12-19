package com.hu.oneclick.model.domain.dto;


import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.Date;

@Data
@Component
public class TestCaseBisDto {
    @Resource
    com.hu.oneclick.model.domain.dto.testCaseDataRunDto testCaseRun;

    public void setTestCaseRun(int run, String status, Date updateTime, BigInteger createUserId) {
        testCaseRun.setRunCount(run);
        testCaseRun.setRunStatus(status);
        testCaseRun.setUpdateTime(updateTime);
        testCaseRun.setCreateUserId(createUserId);
    }

    @Resource
    com.hu.oneclick.model.domain.dto.testCaseDataCaseDto testCase;

    public void setTestCase(Long id, Long projectId, String title, String priority, String feature, String description, Date executeTime, String browser, String platform, String version, String caseCategory, String testType, String testCondition, String env, String externalLinkId, Integer lastRunStatus, String module, String testDevice, String testData, String testMethod, String testStatus, String reportTo, String testcaseExpand, String remarks) {
        testCase.setId(id);
        testCase.setProjectId(projectId);
        testCase.setTitle(title);
        testCase.setPriority(priority);
        testCase.setFeature(feature);
        testCase.setDescription(description);
        testCase.setExecuteTime(executeTime);
        testCase.setBrowser(browser);
        testCase.setPlatform(platform);
        testCase.setVersion(version);
        testCase.setCaseCategory(caseCategory);
        testCase.setTestType(testType);
        testCase.setTestCondition(testCondition);
        testCase.setEnv(env);
        testCase.setExternalLinkId(externalLinkId);
        testCase.setLastRunStatus(lastRunStatus);
        testCase.setModule(module);
        testCase.setTestDevice(testDevice);
        testCase.setTestData(testData);
        testCase.setTestMethod(testMethod);
        testCase.setTestStatus(testStatus);
        testCase.setReportTo(reportTo);
        testCase.setTestcaseExpand(testcaseExpand);
        testCase.setRemarks(remarks);
    }
}
