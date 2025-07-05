package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.CustomField;
import com.hu.oneclick.model.entity.FieldDropDown;
import com.hu.oneclick.model.entity.FieldRadio;
import com.hu.oneclick.model.entity.FieldRichText;
import com.hu.oneclick.model.entity.FieldText;
import com.hu.oneclick.model.domain.dto.CustomFieldDto;

import java.util.List;

/**
 * @author qingyang
 */
public interface CustomFieldService {

    //custom begin

    /** 获取用户定义字段
     * @Param: [customField]
     * @return: com.hu.oneclick.model.base.Resp<java.util.List<com.hu.oneclick.model.entity.CustomField>>
     * @Author: MaSiyi
     * @Date: 2021/11/18
     */
    Resp<List<CustomField>> queryCustomList(CustomField customField);

    //custom end


    //radio begin 单选框

    Resp<FieldRadio> queryFieldRadioById(String customFieldId);

    Resp<String> addCustomRadio(FieldRadio fieldRadio);

    Resp<String> updateCustomRadio(FieldRadio fieldRadio);

    Resp<String> deleteCustomRadio(String id);

    //radio end

    //text begin 文本框

    Resp<FieldText> queryFieldTextById(String customFieldId);

    Resp<String> addCustomText(FieldText fieldText);

    Resp<String> updateCustomText(FieldText fieldText);

    Resp<String> deleteCustomText(String id);

    Resp<String> addCustomRichText(FieldRichText fieldRichText);

    Resp<String> updateCustomRichText(FieldRichText fieldRichText);

    //text end

    //drop down begin 下拉框

    Resp<FieldDropDown> queryFieldDropDownById(String customFieldId);

    Resp<String> addCustomDropDown(FieldDropDown fieldDropDown);

    Resp<String> updateCustomDropDown(FieldDropDown fieldDropDown);

    Resp<String> deleteCustomDropDown(String customFieldId);

    //drop down end

    /** 新建时获取所有用户字段
     * @Param: []
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2021/11/17
     */
    Resp<List< Object>> getAllCustomField(CustomFieldDto customField);
}
