package com.hu.oneclick.server.service.impl;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Maps;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.util.SnowFlakeUtil;
import com.hu.oneclick.dao.CustomFieldsDao;
import com.hu.oneclick.dao.CustomFileldLinkDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.dto.CustomFieldDto;
import com.hu.oneclick.model.domain.dto.CustomFieldPossBileDto;
import com.hu.oneclick.model.domain.dto.CustomFieldsDto;
import com.hu.oneclick.model.domain.vo.ComponentAttributesVo;
import com.hu.oneclick.model.domain.vo.CustomFieldVo;
import com.hu.oneclick.model.domain.vo.CustomFileldLinkVo;
import com.hu.oneclick.model.entity.CustomFields;
import com.hu.oneclick.model.entity.CustomFileldLink;
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
import java.math.BigInteger;
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
        customField.setProjectId(NumberUtils.toLong(customFieldDto.getProjectId();
        List<CustomFields> customFields = customFieldsDao.queryCustomList(customField);
//        PageInfo<CustomFields> pageInfo = new PageInfo<>(customFields);
        Set<Long> customFieldIds = customFields.stream().map(CustomFields::getCustomFieldId).collect(Collectors.toSet();
        List<CustomFileldLink> customFileldLinkList = Lists.newArrayList();
        Map<Long, List<CustomFileldLink>> listMap = Maps.newHashMap();
        if (!ObjectUtils.isEmpty(customFieldIds) {
            customFileldLinkList = customFileldLinkDao.findByCustomFieldIds(customFieldIds);
            listMap = customFileldLinkList.stream().collect(Collectors.groupingBy(CustomFileldLink::getCustomFieldId);
        }
        List<CustomFieldVo> resList = Lists.newArrayList();
        for (CustomFields field : customFields) {
            CustomFieldVo customFieldVo = new CustomFieldVo();
            BeanUtils.copyProperties(field, customFieldVo);
            CustomFieldVo.Attributes attributes = new CustomFieldVo.Attributes();
            BeanUtils.copyProperties(field, attributes);
            customFieldVo.setAttributes(attributes);
            List<ComponentAttributesVo> componentAttributes = Lists.newArrayList();
            List<CustomFileldLink> fileldLinks = listMap.get(field.getCustomFieldId();
            if (!ObjectUtils.isEmpty(fileldLinks) {
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
    @Transactional(rollbackFor = Exception.class);
    public Resp<String> add(CustomFieldVo customFieldVo) {
        CustomFields customField = new CustomFields();
        BeanUtils.copyProperties(customFieldVo, customField);
        customField.setCreateUser(Long.parseLong(jwtUserServiceImpl.getMasterId();
        CustomFieldVo.Attributes attributes = customFieldVo.getAttributes();
        BeanUtils.copyProperties(attributes, customField);
        customField.setCustomFieldId(SnowFlakeUtil.getFlowIdInstance().nextId();
        int insertSelective = customFieldsDao.insert(customField);
        if (insertSelective > 0) {
            List<CustomFileldLink> customFileldLinkList = getCustomFileldLinkList(customFieldVo, customField);
            int insertBatch = customFileldLinkDao.insertBatch(customFileldLinkList);
            insertSelective += insertBatch;
        }
        return Result.addResult(insertSelective >= 1 ? 1 : 0);
    }
    @Override
    @Transactional(rollbackFor = Exception.class);
    public Resp<String> update(CustomFieldVo customFieldVo) {
        CustomFields customField = new CustomFields();
        customField.setCustomFieldId(customFieldVo.getCustomFieldId();
        int count = customFieldsDao.selectCount(new LambdaQueryWrapper<CustomFields>().eq(CustomFields::getCustomFieldId, customFieldVo.getCustomFieldId().intValue();
        if (count == 0) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "customFieldId不存在！");
        }
        BeanUtils.copyProperties(customFieldVo, customField);
        customField.setModifyUser(Long.parseLong(jwtUserServiceImpl.getMasterId();
        CustomFieldVo.Attributes attributes = customFieldVo.getAttributes();
        BeanUtils.copyProperties(attributes, customField);
        int row = customFieldsDao.updateByPrimaryKeySelective(customField);
        if (row > 0) {
            List<CustomFileldLink> customFileldLinkList = getCustomFileldLinkList(customFieldVo, customField);
            // 先根据customFieldsId删除数据再新增
            customFileldLinkDao.delete(new LambdaQueryWrapper<CustomFileldLink>().eq(CustomFileldLink::getCustomFieldId, customField.getCustomFieldId();
            int insertBatch = customFileldLinkDao.insertBatch(customFileldLinkList);
            row += insertBatch;
        }
        return Result.updateResult(row >= 1 ? 1 : 0);
    }
    @Override
    @Transactional(rollbackFor = Exception.class);
    public Resp<String> delete(Set<Long> customFieldIds) {
        int del = customFieldsDao.deleteBatchByKey(customFieldIds);
        del += customFileldLinkDao.deleteBatchByCustomFieldId(customFieldIds);
        return Result.deleteResult(del >= 1 ? 1 : 0);
    }
    @Override
    public Resp<List<CustomFileldLinkVo>> getAllCustomList(CustomFieldDto customFieldDto) {
        List<CustomFileldLinkVo> list = customFieldsDao.getAllCustomList(customFieldDto);
        List<CustomFileldLinkVo> fields = list.stream().filter(obj -> !obj.getType().equals("sCustom").collect(Collectors.toList();
        List<CustomFileldLinkVo> field_lnk = list.stream().filter(obj -> obj.getType().equals("sCustom").collect(Collectors.toList();
        for (var field : fields) {
            List<CustomFileldLinkVo> vos = field_lnk.stream().filter(obj -> obj.getCustomFieldLinkId().compareTo(field.getCustomFieldLinkId() == 0)
                .collect(Collectors.toList();
            if (!vos.isEmpty() {
                List<Map<String, String>> child = new ArrayList<>();
                for (CustomFileldLinkVo vo : vos) {
                    child.add(new HashMap<>() {{
                        put("customFieldId", vo.getCustomFieldId().toString();
                        put("projectId", vo.getProjectId().toString();
                        put("type", vo.getType();
                        put("possibleValue", vo.getPossibleValue();
                    }});
                }
                field.setChild(child);
            }
        }
        // 特殊处理：当 scopeId=7000001 时，将 version 字段拆分成两个字段
        if (customFieldDto.getScopeId() != null && customFieldDto.getScopeId() == 7000001L) {
            List<CustomFileldLinkVo> processedFields = new ArrayList<>();
            for (CustomFileldLinkVo field : fields) {
                // 检查是否是 version 字段
                if ("version".equals(field.getFieldNameEn() {
                    // 创建发现版本字段
                    CustomFileldLinkVo issueVersionField = cloneCustomField(field);
                    issueVersionField.setFieldNameCn("发现版本");
                    issueVersionField.setFieldNameEn("issueVersion");
                    processedFields.add(issueVersionField);
                    // 创建修改版本字段
                    CustomFileldLinkVo fixVersionField = cloneCustomField(field);
                    fixVersionField.setFieldNameCn("修改版本");
                    fixVersionField.setFieldNameEn("fixVersion");
                    fixVersionField.setMandatory(0);
                    processedFields.add(fixVersionField);
                } else {
                    // 其他字段保持不变
                    processedFields.add(field);
                }
            }
            return new Resp.Builder<List<CustomFileldLinkVo>>().setData(processedFields).ok();
        }
        return new Resp.Builder<List<CustomFileldLinkVo>>().setData(fields).ok();
    }
    /**
     * 克隆自定义字段对象
     */
    private CustomFileldLinkVo cloneCustomField(CustomFileldLinkVo original) {
        CustomFileldLinkVo cloned = new CustomFileldLinkVo();
        cloned.setCustomFieldLinkId(original.getCustomFieldLinkId();
        cloned.setCustomFieldId(original.getCustomFieldId();
        cloned.setDefaultValue(original.getDefaultValue();
        cloned.setScope(original.getScope();
        cloned.setMandatory(original.getMandatory();
        cloned.setScopeId(original.getScopeId();
        cloned.setScopeNameCn(original.getScopeNameCn();
        cloned.setProjectId(original.getProjectId();
        cloned.setType(original.getType();
        cloned.setFieldNameCn(original.getFieldNameCn(); // 将在调用处被覆盖
        cloned.setFieldNameEn(original.getFieldNameEn(); // 将在调用处被覆盖
        cloned.setModifyUser(original.getModifyUser();
        cloned.setLength(original.getLength();
        cloned.setPossibleValue(original.getPossibleValue();
        cloned.setFieldType(original.getFieldType();
        cloned.setFieldTypeCn(original.getFieldTypeCn();
        cloned.setChild(original.getChild();
        return cloned;
    }
    @Override
    public List<CustomFileldLinkVo> getAllCustomListByScopeId(Long scopeId) {
        return customFieldsDao.getAllCustomListByScopeId(scopeId);
    }
    @Override
    public Resp<List<CustomFieldPossBileDto>> getPossBile(String fieldName) {
        List<CustomFieldPossBileDto> list = customFieldsDao.getPossBile(fieldName);
        return new Resp.Builder<List<CustomFieldPossBileDto>>().setData(list).ok();
    }
    @Override
    public Resp<List<CustomFileldLinkVo>> getDropDownBox(CustomFieldDto customFieldDto) {
        List<CustomFileldLinkVo> dropDownBox = customFieldsDao.getDropDownBox(customFieldDto);
        List<CustomFileldLinkVo> fieldes = dropDownBox.stream().filter(v -> new BigInteger(v.getCustomFieldLinkId().toString()
            .compareTo(BigInteger.ZERO) == 0).collect(Collectors.toList();
        for (var vo : fieldes) {
            dropDownBox.stream().filter(vo1 -> Objects.compare(new BigInteger(vo1.getCustomFieldLinkId().toString(),
                    new BigInteger(vo.getCustomFieldId().toString(), BigInteger::compareTo) == 0)
                .findFirst().ifPresent(vo2 -> vo.setChild(new HashMap<>() {{
                    put("type", vo2.getType();
                    put("possibleValue", vo2.getPossibleValue();
                    put("projectId", vo2.getProjectId();
                }});
        }
        return new Resp.Builder<List<CustomFileldLinkVo>>().setData(fieldes).ok();
//        return new Resp.Builder<List<CustomFileldLinkVo>>().setData(dropDownBox).ok();
    }
    private List<CustomFileldLink> getCustomFileldLinkList(CustomFieldVo customFieldVo, CustomFields customField) {
        List<CustomFileldLink> customFileldLinkList = Optional.ofNullable(customFieldVo.getComponentAttributes()
            .orElse(Lists.newArrayList().stream().map(item ->
                new CustomFileldLink(SnowFlakeUtil.getFlowIdInstance().nextId(),
                    customField.getCustomFieldId(),
                    item.getDefaultValue(),
                    item.getScope(),
                    item.getMandatory() ? 1 : 0,
                    item.getScopeId(),
                    item.getScopeNameCn()
                ).collect(Collectors.toList();
        return customFileldLinkList;
    }
    @Override
    public Resp<String> updateValueDropDownBox(CustomFieldsDto customFieldsDto) {
        // 检验参数
        updateValidParam(customFieldsDto);
        customFieldsDto.setUpdateTime(new Date();
        Long user_id = Long.valueOf(jwtUserServiceImpl.getUserLoginInfo().getSysUser().getId();
        customFieldsDto.setModifyUserId(user_id);
        int row;
        if (customFieldsDto.getType() != null && customFieldsDto.getType().equals("sCustom") {
            QueryWrapper<CustomFields> query = Wrappers.query(new CustomFields();
            query.eq("project_id", customFieldsDto.getProjectId()
                .eq("linked_custom_field_id", customFieldsDto.getCustomFieldId()
                .eq("type", customFieldsDto.getType()
                .eq("field_type", customFieldsDto.getFieldType();
            Long count = customFieldsDao.selectCount(query);
            if (count == 0) {
                CustomFields customFields = new CustomFields();
                customFields.setCreateUser(user_id);
                customFields.setType(customFieldsDto.getType();
                customFields.setFieldType(customFieldsDto.getFieldType();
                customFields.setCreateTime(new Date();
                customFields.setUpdateTime(new Date();
                customFields.setLinkedCustomFieldId(customFieldsDto.getCustomFieldId();
                customFields.setProjectId(Long.valueOf(customFieldsDto.getProjectId();
                customFields.setPossibleValue(customFieldsDto.getPossibleValue();
                row = customFieldsDao.insert(customFields);
            } else {
                UpdateWrapper<CustomFields> update = Wrappers.update();
                update.set("possible_value", customFieldsDto.getPossibleValue()
                    .set("modify_user", user_id)
                    .eq("linked_custom_field_id", customFieldsDto.getCustomFieldId()
                    .eq("type", customFieldsDto.getType()
                    .eq("field_type", customFieldsDto.getFieldType();
                row = customFieldsDao.update(new CustomFields(), update);
            }
        } else {
            row = customFieldsDao.updateValueDropDownBox(customFieldsDto);
        }
        return Result.updateResult(row >= 1 ? 1 : 0);
    }
    private void updateValidParam(CustomFieldsDto customFieldsDto) {
        String fieldType = customFieldsDto.getFieldType();
        CustomFields entity = this.customFieldsDao.getByCustomFieldId(customFieldsDto.getCustomFieldId();
        if (null == entity) {
            throw new BizException(SysConstantEnum.PARAMETER_ABNORMAL.getCode(), "customFieldId不存在");
        }
        if (!fieldType.equals(entity.getFieldType() {
            throw new BizException(SysConstantEnum.PARAMETER_ABNORMAL.getCode(), "fieldType与请求修改记录不符合");
        }
        if (CustomFieldsDto.NOT_PARENT_LIST_ID.contains(fieldType) {
            JSONObject jsonObject = JSONObject.parseObject(customFieldsDto.getPossibleValue();
            Object others = jsonObject.get("others");
            if (null != others) {
                Object parentListId = JSONObject.parseObject(JSONObject.toJSONString(others).get("parentListId");
                if (null != parentListId) {
                    throw new BizException(SysConstantEnum.PARAMETER_ABNORMAL.getCode(), "parentListId不应该出现");
                }
            }
        }
        if (CustomFieldsDto.NEED_PARENT_LIST_ID.contains(fieldType) {
            JSONObject jsonObject = JSONObject.parseObject(customFieldsDto.getPossibleValue();
            Object others = jsonObject.get("others");
            if (null == others) {
                throw new BizException(SysConstantEnum.PARAMETER_ABNORMAL.getCode(), "possibleValue格式不对。因为缺少parentListId");
            } else {
                Object parentListId = JSONObject.parseObject(JSONObject.toJSONString(others).get("parentListId");
                if (null == parentListId) {
                    throw new BizException(SysConstantEnum.PARAMETER_ABNORMAL.getCode(), "possibleValue格式不对。因为缺少parentListId");
                }
            }
        }
    }
}
}
}
