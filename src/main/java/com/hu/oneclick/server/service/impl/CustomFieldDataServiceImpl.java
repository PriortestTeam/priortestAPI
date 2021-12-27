package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.constant.FieldConstant;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.CustomFieldDataDao;
import com.hu.oneclick.model.domain.CustomFieldData;
import com.hu.oneclick.model.domain.Feature;
import com.hu.oneclick.model.domain.Project;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.TestCycle;
import com.hu.oneclick.model.domain.dto.AuthLoginUser;
import com.hu.oneclick.server.service.CustomFieldDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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

}
