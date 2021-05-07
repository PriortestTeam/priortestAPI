package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.dao.TestCaseStepDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.TestCaseStep;
import com.hu.oneclick.server.service.TestCaseStepService;
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
public class TestCaseStepServiceImpl implements TestCaseStepService {


    private final static Logger logger = LoggerFactory.getLogger(TestCaseStepServiceImpl.class);


    private final TestCaseStepDao testCaseStepDao;

    public TestCaseStepServiceImpl(TestCaseStepDao testCaseStepDao) {
        this.testCaseStepDao = testCaseStepDao;
    }


    @Override
    public Resp<TestCaseStep> queryById(String id,String testCaseId) {
        TestCaseStep query = new TestCaseStep();
        query.setTestCaseId(testCaseId);
        query.setId(id);
        TestCaseStep testCaseStep = testCaseStepDao.selectOne(query);
        return new Resp.Builder<TestCaseStep>().setData(testCaseStep).ok();
    }

    @Override
    public Resp<List<TestCaseStep>> queryList(TestCaseStep testCaseStep) {
        testCaseStep.queryListVerify();
        List<TestCaseStep> select = testCaseStepDao.queryList(testCaseStep);
        return new Resp.Builder<List<TestCaseStep>>().setData(select).total(select).ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> insert(TestCaseStep testCaseStep) {
        try {
            //验证参数
            testCaseStep.verify();
            testCaseStep.setCreateTime(new Date());
            return Result.addResult(testCaseStepDao.insert(testCaseStep));
        }catch (BizException e){
            logger.error("class: TestCaseStepServiceImpl#insert,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> update(TestCaseStep testCaseStep) {
        try {
            //验证参数
            return Result.updateResult(testCaseStepDao.update(testCaseStep));
        }catch (BizException e){
            logger.error("class: TestCaseStepServiceImpl#update,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> delete(String id) {
        try {
            TestCaseStep query = new TestCaseStep();
            query.setId(id);
            return Result.deleteResult(testCaseStepDao.delete(query));
        }catch (BizException e){
            logger.error("class: TestCaseStepServiceImpl#delete,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }
}

