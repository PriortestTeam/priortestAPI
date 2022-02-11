package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.constant.FieldConstant;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.CustomFieldDataDao;
import com.hu.oneclick.dao.SysCustomFieldDao;
import com.hu.oneclick.dao.SysCustomFieldExpandDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.CustomFieldData;
import com.hu.oneclick.model.domain.Feature;
import com.hu.oneclick.model.domain.Issue;
import com.hu.oneclick.model.domain.Project;
import com.hu.oneclick.model.domain.SysCustomField;
import com.hu.oneclick.model.domain.SysCustomFieldExpand;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.TestCase;
import com.hu.oneclick.model.domain.TestCycle;
import com.hu.oneclick.model.domain.dto.AuthLoginUser;
import com.hu.oneclick.model.domain.dto.CustomFieldDto;
import com.hu.oneclick.server.service.CustomFieldDataService;
import com.hu.oneclick.server.service.CustomFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/12/27
 * @since JDK 1.8.0
 */
@Service
public class CustomFieldDataServiceImpl implements CustomFieldDataService {

    @Autowired
    private CustomFieldDataDao customFieldDataDao;
    @Autowired
    private JwtUserServiceImpl jwtUserService;
    @Autowired
    private CustomFieldService customFieldService;
    @Autowired
    private SysCustomFieldDao sysCustomFieldDao;
    @Autowired
    private SysCustomFieldExpandDao sysCustomFieldExpandDao;

    /**
     * 插入项目自定义组件数据
     *
     * @param customFieldDatas
     * @Param: [project]
     * @return: java.lang.Boolean
     * @Author: MaSiyi
     * @Date: 2021/12/27
     */
    @Override
    public Integer insertProjectCustomData(List<CustomFieldData> customFieldDatas, Project project) {
        int insertFlag = 0;

        AuthLoginUser userLoginInfo = jwtUserService.getUserLoginInfo();
        SysUser sysUser = userLoginInfo.getSysUser();
        for (CustomFieldData customFieldData : customFieldDatas) {

            CustomFieldData fieldData = this.insertCustomFieldData(sysUser, customFieldData);

            String projectId = project.getId();
            fieldData.setProjectId(projectId);
            fieldData.setScopeId(projectId);
            fieldData.setScope(FieldConstant.PROJECT);

            insertFlag = customFieldDataDao.insert(fieldData);

        }
        return insertFlag;
    }

    /**
     * 插入故事自定义字段数据
     *
     * @param customFieldDatas
     * @param feature
     * @Param: [customFieldDatas, feature]
     * @return: java.lang.Integer
     * @Author: MaSiyi
     * @Date: 2021/12/27
     */
    @Override
    public Integer insertFeatureCustomData(List<CustomFieldData> customFieldDatas, Feature feature) {

        AuthLoginUser userLoginInfo = jwtUserService.getUserLoginInfo();
        SysUser sysUser = userLoginInfo.getSysUser();

        int insertFlag = 0;
        for (CustomFieldData customFieldData : customFieldDatas) {

            CustomFieldData fieldData = this.insertCustomFieldData(sysUser, customFieldData);

            String projectId = feature.getProjectId();
            fieldData.setProjectId(projectId);
            fieldData.setScopeId(feature.getId());
            fieldData.setScope(FieldConstant.FEATURE);

            insertFlag = customFieldDataDao.insert(fieldData);

        }
        return insertFlag;
    }

    /**
     * 插入测试周期自定义字段数据
     *
     * @param customFieldDatas
     * @param testCycle
     * @Param: [customFieldDatas, testCycle]
     * @return: java.lang.Integer
     * @Author: MaSiyi
     * @Date: 2021/12/27
     */
    @Override
    public Integer insertTestCycleCustomData(List<CustomFieldData> customFieldDatas, TestCycle testCycle) {
        AuthLoginUser userLoginInfo = jwtUserService.getUserLoginInfo();
        SysUser sysUser = userLoginInfo.getSysUser();

        int insertFlag = 0;
        for (CustomFieldData customFieldData : customFieldDatas) {

            CustomFieldData fieldData = this.insertCustomFieldData(sysUser, customFieldData);

            String projectId = testCycle.getProjectId();
            fieldData.setProjectId(projectId);
            fieldData.setScopeId(testCycle.getId());
            fieldData.setScope(FieldConstant.TESTCYCLE);

            insertFlag = customFieldDataDao.insert(fieldData);

        }
        return insertFlag;
    }

