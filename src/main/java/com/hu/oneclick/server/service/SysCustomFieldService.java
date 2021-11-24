package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.SysCustomFieldVo;

import java.util.List;

public interface SysCustomFieldService {


    /** 查询所有系统字段加上用户自己添加的字段
     * @Param: []
     * @return: com.hu.oneclick.model.base.Resp<java.util.List<com.hu.oneclick.model.domain.dto.SysCustomFieldVo>>
     * @Author:
     * @Date: 2021/11/18
     */
    Resp<List<SysCustomFieldVo>> querySysCustomFields();

    Resp<String> updateSysCustomFields(SysCustomFieldVo sysCustomFieldVo);


    /** 根据字段名显示用户和系统字段
     * @Param: [fieldName]
     * @return: com.hu.oneclick.model.base.Resp<com.hu.oneclick.model.domain.dto.SysCustomFieldVo>
     * @Author:
     * @Date: 2021/11/18
     */
    Resp<SysCustomFieldVo> getSysCustomField(String fieldName);


    /** 新建项目时获取所有系统字段
     * @Param: []
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2021/11/17
     */
    Resp<List<SysCustomFieldVo>> getAllSysCustomField();


    

}
