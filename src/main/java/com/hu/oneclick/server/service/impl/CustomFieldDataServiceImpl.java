package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.dao.CustomFieldDataDao;
import com.hu.oneclick.model.domain.CustomFieldData;
import com.hu.oneclick.server.service.CustomFieldDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Integer insertProjectCustomData(List<CustomFieldData> customFieldDatas) {
//        customFieldDatas.f
//        customFieldDataDao.insert();
        return null;
    }
}
