package com.hu.oneclick.server.service.impl;

import com.alibaba.fastjson2.JSON;
import com.hu.oneclick.common.constant.FieldConstant;
import com.hu.oneclick.common.constant.TwoConstant;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.*;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.dto.CustomFieldDto;
import com.hu.oneclick.model.domain.dto.ViewScopeChildParams;
import com.hu.oneclick.model.entity.*;
import com.hu.oneclick.server.service.CustomFieldService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qingyang
 */
@Service
public class CustomFieldServiceImpl implements CustomFieldService {

    private final static Logger logger = LoggerFactory.getLogger(CustomFieldServiceImpl.class);

    private final JwtUserServiceImpl jwtUserServiceImpl;

    private final CustomFieldDao customFieldDao;

    private final FieldRadioDao fieldRadioDao;

    private final FieldTextDao fieldTextDao;

    private final FieldDropDownDao fieldDropDownDao;

    private final JwtUserServiceImpl jwtUserService;

    private final ViewDownChildParamsDao viewDownChildParamsDao;

    public CustomFieldServiceImpl(JwtUserServiceImpl jwtUserServiceImpl, CustomFieldDao customFieldDao, FieldRadioDao fieldRadioDao, FieldTextDao fieldTextDao, FieldDropDownDao fieldDropDownDao, JwtUserServiceImpl jwtUserService, ViewDownChildParamsDao viewDownChildParamsDao) {
        this.jwtUserServiceImpl = jwtUserServiceImpl;
        this.customFieldDao = customFieldDao;
        this.fieldRadioDao = fieldRadioDao;
        this.fieldTextDao = fieldTextDao;
        this.fieldDropDownDao = fieldDropDownDao;
        this.jwtUserService = jwtUserService;
        this.viewDownChildParamsDao = viewDownChildParamsDao;
    }

