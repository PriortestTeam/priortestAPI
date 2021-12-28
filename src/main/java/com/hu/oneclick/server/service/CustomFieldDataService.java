package com.hu.oneclick.server.service;

import com.hu.oneclick.model.domain.CustomFieldData;
import com.hu.oneclick.model.domain.Feature;
import com.hu.oneclick.model.domain.Project;
import com.hu.oneclick.model.domain.TestCase;
import com.hu.oneclick.model.domain.TestCycle;

import java.util.List;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/12/27
 * @since JDK 1.8.0
 */
public interface CustomFieldDataService {


    /** 插入项目自定义字段数据
     * @Param: [project]
     * @return: java.lang.Boolean
     * @Author: MaSiyi
     * @Date: 2021/12/27
     * @param customFieldDatas
     */
    Integer insertProjectCustomData(List<CustomFieldData> customFieldDatas, Project project);


    /** 插入故事自定义字段数据
     * @Param: [customFieldDatas, feature]
     * @return: java.lang.Integer
     * @Author: MaSiyi
     * @Date: 2021/12/27
     */
    Integer insertFeatureCustomData(List<CustomFieldData> customFieldDatas, Feature feature);

    /** 插入测试周期自定义字段数据
     * @Param: [customFieldDatas, testCycle]
     * @return: java.lang.Integer
     * @Author: MaSiyi
     * @Date: 2021/12/27
     */
    Integer insertTestCycleCustomData(List<CustomFieldData> customFieldDatas, TestCycle testCycle);

    /** 插入测试用例自定义字段数据
     * @Param: [customFieldDatas, testCases]
     * @return: void
     * @Author: MaSiyi
     * @Date: 2021/12/28
     */
    void insertTestCaseCustomData(List<CustomFieldData> customFieldDatas, List<TestCase> testCases);
}
