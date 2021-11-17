package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.CustomField;
import com.hu.oneclick.model.domain.FieldDropDown;
import com.hu.oneclick.model.domain.FieldRadio;
import com.hu.oneclick.model.domain.FieldRichText;
import com.hu.oneclick.model.domain.FieldText;
import com.hu.oneclick.model.domain.dto.CustomFieldDto;

import java.util.List;

/**
 * @author qingyang
 */
public interface CustomFieldService {

    //custom begin

    Resp<List<CustomField>> queryCustomList(CustomField customField);

    //custom end


    //radio begin

    Resp<FieldRadio> queryFieldRadioById(String customFieldId);

    Resp<String> addCustomRadio(FieldRadio fieldRadio);

    Resp<String> updateCustomRadio(FieldRadio fieldRadio);

    Resp<String> deleteCustomRadio(String id);

    //radio end

    //text begin

    Resp<FieldText> queryFieldTextById(String customFieldId);

    Resp<String> addCustomText(FieldText fieldText);

    Resp<String> updateCustomText(FieldText fieldText);

    Resp<String> deleteCustomText(String id);

    Resp<String> addCustomRichText(FieldRichText fieldRichText);

    Resp<String> updateCustomRichText(FieldRichText fieldRichText);

    //text end

    //drop down begin

    Resp<FieldDropDown> queryFieldDropDownById(String customFieldId);

    Resp<String> addCustomDropDown(FieldDropDown fieldDropDown);

    Resp<String> updateCustomDropDown(FieldDropDown fieldDropDown);

    Resp<String> deleteCustomDropDown(String customFieldId);

    //drop down end

    Resp<List< Object>> getAllCustomField(CustomFieldDto customField);
}
