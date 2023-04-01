package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.CustomFieldDto;
import com.hu.oneclick.model.domain.dto.CustomFieldsDto;
import com.hu.oneclick.model.domain.vo.CustomFieldVo;
import com.hu.oneclick.model.domain.vo.CustomFileldLinkVo;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 自定义字段表 服务类
 * </p>
 *
 * @author vince
 * @since 2022-12-13
 */
public interface CustomFieldsService {


    Resp<List<CustomFieldVo>>  queryCustomList(CustomFieldDto customFieldDto);
    Resp<String> add(CustomFieldVo customFieldVo);

    Resp<String> update(CustomFieldVo customFieldVo);

    Resp<String> delete(Set<Long> customFieldIds);

    Resp<List<CustomFileldLinkVo>> getAllCustomList(CustomFieldDto customFieldDto);

    Resp<List<CustomFileldLinkVo>> getDropDownBox(CustomFieldDto customFieldDto);

    Resp<String> updateValueDropDownBox(CustomFieldsDto customFieldsDto);
}
