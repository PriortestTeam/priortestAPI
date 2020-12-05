package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.CustomFieldDao;
import com.hu.oneclick.dao.FieldRadioDao;
import com.hu.oneclick.dao.FieldTextDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.FieldRadio;
import com.hu.oneclick.model.domain.FieldRichText;
import com.hu.oneclick.model.domain.FieldText;
import com.hu.oneclick.server.service.CustomFieldService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public CustomFieldServiceImpl(JwtUserServiceImpl jwtUserServiceImpl, CustomFieldDao customFieldDao, FieldRadioDao fieldRadioDao, FieldTextDao fieldTextDao) {
        this.jwtUserServiceImpl = jwtUserServiceImpl;
        this.customFieldDao = customFieldDao;
        this.fieldRadioDao = fieldRadioDao;
        this.fieldTextDao = fieldTextDao;
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
            if (customFieldDao.insert(fieldRadio) > 0 && fieldRadioDao.insert(fieldRadio) > 0){
                return new Resp.Builder<String>().setData(SysConstantEnum.ADD_SUCCESS.getValue()).ok();
            }
            return new Resp.Builder<String>().setData(SysConstantEnum.ADD_FAILED.getValue()).fail();
        }catch (BaseException e){
            logger.error("class: CustomFieldServiceImpl#addCustomRadio,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> updateCustomRadio(FieldRadio fieldRadio) {
        try {
            fieldRadio.setUserId(jwtUserServiceImpl.getMasterId());
            if (customFieldDao.update(fieldRadio) > 0 && fieldRadioDao.update(fieldRadio) > 0){
                return new Resp.Builder<String>().setData(SysConstantEnum.UPDATE_SUCCESS.getValue()).ok();
            }
            return new Resp.Builder<String>().setData(SysConstantEnum.UPDATE_FAILED.getValue()).fail();
        }catch (BaseException e){
            logger.error("class: CustomFieldServiceImpl#updateCustomRadio,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> deleteCustomRadio(String id) {
        try {
            String userId = jwtUserServiceImpl.getMasterId();
            if (customFieldDao.deleteById(id,userId) > 0 && fieldRadioDao.deleteById(id) > 0){
                return new Resp.Builder<String>().setData(SysConstantEnum.DELETE_SUCCESS.getValue()).ok();
            }
            return new Resp.Builder<String>().setData(SysConstantEnum.DELETE_FAILED.getValue()).fail();
        }catch (BaseException e){
            logger.error("class: CustomFieldServiceImpl#deleteCustomRadio,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    @Override
    public Resp<FieldText> queryFieldTextById(String customFieldId) {
        FieldText fieldText = fieldTextDao.queryFieldTextById(customFieldId, jwtUserServiceImpl.getMasterId());
        return new Resp.Builder<FieldText>().setData(fieldText).ok();
    }

    @Override
    public Resp<String> addCustomText(FieldText fieldText) {
        fieldText.subVerify();
        return addCustomText2(fieldText);
    }

    private Resp<String> addCustomText2(FieldText fieldText){
        try {
            fieldText.setUserId(jwtUserServiceImpl.getMasterId());
            if (customFieldDao.insert(fieldText) > 0 && fieldTextDao.insert(fieldText) > 0){
                return new Resp.Builder<String>().setData(SysConstantEnum.ADD_SUCCESS.getValue()).ok();
            }
            return new Resp.Builder<String>().setData(SysConstantEnum.ADD_FAILED.getValue()).fail();
        }catch (BaseException e){
            logger.error("class: CustomFieldServiceImpl#addCustomText2,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    @Override
    public Resp<String> updateCustomText(FieldText fieldText) {
        try {
            fieldText.setUserId(jwtUserServiceImpl.getMasterId());
            if (customFieldDao.update(fieldText) > 0 && fieldTextDao.update(fieldText) > 0){
                return new Resp.Builder<String>().setData(SysConstantEnum.UPDATE_SUCCESS.getValue()).ok();
            }
            return new Resp.Builder<String>().setData(SysConstantEnum.UPDATE_FAILED.getValue()).fail();
        }catch (BaseException e){
            logger.error("class: CustomFieldServiceImpl#updateCustomText,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    private Resp<String> updateCustomText2(FieldText fieldText) {
        try {
            fieldText.setUserId(jwtUserServiceImpl.getMasterId());
            if (customFieldDao.update(fieldText) > 0 && fieldTextDao.update(fieldText) > 0){
                return new Resp.Builder<String>().setData(SysConstantEnum.UPDATE_SUCCESS.getValue()).ok();
            }
            return new Resp.Builder<String>().setData(SysConstantEnum.UPDATE_FAILED.getValue()).fail();
        }catch (BaseException e){
            logger.error("class: CustomFieldServiceImpl#updateCustomText2,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    @Override
    public Resp<String> deleteCustomText(String id) {
        try {
            String userId = jwtUserServiceImpl.getMasterId();
            if (customFieldDao.deleteById(id,userId) > 0 && fieldTextDao.deleteById(id) > 0){
                return new Resp.Builder<String>().setData(SysConstantEnum.DELETE_SUCCESS.getValue()).ok();
            }
            return new Resp.Builder<String>().setData(SysConstantEnum.DELETE_FAILED.getValue()).fail();
        }catch (BaseException e){
            logger.error("class: CustomFieldServiceImpl#deleteCustomText,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    @Override
    public Resp<String> addCustomRichText(FieldRichText fieldRichText) {
        fieldRichText.subVerify();
        FieldText fieldText = new FieldText();
        BeanUtils.copyProperties(fieldRichText,fieldText);
        return addCustomText2(fieldText);
    }

    @Override
    public Resp<String> updateCustomRichText(FieldRichText fieldRichText) {
        FieldText fieldText = new FieldText();
        BeanUtils.copyProperties(fieldRichText,fieldText);
        return updateCustomText2(fieldText);
    }
}
