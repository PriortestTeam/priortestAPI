package com.hu.oneclick.server.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Maps;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.util.SnowFlakeUtil;
import com.hu.oneclick.dao.CustomFieldsDao;
import com.hu.oneclick.dao.CustomFileldLinkDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.CustomFields;
import com.hu.oneclick.model.domain.CustomFileldLink;
import com.hu.oneclick.model.domain.dto.CustomFieldDto;
import com.hu.oneclick.model.domain.dto.CustomFieldsDto;
import com.hu.oneclick.model.domain.vo.ComponentAttributesVo;
import com.hu.oneclick.model.domain.vo.CustomFieldVo;
import com.hu.oneclick.model.domain.vo.CustomFileldLinkVo;
import com.hu.oneclick.server.service.CustomFieldsService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
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
    public Resp<List<CustomFieldVo>> queryCustomList(CustomFieldDto customFieldDto) {
        CustomFields customField = new CustomFields();
        customField.setProjectId(NumberUtils.toLong(customFieldDto.getProjectId()));
        List<CustomFields> customFields = customFieldsDao.queryCustomList(customField);
//        PageInfo<CustomFields> pageInfo = new PageInfo<>(customFields);
        Set<Long> customFieldIds = customFields.stream().map(CustomFields::getCustomFieldId).collect(Collectors.toSet());
        List<CustomFileldLink> customFileldLinkList = Lists.newArrayList();
        Map<Long, List<CustomFileldLink>> listMap = Maps.newHashMap();
        if (!ObjectUtils.isEmpty(customFieldIds)) {
            customFileldLinkList = customFileldLinkDao.findByCustomFieldIds(customFieldIds);
            listMap = customFileldLinkList.stream().collect(Collectors.groupingBy(CustomFileldLink::getCustomFieldId));
        }

        List<CustomFieldVo> resList = Lists.newArrayList();
        for (CustomFields field : customFields) {
            CustomFieldVo customFieldVo = new CustomFieldVo();
            BeanUtils.copyProperties(field, customFieldVo);

            CustomFieldVo.Attributes attributes = new CustomFieldVo.Attributes();
            BeanUtils.copyProperties(field, attributes);
            customFieldVo.setAttributes(attributes);

            List<ComponentAttributesVo> componentAttributes = Lists.newArrayList();

            List<CustomFileldLink> fileldLinks = listMap.get(field.getCustomFieldId());
            if (!ObjectUtils.isEmpty(fileldLinks)) {
                for (CustomFileldLink fileldLink : fileldLinks) {
                    ComponentAttributesVo componentAttributesVo = new ComponentAttributesVo();
                    BeanUtils.copyProperties(fileldLink, componentAttributesVo);
                    componentAttributesVo.setMandatory(fileldLink.getMandatory() == 1);
                    componentAttributes.add(componentAttributesVo);
                }
            }
            customFieldVo.setComponentAttributes(componentAttributes);
            resList.add(customFieldVo);
        }

        return new Resp.Builder<List<CustomFieldVo>>().setData(resList).total(customFields).ok();
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
        int insertSelective = customFieldsDao.insert(customField);
        if (insertSelective > 0) {
            List<CustomFileldLink> customFileldLinkList = getCustomFileldLinkList(customFieldVo, customField);
            int insertBatch = customFileldLinkDao.insertBatch(customFileldLinkList);
            insertSelective += insertBatch;
        }
        return Result.addResult(insertSelective >= 1 ? 1 : 0);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> update(CustomFieldVo customFieldVo) {

        CustomFields customField = new CustomFields();
        customField.setCustomFieldId(customFieldVo.getCustomFieldId());
        int count = customFieldsDao.selectCount(new LambdaQueryWrapper<CustomFields>().eq(CustomFields::getCustomFieldId, customFieldVo.getCustomFieldId())).intValue();
        if (count == 0) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "customFieldId不存在！");
        }
        BeanUtils.copyProperties(customFieldVo, customField);

        customField.setModifyUser(Long.parseLong(jwtUserServiceImpl.getMasterId()));
        CustomFieldVo.Attributes attributes = customFieldVo.getAttributes();
        BeanUtils.copyProperties(attributes, customField);
        int row = customFieldsDao.updateByPrimaryKeySelective(customField);

        if (row > 0) {
            List<CustomFileldLink> customFileldLinkList = getCustomFileldLinkList(customFieldVo, customField);
            // 先根据customFieldsId删除数据再新增
            customFileldLinkDao.delete(new LambdaQueryWrapper<CustomFileldLink>().eq(CustomFileldLink::getCustomFieldId, customField.getCustomFieldId()));
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
        return Result.deleteResult(del >= 1 ? 1 : 0);
    }

    @Override
    public Resp<List<CustomFileldLinkVo>> getAllCustomList(CustomFieldDto customFieldDto) {
        List<CustomFileldLinkVo> list = customFieldsDao.getAllCustomList(customFieldDto);
        return new Resp.Builder<List<CustomFileldLinkVo>>().setData(list).ok();
    }

    @Override
    public List<CustomFileldLinkVo> getAllCustomListByScopeId(Long scopeId) {

        return customFieldsDao.getAllCustomListByScopeId(scopeId);
    }

    @Override
    public Resp<List<CustomFileldLinkVo>> getDropDownBox(CustomFieldDto customFieldDto) {
        List<CustomFileldLinkVo> dropDownBox = customFieldsDao.getDropDownBox(customFieldDto);
        return new Resp.Builder<List<CustomFileldLinkVo>>().setData(dropDownBox).ok();
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
                                item.getScopeNameCn()
                        )).collect(Collectors.toList());

        return customFileldLinkList;
    }

    @Override
    public Resp<String> updateValueDropDownBox(CustomFieldsDto customFieldsDto) {
        // 检验参数
        updateValidParam(customFieldsDto);
        customFieldsDto.setUpdateTime(new Date());
        customFieldsDto.setModifyUserId(Long.valueOf(jwtUserServiceImpl.getUserLoginInfo().getSysUser().getId()));
        int row = customFieldsDao.updateValueDropDownBox(customFieldsDto);
        return Result.updateResult(row >= 1 ? 1 : 0);
    }

    private void updateValidParam(CustomFieldsDto customFieldsDto) {
        String fieldType = customFieldsDto.getFieldType();
        CustomFields entity = this.customFieldsDao.getByCustomFieldId(customFieldsDto.getCustomFieldId());
        if (null == entity) {
            throw new BizException(SysConstantEnum.PARAMETER_ABNORMAL.getCode(), "customFieldId不存在");
        }

        if (!fieldType.equals(entity.getFieldType())) {
            throw new BizException(SysConstantEnum.PARAMETER_ABNORMAL.getCode(), "fieldType与请求修改记录不符合");
        }

        if (CustomFieldsDto.NOT_PARENT_LIST_ID.contains(fieldType)) {
            JSONObject jsonObject = JSONObject.parseObject(customFieldsDto.getPossibleValue());
            Object others = jsonObject.get("others");
            if (null != others) {
                Object parentListId = JSONObject.parseObject(JSONObject.toJSONString(others)).get("parentListId");
                if (null != parentListId) {
                    throw new BizException(SysConstantEnum.PARAMETER_ABNORMAL.getCode(), "parentListId不应该出现");
                }
            }
        }

        if (CustomFieldsDto.NEED_PARENT_LIST_ID.contains(fieldType)) {
            JSONObject jsonObject = JSONObject.parseObject(customFieldsDto.getPossibleValue());
            Object others = jsonObject.get("others");
            if (null == others) {
                throw new BizException(SysConstantEnum.PARAMETER_ABNORMAL.getCode(), "possibleValue格式不对。因为缺少parentListId");
            } else {
                Object parentListId = JSONObject.parseObject(JSONObject.toJSONString(others)).get("parentListId");
                if (null == parentListId) {
                    throw new BizException(SysConstantEnum.PARAMETER_ABNORMAL.getCode(), "possibleValue格式不对。因为缺少parentListId");
                }
            }
        }
    }
}
