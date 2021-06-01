package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.FeatureDao;
import com.hu.oneclick.dao.TestCaseDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.Feature;
import com.hu.oneclick.model.domain.ModifyRecord;
import com.hu.oneclick.model.domain.TestCase;
import com.hu.oneclick.model.domain.dto.FeatureDto;
import com.hu.oneclick.model.domain.dto.LeftJoinDto;
import com.hu.oneclick.model.domain.dto.TestCaseDto;
import com.hu.oneclick.server.service.ModifyRecordsService;
import com.hu.oneclick.server.service.QueryFilterService;
import com.hu.oneclick.server.service.TestCaseService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author qingyang
 */
@Service
public class TestCaseServiceImpl implements TestCaseService {

    private final static Logger logger = LoggerFactory.getLogger(TestCaseServiceImpl.class);


    private final TestCaseDao testCaseDao;

    private final ModifyRecordsService modifyRecordsService;

    private final JwtUserServiceImpl jwtUserService;

    private final QueryFilterService queryFilterService;

    private final FeatureDao featureDao;

    public TestCaseServiceImpl(TestCaseDao testCaseDao, ModifyRecordsService modifyRecordsService, JwtUserServiceImpl jwtUserService, QueryFilterService queryFilterService, FeatureDao featureDao) {
        this.testCaseDao = testCaseDao;
        this.modifyRecordsService = modifyRecordsService;
        this.jwtUserService = jwtUserService;
        this.queryFilterService = queryFilterService;
        this.featureDao = featureDao;
    }


    @Override
    public Resp<List<LeftJoinDto>> queryTitles(String projectId, String title) {
        List<LeftJoinDto> select = testCaseDao.queryTitles(projectId,title,jwtUserService.getMasterId());
        return new Resp.Builder<List<LeftJoinDto>>().setData(select).total(select.size()).ok();
    }


    @Override
    public Resp<TestCase> queryById(String id) {
        String masterId = jwtUserService.getMasterId();
        TestCase testCase =  testCaseDao.queryById(id,masterId);
        return new Resp.Builder<TestCase>().setData(testCase).ok();
    }

    @Override
    public Resp<List<TestCase>> queryList(TestCaseDto testCase) {
        testCase.queryListVerify();
        String masterId = jwtUserService.getMasterId();
        testCase.setUserId(masterId);

        testCase.setFilter(queryFilterService.mysqlFilterProcess(testCase.getViewTreeDto(),masterId));

        List<TestCase> select = testCaseDao.queryList(testCase);
        return new Resp.Builder<List<TestCase>>().setData(select).total(select).ok();
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
            testCase.setAuthorName(jwtUserService.getUserLoginInfo().getSysUser().getUserName());
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
            //验证是否存在
            verifyIsExist(testCase.getTitle(),testCase.getProjectId());
            testCase.setUserId(jwtUserService.getMasterId());
            //新增修改字段记录
            modifyRecord(testCase);
            return Result.updateResult(testCaseDao.update(testCase));
        }catch (BizException e){
            logger.error("class: TestCaseServiceImpl#update,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    /**
     * 修改字段，进行记录
     * @param testCase
     */
    private void modifyRecord(TestCase testCase) {
       try {
           TestCase query = testCaseDao.queryById(testCase.getId(), testCase.getUserId());
           if (query == null){
               throw new RuntimeException();
           }

           Field[] fields = testCase.getClass().getDeclaredFields();

           Field[] fields2 = query.getClass().getDeclaredFields();
           List<ModifyRecord> modifyRecords = new ArrayList<>();
           for (int i = 0, len = fields.length; i < len; i++) {
               String field = fields[i].getName(); //获取字段名

               fields[i].setAccessible(true);
               fields2[i].setAccessible(true);

               if(field.equals("id")
                       || field.equals("projectId")
                       || field.equals("userId")
                       || field.equals("updateTime")
                       || field.equals("createTime")
                       || field.equals("scope")
                       || field.equals("serialVersionUID")
                       || field.equals("description")
                       || fields[i].get(testCase) == null
                       || fields[i].get(testCase) == "") {
                   continue;
               }

               String after = fields[i].get(testCase).toString(); //获取用户需要修改的字段
               String before = fields2[i].get(query) == null || fields2[i].get(query) == ""
                       ? "" : fields2[i].get(query).toString();//获取数据库的原有的字段

               //值不相同
               if (!before.equals(after)) {

                   ModifyRecord mr = new ModifyRecord();
                   mr.setProjectId(query.getProjectId());
                   mr.setUserId(query.getUserId());
                   mr.setScope(OneConstant.SCOPE.ONE_TEST_CASE);
                   mr.setModifyDate(new Date());
                   mr.setModifyUser(jwtUserService.getUserLoginInfo().getSysUser().getUserName());
                   mr.setBeforeVal(before);
                   mr.setAfterVal(after);
                   mr.setLinkId(query.getId());
                   mr.setModifyField(getCnField(field));
                   modifyRecords.add(mr);
               }
           }
           if (modifyRecords.size() <= 0){
               return;
           }
           modifyRecordsService.insert(modifyRecords);
       } catch (IllegalAccessException e) {
           throw new BizException(SysConstantEnum.ADD_FAILED.getCode(),"修改字段新增失败！");
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

    @Override
    public Resp<Feature> queryTestNeedByFeatureId(String featureId) {
        String masterId = jwtUserService.getMasterId();
        Feature featureDto = featureDao.queryById(featureId,masterId);
        return new Resp.Builder<Feature>().setData(featureDto).ok();
    }


    /**
     *  查重
     */
    private void verifyIsExist(String title,String projectId){
        if (StringUtils.isEmpty(title)){
            return;
        }
        TestCase testCase = new TestCase();
        testCase.setTitle(title);
        testCase.setProjectId(projectId);
        testCase.setId(null);
        if (testCaseDao.selectOne(testCase) != null){
            throw new BizException(SysConstantEnum.DATE_EXIST.getCode(),testCase.getTitle() + SysConstantEnum.DATE_EXIST.getValue());
        }
    }

    /**
     * 获取字段对应中文字义
     * @param args
     * @return
     */
    private String getCnField(String args){
        switch (args) {
            case "title":
                return  "名称";
            case "priority":
                return "优先权";
            case "故事":
                return "feature";
            case "status":
                return  "状态";
            case "description":
                return "描述";
            case "executedDate":
                return "执行时间";
            case "authorName":
                return  "管理人";
            case "browser":
                return  "浏览器";
            case "platform":
                return  "平台";
            case "version":
                return  "版本";
            case "caseCategory":
                return  "用例类别";
            case "caseType":
                return  "用例类型";
            case "externaId":
                return  "外部ID";
            case "env":
                return  "环境";
            case "preCondition":
                return  "前提条件";
        }
        return args;
    }


}
