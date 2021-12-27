package com.hu.oneclick.server.service;

import com.alibaba.fastjson.JSONObject;

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
     * @param project
     */
    Integer insertProjectCustomData(JSONObject project);


}