    /**
     * 插入测试用例自定义字段数据
     *
     * @param customFieldDatas
     * @param testCases
     * @Param: [customFieldDatas, testCases]
     * @return: void
     * @Author: MaSiyi
     * @Date: 2021/12/28
     */
    @Override
    public void insertTestCaseCustomData(List<CustomFieldData> customFieldDatas, List<TestCase> testCases) {
        AuthLoginUser userLoginInfo = jwtUserService.getUserLoginInfo();
        SysUser sysUser = userLoginInfo.getSysUser();
        for (CustomFieldData customFieldData : customFieldDatas) {
            CustomFieldData fieldData = this.insertCustomFieldData(sysUser, customFieldData);
            for (TestCase testCase : testCases) {
                String projectId = testCase.getProjectId();
                fieldData.setProjectId(projectId);
                fieldData.setScopeId(testCase.getId());
                fieldData.setScope(FieldConstant.TESTCASE);
            }

            customFieldDataDao.insert(fieldData);

        }
    }

    /**
     * 插入缺陷自定义字段数据
     *
     * @param customFieldDatas
     * @param issue
     * @Param: [customFieldDatas, issue]
     * @return: java.lang.Integer
     * @Author: MaSiyi
     * @Date: 2021/12/28
     */
    @Override
    public Integer insertIssueCustomData(List<CustomFieldData> customFieldDatas, Issue issue) {
        AuthLoginUser userLoginInfo = jwtUserService.getUserLoginInfo();
        SysUser sysUser = userLoginInfo.getSysUser();

        int insertFlag = 0;
        for (CustomFieldData customFieldData : customFieldDatas) {

            CustomFieldData fieldData = this.insertCustomFieldData(sysUser, customFieldData);

            String projectId = issue.getProjectId();
            fieldData.setProjectId(projectId);
            fieldData.setScopeId(issue.getId());
            fieldData.setScope(FieldConstant.ISSUE);

            insertFlag = customFieldDataDao.insert(fieldData);

        }
        return insertFlag;
    }


    /**
     * 点击项目渲染自定义数据
     *
     * @param scopeId
     * @Param:
     * @return:
     * @Author: MaSiyi
     * @Date: 2021/12/28
     */
    @Override
    public List<CustomFieldData> projectRenderingCustom(String scopeId) {
        return customFieldDataDao.getAllByScopeIdAndScope(FieldConstant.PROJECT, scopeId);
    }

    /**
     * 点击故事渲染自定义数据
     *
     * @param id
     * @Param: [id]
     * @return: java.util.List<com.hu.oneclick.model.domain.CustomFieldData>
     * @Author: MaSiyi
     * @Date: 2021/12/28
     */
    @Override
    public List<CustomFieldData> featureRenderingCustom(String id) {
        return customFieldDataDao.getAllByScopeIdAndScope(FieldConstant.FEATURE, id);
    }

    /**
     * 点击测试周期渲染自定义数据
     *
     * @param id
     * @Param: [id]
     * @return: java.util.List<com.hu.oneclick.model.domain.CustomFieldData>
     * @Author: MaSiyi
     * @Date: 2021/12/28
     */
    @Override
    public List<CustomFieldData> testCycleRenderingCustom(String id) {
        return customFieldDataDao.getAllByScopeIdAndScope(FieldConstant.TESTCYCLE, id);
    }

    /**
     * 点击测试用例渲染自定义数据
     *
     * @param id
     * @Param: [id]
     * @return: java.util.List<com.hu.oneclick.model.domain.CustomFieldData>
     * @Author: MaSiyi
     * @Date: 2021/12/28
     */
    @Override
    public List<CustomFieldData> testCaseRenderingCustom(String id) {
        return customFieldDataDao.getAllByScopeIdAndScope(FieldConstant.TESTCASE, id);
    }

