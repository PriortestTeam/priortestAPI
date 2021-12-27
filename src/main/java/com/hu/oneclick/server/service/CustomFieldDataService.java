package com.hu.oneclick.server.service;

import com.hu.oneclick.model.domain.CustomFieldData;
import com.hu.oneclick.model.domain.Feature;
import com.hu.oneclick.model.domain.Project;

import java.util.List;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/12/27
 * @since JDK 1.8.0
 */
public interface CustomFieldDataService {


    /** 插入项目自定义组件数据
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
}
