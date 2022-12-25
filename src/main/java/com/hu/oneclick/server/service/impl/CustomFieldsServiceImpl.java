package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.util.SnowFlakeUtil;
import com.hu.oneclick.dao.CustomFieldsDao;
import com.hu.oneclick.dao.CustomFileldLinkDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.CustomField;
import com.hu.oneclick.model.domain.CustomFields;
import com.hu.oneclick.model.domain.CustomFileldLink;
import com.hu.oneclick.model.domain.dto.CustomFieldDto;
import com.hu.oneclick.model.domain.vo.CustomFieldVo;
import com.hu.oneclick.server.service.CustomFieldsService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 自定义字段表 服务实现类
 * </p>
 *
 * @author vince
 * @since 2022-12-13
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class CustomFieldsServiceImpl implements CustomFieldsService {

    @NonNull
    private final CustomFieldsDao customFieldsDao;
    @NonNull
    private final CustomFileldLinkDao customFileldLinkDao;
    @NonNull
    private final JwtUserServiceImpl jwtUserServiceImpl;

    @Override
    public Resp<List<CustomFields>>queryCustomList(CustomFieldDto customFieldDto) {
        CustomFields customField = new CustomFields();

        customField.setType(customFieldDto.getType());
        customField.setCreateUser(Long.parseLong(jwtUserServiceImpl.getMasterId()));
        customField.setProjectId(NumberUtils.toLong(customFieldDto.getProjectId(),0 ));
        List<CustomFields> customFields = customFieldsDao.queryCustomList(customField);;

        return new Resp.Builder<List<CustomFields>>().setData(customFields).total(customFields).ok();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> add(CustomFieldVo customFieldVo) {

        CustomFields customField = new CustomFields();
        BeanUtils.copyProperties(customFieldVo, customField);
        customField.setCreateUser(Long.parseLong(jwtUserServiceImpl.getMasterId()));
        CustomFieldVo.Attributes attributes = customFieldVo.getAttributes();
        BeanUtils.copyProperties(attributes, customField);

        customField.setCustomFieldId(SnowFlakeUtil.getFlowIdInstance().nextId());
        int insertSelective = customFieldsDao.insertSelective(customField);
        if (insertSelective > 0) {
            List<CustomFileldLink>  customFileldLinkList = getCustomFileldLinkList(customFieldVo, customField);
            int insertBatch = customFileldLinkDao.insertBatch(customFileldLinkList);
            insertSelective += insertBatch;
        }
        return Result.addResult(insertSelective >= 1 ? 1 : 0);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> update(CustomFieldVo customFieldVo) {
        CustomFields customField = new CustomFields();
        BeanUtils.copyProperties(customFieldVo, customField);
        customField.setModifyUser(Long.parseLong(jwtUserServiceImpl.getMasterId()));
        CustomFieldVo.Attributes attributes = customFieldVo.getAttributes();
        BeanUtils.copyProperties(attributes, customField);
        int row = customFieldsDao.update(customField);

        if (row > 0) {
            List<CustomFileldLink>  customFileldLinkList = getCustomFileldLinkList(customFieldVo, customField);
            // 先根据customFieldsId删除数据再新增
            customFileldLinkDao.delete(new CustomFileldLink(customField.getCustomFieldId()));
            int insertBatch = customFileldLinkDao.insertBatch(customFileldLinkList);
            row += insertBatch;
        }
        return Result.updateResult(row >= 1 ? 1 : 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> delete(Set<Long> customFieldIds) {
        int del = customFieldsDao.deleteBatchByKey(customFieldIds);
         del += customFileldLinkDao.deleteBatchByCustomFieldId(customFieldIds);
        return Result.updateResult(del >= 1 ? 1 : 0);
    }



    private List<CustomFileldLink> getCustomFileldLinkList(CustomFieldVo customFieldVo, CustomFields customField) {
        List<CustomFileldLink> customFileldLinkList = Optional.ofNullable(customFieldVo.getComponentAttributes())
                .orElse(Lists.newArrayList()).stream().map(item ->
                        new CustomFileldLink(SnowFlakeUtil.getFlowIdInstance().nextId(),
                                customField.getCustomFieldId(),
                                item.getDefaultValue(),
                                item.getScope(),
                                item.getMandatory() ? 1 : 0,
                                item.getScopeId(),
                                item.getScopeCn()
                        )).collect(Collectors.toList());

        return customFileldLinkList;
    }

}
