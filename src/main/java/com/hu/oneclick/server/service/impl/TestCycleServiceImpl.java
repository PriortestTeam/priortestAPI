package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.TestCycleDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.ModifyRecord;
import com.hu.oneclick.model.domain.TestCycle;
import com.hu.oneclick.server.service.ModifyRecordService;
import com.hu.oneclick.server.service.TestCycleService;
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
import java.util.Map;

@Service
public class TestCycleServiceImpl implements TestCycleService {

    private final static Logger logger = LoggerFactory.getLogger(TestCycleServiceImpl.class);


    private final ModifyRecordService modifyRecordService;

    private final JwtUserServiceImpl jwtUserService;

    private final TestCycleDao testCycleDao;


    public TestCycleServiceImpl(ModifyRecordService modifyRecordService, JwtUserServiceImpl jwtUserService, TestCycleDao testCycleDao) {
        this.modifyRecordService = modifyRecordService;
        this.jwtUserService = jwtUserService;
        this.testCycleDao = testCycleDao;
    }


    @Override
    public Resp<List<Map<String,String>>> queryTitles(String projectId, String title) {
        List<Map<String,String>> select = testCycleDao.queryTitles(projectId,title,jwtUserService.getMasterId());
        return new Resp.Builder<List<Map<String,String>>>().setData(select).total(select.size()).ok();
    }


    @Override
    public Resp<TestCycle> queryById(String id) {
        String masterId = jwtUserService.getMasterId();
        TestCycle testCycle = testCycleDao.queryById(id,masterId);
        return new Resp.Builder<TestCycle>().setData(testCycle).ok();
    }

    @Override
    public Resp<List<TestCycle>> queryList(TestCycle testCycle) {
        testCycle.queryListVerify();
        testCycle.setUserId(jwtUserService.getMasterId());
        List<TestCycle> select = testCycleDao.queryAll(testCycle);
        return new Resp.Builder<List<TestCycle>>().setData(select).total(select.size()).ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> insert(TestCycle testCycle) {
        try {
            //验证参数
            testCycle.verify();
            //验证是否存在
            verifyIsExist(testCycle.getTitle(),testCycle.getProjectId());
            testCycle.setUserId(jwtUserService.getMasterId());
            testCycle.setAuthorName(jwtUserService.getUserLoginInfo().getSysUser().getUserName());
            Date date = new Date();
            testCycle.setCreateTime(date);
            testCycle.setUpdateTime(date);
            return Result.addResult(testCycleDao.insert(testCycle));
        }catch (BizException e){
            logger.error("class: TestCycleServiceImpl#insert,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> update(TestCycle testCycle) {
        try {
            //验证是否存在
            verifyIsExist(testCycle.getTitle(),testCycle.getProjectId());
            testCycle.setUserId(jwtUserService.getMasterId());
            //新增修改字段记录
            modifyRecord(testCycle);
            return Result.updateResult(testCycleDao.update(testCycle));
        }catch (BizException e){
            logger.error("class: TestCycleServiceImpl#update,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> delete(String id) {
        try {
            TestCycle testCycle = new TestCycle();
            testCycle.setId(id);
            return Result.deleteResult(testCycleDao.delete(testCycle));
        }catch (BizException e){
            logger.error("class: TestCycleServiceImpl#delete,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }


    /**
     *  查重
     */
    private void verifyIsExist(String title,String projectId){
        if (StringUtils.isEmpty(title)){
            return;
        }
        TestCycle testCycle = new TestCycle();
        testCycle.setTitle(title);
        testCycle.setProjectId(projectId);
        testCycle.setId(null);
        if (testCycleDao.selectOne(testCycle) != null){
            throw new BizException(SysConstantEnum.DATE_EXIST.getCode(),testCycle.getTitle() + SysConstantEnum.DATE_EXIST.getValue());
        }
    }
    /**
     * 修改字段，进行记录
     * @param testCycle
     */
    private void modifyRecord(TestCycle testCycle) {
        try {
            TestCycle query = testCycleDao.queryById(testCycle.getId(), testCycle.getUserId());
            if (query == null){
                throw new RuntimeException();
            }

            Field[] fields = testCycle.getClass().getDeclaredFields();

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
                        || fields[i].get(testCycle) == null
                        || fields[i].get(testCycle) == "") {
                    continue;
                }

                String after = fields[i].get(testCycle).toString(); //获取用户需要修改的字段
                String before = fields2[i].get(query) == null || fields2[i].get(query) == ""
                        ? "" : fields2[i].get(query).toString();//获取数据库的原有的字段

                //值不相同
                if (!before.equals(after)) {

                    ModifyRecord mr = new ModifyRecord();
                    mr.setProjectId(query.getProjectId());
                    mr.setUserId(query.getUserId());
                    mr.setScope(OneConstant.SCOPE.ONE_TEST_CYCLE);
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
            modifyRecordService.insert(modifyRecords);
        } catch (IllegalAccessException e) {
            throw new BizException(SysConstantEnum.ADD_FAILED.getCode(),"修改字段新增失败！");
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
            case "runStatus":
                return "运行状态";
            case "故事":
                return "feature";
            case "status":
                return  "状态";
            case "lastRunDate":
                return "最后一次运行时间";
            case "lastModify":
                return "最后修改时间";
            case "featureId":
                return  "关联故事";
            case "sprintId":
                return  "关联迭代";
            case "version":
                return  "版本";
            case "ped":
                return  "ped";
        }
        return args;
    }
}
