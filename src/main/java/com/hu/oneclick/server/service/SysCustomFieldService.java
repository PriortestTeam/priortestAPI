package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.SysCustomFieldVo;

import java.util.List;

public interface SysCustomFieldService {

    Resp<List<SysCustomFieldVo>> querySysCustomFields();

    Resp<String> updateSysCustomFields(SysCustomFieldVo sysCustomFieldVo);

    Resp<SysCustomFieldVo> getSysCustomField(String fieldName);


    /** 新建项目时获取所有系统字段
     * @Param: []
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2021/11/17
     */
    Resp<List<SysCustomFieldVo>> getAllSysCustomField();


    

}