    @Override
    public Resp<List<CustomField>> queryCustomList(CustomField customField) {
        customField.setUserId(jwtUserServiceImpl.getMasterId());

        List<CustomField> customFields = null;
        try {
            customFields = customFieldDao.queryAll(customField);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Resp.Builder<List<CustomField>>().setData(customFields).total(customFields).ok();
    }

    @Override
    public Resp<FieldRadio> queryFieldRadioById(String customFieldId) {
        FieldRadio fieldRadio = fieldRadioDao.queryFieldRadioById(customFieldId, jwtUserServiceImpl.getMasterId());
        return new Resp.Builder<FieldRadio>().setData(fieldRadio).ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> addCustomRadio(FieldRadio fieldRadio) {
        try {
            fieldRadio.subVerify();
            String masterId = jwtUserServiceImpl.getMasterId();
            fieldRadio.setUserId(masterId);
            String projectId = fieldRadio.getProjectId();
            Result.verifyDoesExist(queryByFieldName(fieldRadio.getFieldName(), projectId), fieldRadio.getFieldName());

            addViewDownChildParams(fieldRadio);
            return Result.addResult((customFieldDao.insert(fieldRadio) > 0
                && fieldRadioDao.insert(fieldRadio) > 0) ? 1 : 0);
        } catch (BizException e) {
            logger.error("class: CustomFieldServiceImpl#addCustomRadio,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> updateCustomRadio(FieldRadio fieldRadio) {
        try {
            fieldRadio.subVerify();
            fieldRadio.setUserId(jwtUserServiceImpl.getMasterId());
            fieldRadio.setCustomFieldId();
            Result.verifyDoesExist(queryByFieldName(fieldRadio.getFieldName(), fieldRadio.getProjectId()), fieldRadio.getFieldName());
            //masiyi 2021年11月27日10:39:52 更新视图字段
            updateViewDownChildParams(fieldRadio);
            return Result.updateResult((customFieldDao.update(fieldRadio) > 0
                && fieldRadioDao.update(fieldRadio) > 0) ? 1 : 0);
        } catch (BizException e) {
            logger.error("class: CustomFieldServiceImpl#updateCustomRadio,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> deleteCustomRadio(String id) {
        try {
            String userId = jwtUserServiceImpl.getMasterId();
            return Result.deleteResult((customFieldDao.deleteById(id, userId) > 0
                && fieldRadioDao.deleteById(id) > 0) ? 1 : 0);
        } catch (BizException e) {
            logger.error("class: CustomFieldServiceImpl#deleteCustomRadio,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    @Override
    public Resp<FieldText> queryFieldTextById(String customFieldId) {
        FieldText fieldText = fieldTextDao.queryFieldTextById(customFieldId, jwtUserServiceImpl.getMasterId());
        fieldText.setDefaultValues(TwoConstant.convertToList(fieldText.getDefaultValue()));
        return new Resp.Builder<FieldText>().setData(fieldText).ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> addCustomText(FieldText fieldText) {
        fieldText.subVerify();
        return addCustomText2(fieldText);
    }

    /**
     * 添加文本框
     *
     * @Param: [fieldText]
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author:
     * @Date: 2021/11/26
     */
    private Resp<String> addCustomText2(FieldText fieldText) {
        try {
            fieldText.setUserId(jwtUserServiceImpl.getMasterId());
            Result.verifyDoesExist(queryByFieldName(fieldText.getFieldName(), fieldText.getProjectId()), fieldText.getFieldName());

            addViewDownChildParams(fieldText);
            return Result.addResult((customFieldDao.insert(fieldText) > 0
                && fieldTextDao.insert(fieldText) > 0) ? 1 : 0);
        } catch (BizException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logger.error("class: CustomFieldServiceImpl#addCustomText2,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> updateCustomText(FieldText fieldText) {
        fieldText.subVerify();
        fieldText.setCustomFieldId();
        //masiyi 2021年11月27日10:39:52 更新视图字段
        updateViewDownChildParams(fieldText);
        return updateCustomText2(fieldText);
    }

    private Resp<String> updateCustomText2(FieldText fieldText) {
        try {
            fieldText.setUserId(jwtUserServiceImpl.getMasterId());
            Result.verifyDoesExist(queryByFieldName(fieldText.getFieldName(), fieldText.getProjectId()), fieldText.getFieldName());
            return Result.updateResult((customFieldDao.update(fieldText) > 0
                && fieldTextDao.update(fieldText) > 0) ? 1 : 0);
        } catch (BizException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logger.error("class: CustomFieldServiceImpl#updateCustomText2,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> deleteCustomText(String id) {
        try {
            String userId = jwtUserServiceImpl.getMasterId();
            return Result.deleteResult((customFieldDao.deleteById(id, userId) > 0
                && fieldTextDao.deleteById(id) > 0) ? 1 : 0);
        } catch (BizException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logger.error("class: CustomFieldServiceImpl#deleteCustomText,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> addCustomRichText(FieldRichText fieldRichText) {
        fieldRichText.subVerify();
        FieldText fieldText = new FieldText();
        BeanUtils.copyProperties(fieldRichText, fieldText);
        return addCustomText2(fieldText);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> updateCustomRichText(FieldRichText fieldRichText) {
        fieldRichText.subVerify();
        FieldText fieldText = new FieldText();
        BeanUtils.copyProperties(fieldRichText, fieldText);
        //masiyi 2021年11月27日10:39:52 更新视图字段
        updateViewDownChildParams(fieldRichText);
        return updateCustomText2(fieldText);
    }

    @Override
    public Resp<FieldDropDown> queryFieldDropDownById(String customFieldId) {
        FieldDropDown fieldDropDown = fieldDropDownDao.queryFieldTextById(customFieldId, jwtUserServiceImpl.getMasterId());
        fieldDropDown.setDropDowns(TwoConstant.convertToList(fieldDropDown.getDropDownList()));
        return new Resp.Builder<FieldDropDown>().setData(fieldDropDown).ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> addCustomDropDown(FieldDropDown fieldDropDown) {
        try {
            fieldDropDown.subVerify();
            fieldDropDown.setUserId(jwtUserServiceImpl.getMasterId());
            Result.verifyDoesExist(queryByFieldName(fieldDropDown.getFieldName(), fieldDropDown.getProjectId()), fieldDropDown.getFieldName());

            addViewDownChildParams(fieldDropDown);
            return Result.addResult((customFieldDao.insert(fieldDropDown) > 0
                && fieldDropDownDao.insert(fieldDropDown) > 0) ? 1 : 0);
        } catch (BizException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logger.error("class: CustomFieldServiceImpl#addCustomDropDown,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> updateCustomDropDown(FieldDropDown fieldDropDown) {
        try {
            fieldDropDown.subVerify();
            fieldDropDown.setUserId(jwtUserServiceImpl.getMasterId());
            Result.verifyDoesExist(queryByFieldName(fieldDropDown.getFieldName(), fieldDropDown.getProjectId()), fieldDropDown.getFieldName());
            //masiyi 2021年11月27日10:39:52 更新视图字段
            updateViewDownChildParams(fieldDropDown);
            return Result.updateResult((customFieldDao.update(fieldDropDown) > 0
                && fieldDropDownDao.update(fieldDropDown) > 0) ? 1 : 0);
        } catch (BizException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logger.error("class: CustomFieldServiceImpl#updateCustomDropDown,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> deleteCustomDropDown(String customFieldId) {
        try {
            String userId = jwtUserServiceImpl.getMasterId();
            return Result.deleteResult((customFieldDao.deleteById(customFieldId, userId) > 0
                && fieldDropDownDao.deleteById(customFieldId) > 0) ? 1 : 0);
        } catch (BizException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logger.error("class: CustomFieldServiceImpl#deleteCustomDropDown,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    /**
     * 查询字段在当前项目中是否存在
     *
     * @param fieldName
     * @return
     */
    private Integer queryByFieldName(String fieldName, String projectId) {
        if (StringUtils.isEmpty(fieldName)) {
            return null;
        }
        if (customFieldDao.queryByFieldName(jwtUserService.getMasterId(), fieldName, projectId) > 0) {
            return 1;
        }
        return null;
    }

    @Override
    public Resp<List<Object>> getAllCustomField(CustomFieldDto customFieldDto) {

        CustomField customField = new CustomField();
        customField.setProjectId(customFieldDto.getProjectId());
        Resp<List<CustomField>> listResp = queryCustomList(customField);
        //获取该用户该项目下的用户字段
        List<CustomField> data = listResp.getData();
        //过滤的list
        List<CustomField> collect = new ArrayList<>();
        //按照scope分数据
        String scope = customFieldDto.getScope();
        if (StringUtils.isEmpty(scope)) {
            return null;
        }
        switch (scope) {
            case FieldConstant.PROJECT:
                collect = data.stream().filter(p -> {
                    String[] split = p.getScope().split(",");
                    return "1".equals(split[0]);
                }).collect(Collectors.toList());
                break;
            case FieldConstant.FEATURE:
                collect = data.stream().filter(p -> {
                    String[] split = p.getScope().split(",");
                    return "1".equals(split[1]);
                }).collect(Collectors.toList());
                break;
            case FieldConstant.TESTCASE:
                collect = data.stream().filter(p -> {
                    String[] split = p.getScope().split(",");
                    return "1".equals(split[2]);
                }).collect(Collectors.toList());
                break;
            case FieldConstant.TESTCYCLE:
                collect = data.stream().filter(p -> {
                    String[] split = p.getScope().split(",");
                    return "1".equals(split[3]);
                }).collect(Collectors.toList());
                break;
            case FieldConstant.ISSUE:
                collect = data.stream().filter(p -> {
                    String[] split = p.getScope().split(",");
                    return "1".equals(split[4]);
                }).collect(Collectors.toList());
                break;
            default:

        }
        //存储
        List<Object> list = new ArrayList<>();

        //获取对应scope下的字段类型
        for (CustomField field : collect) {

            String type = field.getType();
            switch (type) {
                case FieldConstant.type.DROPDOWN:
                    Resp<FieldDropDown> fieldDropDownResp = this.queryFieldDropDownById(field.getId());
                    FieldDropDown data1 = fieldDropDownResp.getData();
                    list.add(data1);
                    break;
                case FieldConstant.type.RADIO:
                    Resp<FieldRadio> fieldRadioResp = this.queryFieldRadioById(field.getId());
                    FieldRadio data2 = fieldRadioResp.getData();
                    list.add(data2);
                    break;
                case FieldConstant.type.MEMO:
                    break;
                case FieldConstant.type.TEXT:
                    Resp<FieldText> fieldTextResp = this.queryFieldTextById(field.getId());
                    FieldText data3 = fieldTextResp.getData();
                    list.add(data3);
                    break;
                default:
                    break;
            }

        }


        return new Resp.Builder<List<Object>>().setData(list).ok();
    }

    /**
     * 添加视图字段集合
     *
     * @Param: [cla]
     * @return: com.hu.oneclick.model.entity.ViewDownChildParams
     * @Author: MaSiyi
     * @Date: 2021/11/26
     */
    private <T> void addViewDownChildParams(T cla) {

        ViewDownChildParams viewDownChildParams = new ViewDownChildParams();
        if (cla instanceof CustomField) {
            CustomField customField = (CustomField) cla;
            viewDownChildParams.setUserId(customField.getUserId());
            viewDownChildParams.setProjectId(customField.getProjectId());
        }


        List<ViewDownChildParams> viewDownChildParams1 = viewDownChildParamsDao.queryList(viewDownChildParams);

        // 设置DefaultValues
        ViewScopeChildParams viewScopeChildParams = new ViewScopeChildParams();
        viewScopeChildParams.setType("fString");
        //设置子选择框
        ViewScopeChildParams viewScopeChildParams1 = new ViewScopeChildParams();
        if (cla instanceof FieldRadio) {
            FieldRadio fieldRadio = (FieldRadio) cla;
            viewScopeChildParams1.setOptionValue(fieldRadio.getDefaultValue());
            viewScopeChildParams1.setOptionValueCn(fieldRadio.getFieldName());
            viewDownChildParams.setScope(fieldRadio.getScope());
        } else if (cla instanceof FieldText) {
            FieldText fieldText = (FieldText) cla;
            viewScopeChildParams1.setOptionValue(fieldText.getDefaultValue());
            viewScopeChildParams1.setOptionValueCn(fieldText.getFieldName());
            viewDownChildParams.setScope(fieldText.getScope());
        } else if (cla instanceof FieldRichText) {
            FieldRichText fieldRichText = (FieldRichText) cla;
            viewScopeChildParams1.setOptionValue(fieldRichText.getDefaultValue());
            viewScopeChildParams1.setOptionValueCn(fieldRichText.getFieldName());
            viewDownChildParams.setScope(fieldRichText.getScope());
        } else if (cla instanceof FieldDropDown) {
            FieldDropDown fieldDropDown = (FieldDropDown) cla;
            viewScopeChildParams1.setOptionValue(fieldDropDown.getDefaultValue());
            viewScopeChildParams1.setOptionValueCn(fieldDropDown.getFieldName());
            viewDownChildParams.setScope(fieldDropDown.getScope());
        }

        if (viewDownChildParams1.isEmpty()) {
            Date time = new Date();
            viewDownChildParams.setCreateTime(time);
            viewScopeChildParams.setSelectChild(Collections.singletonList(viewScopeChildParams1));
            //对应view_down_child_params表

            viewDownChildParams.setDefaultValues(JSON.toJSONString(viewScopeChildParams));
            viewDownChildParamsDao.insert(viewDownChildParams);
        } else if (viewDownChildParams1.size() == 1) {
            String defaultValues = viewDownChildParams.getDefaultValues();
            List<ViewScopeChildParams> childParams = JSON.parseArray(defaultValues, ViewScopeChildParams.class);
            if (childParams == null) {
                childParams = new ArrayList<>();
            }
            childParams.add(viewScopeChildParams);
            viewDownChildParams.setDefaultValues(JSON.toJSONString(childParams));
            viewDownChildParamsDao.update(viewDownChildParams);
        } else {
            throw new BizException("系统异常");
        }

    }

    /**
     * 根据projectid和userid删除视图字段
     *
     * @Param: [customField]
     * @return: java.lang.Integer
     * @Author: MaSiyi
     * @Date: 2021/11/26
     */
    private Integer deleteViewDownChildParams(CustomField customField) {
        return viewDownChildParamsDao.deleteByProjectAndUserId(customField.getProjectId(), customField.getUserId());
    }

    /**
     * 更新视图字段
     *
     * @Param: []
     * @return: void
     * @Author: MaSiyi
     * @Date: 2021/11/27
     */
    private <T> void updateViewDownChildParams(T cla) {
        CustomField customField = new CustomField();
        if (cla instanceof CustomField) {
            customField = (CustomField) cla;
        }
        if (deleteViewDownChildParams(customField) > 0) {
            addViewDownChildParams(cla);
        }
    }

}
