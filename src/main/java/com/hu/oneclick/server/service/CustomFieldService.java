package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.FieldRadio;
import com.hu.oneclick.model.domain.FieldRichText;
import com.hu.oneclick.model.domain.FieldText;

/**
 * @author qingyang
 */
public interface CustomFieldService {

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
}
