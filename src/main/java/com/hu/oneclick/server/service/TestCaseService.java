package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.Feature;
import com.hu.oneclick.model.domain.TestCase;
import com.hu.oneclick.model.domain.dto.ImportTestCaseDto;
import com.hu.oneclick.model.domain.dto.LeftJoinDto;
import com.hu.oneclick.model.domain.dto.TestCaseDto;
import com.hu.oneclick.model.domain.dto.TestCycleDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author qingyang
 */
public interface TestCaseService {

    Resp< List<LeftJoinDto>> queryTitles(String projectId, String title);

    Resp<TestCase> queryById(String id);

    Resp<List<TestCase>> queryList(TestCaseDto testCase);

    Resp<String> insert(TestCase testCase);

    Resp<String> update(TestCase testCase);

    Resp<String> delete(String id);

    Resp<Feature> queryTestNeedByFeatureId(String featureId);

    /**
     * excel导入测试用例
     * @param file
     * @param param
     * @return
     */
    Resp<ImportTestCaseDto> importTestCase(MultipartFile file, String param);

    /** 添加测试用例
     * @Param: [testCase]
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2021/12/1
     * @param testCycleDto
     */
    Resp<String> addTestCase(TestCycleDto testCycleDto);

    /** 更新action
     * @Param: [testCaseId]
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2021/12/1
     */
    Resp<List<TestCase>> updateAction(List<String> testCaseId, String actionType, String testCycleId);
}
