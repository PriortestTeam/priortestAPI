package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.constant.FieldConstant;
import com.hu.oneclick.common.constant.TwoConstant;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.CustomFieldDao;
import com.hu.oneclick.dao.FieldDropDownDao;
import com.hu.oneclick.dao.FieldRadioDao;
import com.hu.oneclick.dao.FieldTextDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.CustomField;
import com.hu.oneclick.model.domain.FieldDropDown;
import com.hu.oneclick.model.domain.FieldRadio;
import com.hu.oneclick.model.domain.FieldRichText;
import com.hu.oneclick.model.domain.FieldText;
import com.hu.oneclick.model.domain.dto.CustomFieldDto;
import com.hu.oneclick.server.service.CustomFieldService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
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

    public CustomFieldServiceImpl(JwtUserServiceImpl jwtUserServiceImpl, CustomFieldDao customFieldDao, FieldRadioDao fieldRadioDao, FieldTextDao fieldTextDao, FieldDropDownDao fieldDropDownDao, JwtUserServiceImpl jwtUserService) {
        this.jwtUserServiceImpl = jwtUserServiceImpl;
        this.customFieldDao = customFieldDao;
        this.fieldRadioDao = fieldRadioDao;
        this.fieldTextDao = fieldTextDao;
        this.fieldDropDownDao = fieldDropDownDao;
        this.jwtUserService = jwtUserService;
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
            fieldRadio.setUserId(jwtUserServiceImpl.getMasterId());
            Result.verifyDoesExist(queryByFieldName(fieldRadio.getFieldName(), fieldRadio.getProjectId()), fieldRadio.getFieldName());
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

    private Resp<String> addCustomText2(FieldText fieldText) {
        try {
            fieldText.setUserId(jwtUserServiceImpl.getMasterId());
            Result.verifyDoesExist(queryByFieldName(fieldText.getFieldName(), fieldText.getProjectId()), fieldText.getFieldName());
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
}
