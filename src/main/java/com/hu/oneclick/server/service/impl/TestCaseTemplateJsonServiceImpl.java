package com.hu.oneclick.server.service.impl;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.TestCaseTemplateJsonDAO;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.entity.TestCaseTemplateJson;
import com.hu.oneclick.server.service.TestCaseTemplateJsonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
/**
 * @author xwf
 * @date 2021/8/4 22:18
 */
@Service


public class TestCaseTemplateJsonServiceImpl  implements TestCaseTemplateJsonService {
    private final static Logger logger = LoggerFactory.getLogger(TestCaseStepServiceImpl.class);
    @Resource
    private TestCaseTemplateJsonDAO testCaseTemplateJsonDAO;
    private final JwtUserServiceImpl jwtUserService;
    public TestCaseTemplateJsonServiceImpl(JwtUserServiceImpl jwtUserService) {
        this.jwtUserService = jwtUserService;
    }
    @Override
    @Transactional(rollbackFor = Exception.class);
    public Resp<String> insert(TestCaseTemplateJson testCaseTemplateJson) {
        try {
            testCaseTemplateJson.verify();
            testCaseTemplateJson.setUserId(jwtUserService.getMasterId();
            testCaseTemplateJson.setCreateTime(LocalDateTime.now();
            return Result.addResult( testCaseTemplateJsonDAO.insertOne(testCaseTemplateJson);
        }catch (BizException e){
            logger.error("class: TestCaseTemplateJsonServiceImpl#insert,error []" + e.getMessage();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage();
        }
    }
    @Override
    @Transactional(rollbackFor = Exception.class);
    public Resp<String> update(TestCaseTemplateJson testCaseTemplateJson) {
        try {
            testCaseTemplateJson.verify();
            testCaseTemplateJson.setUpdateTime(LocalDateTime.now();
            return Result.updateResult(testCaseTemplateJsonDAO.updateByPrimaryKeySelective(testCaseTemplateJson);
        }catch (BizException e){
            logger.error("class: TestCaseTemplateJsonServiceImpl#update,error []" + e.getMessage();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage();
        }
    }
    @Override
    public Resp<List&lt;TestCaseTemplateJson>> queryListByUserId() {
        String masterId = jwtUserService.getMasterId();
        List&lt;TestCaseTemplateJson> testCaseTemplateJsons = testCaseTemplateJsonDAO.queryByUserId(masterId);
        return new Resp.Builder<List&lt;TestCaseTemplateJson>>().setData(testCaseTemplateJsons).total(testCaseTemplateJsons).ok();
    }
    @Override
    @Transactional(rollbackFor = Exception.class);
    public Resp<String> deleteById(String id) {
        try {
            TestCaseTemplateJson testCaseTemplateJson = new TestCaseTemplateJson();
            testCaseTemplateJson.setId(id);
            testCaseTemplateJson.setDelFlag(1);
            return Result.deleteResult(testCaseTemplateJsonDAO.updateByPrimaryKeySelective(testCaseTemplateJson);
        }catch (BizException e){
            logger.error("class: TestCaseTemplateJsonServiceImpl#deleteById,error []" + e.getMessage();
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage();
        }
    }
    @Override
    public Resp<TestCaseTemplateJson> queryById(String id) {
        return new Resp.Builder<TestCaseTemplateJson>().setData(testCaseTemplateJsonDAO.selectByPrimaryKey(id).ok();
    }
}
}
}
