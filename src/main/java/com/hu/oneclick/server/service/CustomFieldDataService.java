package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.CustomFieldData;
import com.hu.oneclick.model.domain.Feature;
import com.hu.oneclick.model.domain.Issue;
import com.hu.oneclick.model.domain.Project;
import com.hu.oneclick.model.domain.SysCustomField;
import com.hu.oneclick.model.domain.TestCase;
import com.hu.oneclick.model.domain.TestCycle;
import com.hu.oneclick.model.domain.dto.CustomFieldDto;

import java.util.List;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/12/27
 * @since JDK 1.8.0
 */
public interface CustomFieldDataService {


    /**
     * 插入项目自定义字段数据
     *
     * @param customFieldDatas
     * @Param: [project]
     * @return: java.lang.Boolean
     * @Author: MaSiyi
     * @Date: 2021/12/27
     */
    Integer insertProjectCustomData(List<CustomFieldData> customFieldDatas, Project project);


    /**
     * 插入故事自定义字段数据
     *
     * @Param: [customFieldDatas, feature]
     * @return: java.lang.Integer
     * @Author: MaSiyi
     * @Date: 2021/12/27
     */
    Integer insertFeatureCustomData(List<CustomFieldData> customFieldDatas, Feature feature);

    /**
     * 插入测试周期自定义字段数据
     *
     * @Param: [customFieldDatas, testCycle]
     * @return: java.lang.Integer
     * @Author: MaSiyi
     * @Date: 2021/12/27
     */
    Integer insertTestCycleCustomData(List<CustomFieldData> customFieldDatas, TestCycle testCycle);

    /**
     * 插入测试用例自定义字段数据
     *
     * @Param: [customFieldDatas, testCases]
     * @return: void
     * @Author: MaSiyi
     * @Date: 2021/12/28
     */
    void insertTestCaseCustomData(List<CustomFieldData> customFieldDatas, List<TestCase> testCases);

    /**
     * 插入缺陷自定义字段数据
     *
     * @Param: [customFieldDatas, issue]
     * @return: java.lang.Integer
     * @Author: MaSiyi
     * @Date: 2021/12/28
     */
    Integer insertIssueCustomData(List<CustomFieldData> customFieldDatas, Issue issue);

    /**
     * 点击项目渲染自定义数据
     *
     * @param scopeId
     * @Param:
     * @return:
     * @Author: MaSiyi
     * @Date: 2021/12/28
     */
    List<CustomFieldData> projectRenderingCustom(String scopeId);

    /**
     * 点击故事渲染自定义数据
     *
     * @Param: [id]
     * @return: java.util.List<com.hu.oneclick.model.domain.CustomFieldData>
     * @Author: MaSiyi
     * @Date: 2021/12/28
     */
    List<CustomFieldData> featureRenderingCustom(String id);

    /**
     * 点击测试周期渲染自定义数据
     *
     * @Param: [id]
     * @return: java.util.List<com.hu.oneclick.model.domain.CustomFieldData>
     * @Author: MaSiyi
     * @Date: 2021/12/28
     */
    List<CustomFieldData> testCycleRenderingCustom(String id);

    /**
     * 点击测试用例渲染自定义数据
     *
     * @Param: [id]
     * @return: java.util.List<com.hu.oneclick.model.domain.CustomFieldData>
     * @Author: MaSiyi
     * @Date: 2021/12/28
     */
    List<CustomFieldData> testCaseRenderingCustom(String id);

    /**
     * 点击缺陷渲染自定义数据
     *
     * @Param: [id]
     * @return: java.util.List<com.hu.oneclick.model.domain.CustomFieldData>
     * @Author: MaSiyi
     * @Date: 2021/12/28
     */
    List<CustomFieldData> issueRenderingCustom(String id);


    /**
     * 新建时获取所有用户字段
     *
     * @Param: [customFieldDto]
     * @return: com.hu.oneclick.model.base.Resp<java.util.List < java.lang.Object>>
     * @Author: MaSiyi
     * @Date: 2021/12/29
     */
    Resp<List<Object>> getAllCustomField(CustomFieldDto customFieldDto);

    /** 新建时获取所有系统字段
     * @Param: []
     * @return: com.hu.oneclick.model.base.Resp<java.util.List<java.lang.Object>>
     * @Author: MaSiyi
     * @Date: 2021/12/29
     * @param scope
     */
    Resp<List<SysCustomField>> getAllSysCustomField(String scope);

    /** 查询该用户下的该项目数据
     * @Param: [scope]
     * @return: java.util.List<com.hu.oneclick.model.domain.CustomField>
     * @Author: MaSiyi
     * @Date: 2022/1/4
     */
    List<CustomFieldData> findAllByUserIdAndScope(String scope, String fieldName);
}
