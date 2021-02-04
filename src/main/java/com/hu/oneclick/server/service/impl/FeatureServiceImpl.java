package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.FeatureDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.Feature;
import com.hu.oneclick.server.service.FeatureService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;
import java.util.List;

/**
 * @author qingyang
 */
@Service
public class FeatureServiceImpl implements FeatureService {

    private final static Logger logger = LoggerFactory.getLogger(SprintServiceImpl.class);


    private final FeatureDao featureDao;

    private final JwtUserServiceImpl jwtUserService;

    public FeatureServiceImpl(FeatureDao featureDao, JwtUserServiceImpl jwtUserService) {
        this.featureDao = featureDao;
        this.jwtUserService = jwtUserService;
    }


    @Override
    public Resp<Feature> queryById(String id) {
        String masterId = jwtUserService.getMasterId();
        Feature feature =  featureDao.queryById(id,masterId);
        return new Resp.Builder<Feature>().setData(feature).ok();
    }

    @Override
    public Resp<List<Feature>> queryList(Feature feature) {
        feature.queryListVerify();
        feature.setUserId(jwtUserService.getMasterId());
        List<Feature> select = featureDao.select(feature);
        return new Resp.Builder<List<Feature>>().setData(select).total(select.size()).ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> insert(Feature feature) {
        try {
            //验证参数
            feature.verify();
            //验证是否存在
            verifyIsExist(feature.getTitle(),feature.getProjectId());
            feature.setUserId(jwtUserService.getMasterId());
            Date date = new Date();
            feature.setCreateTime(date);
            feature.setUpdateTime(date);
            return Result.addResult(featureDao.insert(feature));
        }catch (BizException e){
            logger.error("class: FeatureServiceImpl#insert,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> update(Feature feature) {
        try {
            //验证参数
            feature.verify();
            //验证是否存在
            verifyIsExist(feature.getTitle(),feature.getProjectId());
            feature.setUserId(jwtUserService.getMasterId());
            return Result.updateResult(featureDao.update(feature));
        }catch (BizException e){
            logger.error("class: FeatureServiceImpl#update,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> delete(String id) {
        try {
            Feature feature = new Feature();
            feature.setId(id);
            return Result.deleteResult(featureDao.delete(feature));
        }catch (BizException e){
            logger.error("class: FeatureServiceImpl#delete,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    /**
     *  查重
     */
    private void verifyIsExist(String title,String projectId){
        Feature feature = new Feature();
        feature.setTitle(title);
        feature.setProjectId(projectId);
        feature.setId(null);
        if (featureDao.selectOne(feature) != null){
            throw new BizException(SysConstantEnum.DATE_EXIST.getCode(),feature.getTitle() + SysConstantEnum.DATE_EXIST.getValue());
        }
    }
}
