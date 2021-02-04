package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.FeatureDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.Feature;
import com.hu.oneclick.server.service.FeatureService;
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
        return new Resp.Builder<List<Feature>>().setData(select).ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> insert(Feature feature) {
        try {
            feature.verify();
            feature.setUserId(jwtUserService.getMasterId());
            Date date = new Date();
            feature.setCreateTime(date);
            feature.setUpdateTime(date);
            return Result.addResult(featureDao.insert(feature));
        }catch (BaseException e){
            logger.error("class: FeatureServiceImpl#insert,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> update(Feature feature) {
        try {
            feature.verify();
            feature.setUserId(jwtUserService.getMasterId());
            return Result.updateResult(featureDao.update(feature));
        }catch (BaseException e){
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
        }catch (BaseException e){
            logger.error("class: FeatureServiceImpl#delete,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }
}
