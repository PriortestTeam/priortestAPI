package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.TestCaseDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.TestCase;
import com.hu.oneclick.server.service.TestCaseService;
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
public class TestCaseServiceImpl implements TestCaseService {

    private final static Logger logger = LoggerFactory.getLogger(TestCaseServiceImpl.class);


    private final TestCaseDao testCaseDao;

    private final JwtUserServiceImpl jwtUserService;

    public TestCaseServiceImpl(TestCaseDao testCaseDao, JwtUserServiceImpl jwtUserService) {
        this.testCaseDao = testCaseDao;
        this.jwtUserService = jwtUserService;
    }


    @Override
    public Resp<TestCase> queryById(String id) {
        String masterId = jwtUserService.getMasterId();
        TestCase testCase =  testCaseDao.queryById(id,masterId);
        return new Resp.Builder<TestCase>().setData(testCase).ok();
    }

    @Override
    public Resp<List<TestCase>> queryList(TestCase testCase) {
        testCase.queryListVerify();
        testCase.setUserId(jwtUserService.getMasterId());
        List<TestCase> select = testCaseDao.select(testCase);
        return new Resp.Builder<List<TestCase>>().setData(select).total(select.size()).ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> insert(TestCase testCase) {
        try {
            //验证参数
            testCase.verify();
            //验证是否存在
            verifyIsExist(testCase.getTitle(),testCase.getProjectId());
            testCase.setUserId(jwtUserService.getMasterId());
            Date date = new Date();
            testCase.setCreateTime(date);
            testCase.setUpdateTime(date);
            return Result.addResult(testCaseDao.insert(testCase));
        }catch (BizException e){
            logger.error("class: TestCaseServiceImpl#insert,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> update(TestCase testCase) {
        try {
            //验证参数
            testCase.verify();
            //验证是否存在
            verifyIsExist(testCase.getTitle(),testCase.getProjectId());
            testCase.setUserId(jwtUserService.getMasterId());
            return Result.updateResult(testCaseDao.update(testCase));
        }catch (BizException e){
            logger.error("class: TestCaseServiceImpl#update,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> delete(String id) {
        try {
            TestCase testCase = new TestCase();
            testCase.setId(id);
            return Result.deleteResult(testCaseDao.delete(testCase));
        }catch (BizException e){
            logger.error("class: TestCaseServiceImpl#delete,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }



    /**
     *  查重
     */
    private void verifyIsExist(String title,String projectId){
        TestCase testCase = new TestCase();
        testCase.setTitle(title);
        testCase.setProjectId(projectId);
        testCase.setId(null);
        if (testCaseDao.selectOne(testCase) != null){
            throw new BizException(SysConstantEnum.DATE_EXIST.getCode(),testCase.getTitle() + SysConstantEnum.DATE_EXIST.getValue());
        }
    }

}