    /**
     * 点击缺陷渲染自定义数据
     *
     * @param id
     * @Param: [id]
     * @return: java.util.List<com.hu.oneclick.model.domain.CustomFieldData>
     * @Author: MaSiyi
     * @Date: 2021/12/28
     */
    @Override
    public List<CustomFieldData> issueRenderingCustom(String id) {
        return customFieldDataDao.getAllByScopeIdAndScope(FieldConstant.ISSUE, id);
    }

    /**
     * 插入公共值
     *
     * @Param: []
     * @return: com.hu.oneclick.model.domain.CustomFieldData
     * @Author: MaSiyi
     * @Date: 2021/12/27
     */
    private CustomFieldData insertCustomFieldData(SysUser sysUser, CustomFieldData customFieldDataInsert) {

        CustomFieldData fieldData = new CustomFieldData();

        fieldData.setUserId(sysUser.getId());

        fieldData.setCustomFieldId(customFieldDataInsert.getCustomFieldId());

        fieldData.setValueData(customFieldDataInsert.getValueData());
        fieldData.setCreateTime(new Date());
        fieldData.setIsDel(false);
        fieldData.setCreateUserId(sysUser.getId());

        return fieldData;

    }

    /**
     * 新建时获取所有用户字段
     *
     * @param customFieldDto
     * @Param: [customFieldDto]
     * @return: com.hu.oneclick.model.base.Resp<java.util.List < java.lang.Object>>
     * @Author: MaSiyi
     * @Date: 2021/12/29
     */
    @Override
    public Resp<List<Object>> getAllCustomField(CustomFieldDto customFieldDto) {
        return customFieldService.getAllCustomField(customFieldDto);
    }


    /**
     * 新建时获取所有系统字段
     *
     * @param scope
     * @Param: []
     * @return: com.hu.oneclick.model.base.Resp<java.util.List < java.lang.Object>>
     * @Author: MaSiyi
     * @Date: 2021/12/29
     */
    @Override
    public Resp<List<SysCustomField>> getAllSysCustomField(String scope) {
        //获取系统固定字段
        List<SysCustomField> sysCustomFields = sysCustomFieldDao.getAllSysCustomFieldByScope(scope);
        //将字段名称单独拿出来成为一个list
        List<String> collect = sysCustomFields.stream().map(SysCustomField::getFieldName).collect(Collectors.toList());
        //获取用户自己添加的自定义系统字段
        SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();
        String userId = sysUser.getId();
        String projectId = sysUser.getUserUseOpenProject().getProjectId();
        List<SysCustomFieldExpand> sysCustomFieldExpands = sysCustomFieldExpandDao.getAllSysCustomFieldExpand(userId, projectId);
        //将拓展表中字段名与系统表中匹配的值拿出来
        List<SysCustomFieldExpand> filterSysCustomFieldExpand = sysCustomFieldExpands.stream().filter(f -> collect.contains(f.getLinkSysCustomField())).collect(Collectors.toList());
        //拼装拓展值
        ArrayList<SysCustomField> newSysCustomFields = new ArrayList<>();
        for (SysCustomField sysCustomField : sysCustomFields) {
            for (SysCustomFieldExpand sysCustomFieldExpand : filterSysCustomFieldExpand) {
                if (sysCustomField.getFieldName().equals(sysCustomFieldExpand.getLinkSysCustomField())) {

                    sysCustomField.setDefaultValues(sysCustomField.getDefaultValues() + "," + sysCustomFieldExpand.getValues());
                }
            }
            newSysCustomFields.add(sysCustomField);
        }
        return new Resp.Builder<List<SysCustomField>>().setData(newSysCustomFields).ok();
    }

    /**
     * 查询该用户下的该项目数据
     *
     * @param scope
     * @param fieldName
     * @Param: [scope]
     * @return: java.util.List<com.hu.oneclick.model.domain.CustomField>
     * @Author: MaSiyi
     * @Date: 2022/1/4
     */
    @Override
    public List<CustomFieldData> findAllByUserIdAndScope(String scope, String fieldName) {
        SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();
        String projectId = sysUser.getUserUseOpenProject().getProjectId();
        String userId = sysUser.getId();

        return  customFieldDataDao.findAllByUserIdAndScope(projectId, userId, scope,fieldName);
    }
}
