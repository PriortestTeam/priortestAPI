package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.*;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.*;
import com.hu.oneclick.model.domain.dto.ExecuteTestCaseDto;
import com.hu.oneclick.model.domain.dto.LeftJoinDto;
import com.hu.oneclick.model.domain.dto.TestCycleDto;
import com.hu.oneclick.server.service.ModifyRecordsService;
import com.hu.oneclick.server.service.QueryFilterService;
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


    private final ModifyRecordsService modifyRecordsService;

    private final JwtUserServiceImpl jwtUserService;

    private final TestCycleDao testCycleDao;

    private final TestCaseDao testCaseDao;

    private final TestCaseStepDao testCaseStepDao;

    private final FeatureDao featureDao;

    private final TestCycleJoinTestCaseDao testCycleJoinTestCaseDao;

    private final SprintDao sprintDao;

    private final QueryFilterService queryFilterService;


    public TestCycleServiceImpl(ModifyRecordsService modifyRecordsService, JwtUserServiceImpl jwtUserService, TestCycleDao testCycleDao, TestCaseDao testCaseDao, TestCaseStepDao testCaseStepDao, FeatureDao featureDao, TestCycleJoinTestCaseDao testCycleJoinTestCaseDao, SprintDao sprintDao, QueryFilterService queryFilterService) {
        this.modifyRecordsService = modifyRecordsService;
        this.jwtUserService = jwtUserService;
        this.testCycleDao = testCycleDao;
        this.testCaseDao = testCaseDao;
        this.testCaseStepDao = testCaseStepDao;
        this.featureDao = featureDao;
        this.testCycleJoinTestCaseDao = testCycleJoinTestCaseDao;
        this.sprintDao = sprintDao;
        this.queryFilterService = queryFilterService;
    }


    @Override
    public Resp<List<LeftJoinDto>> queryTitles(String projectId, String title) {
        List<LeftJoinDto> select = testCycleDao.queryTitles(projectId,title,jwtUserService.getMasterId());
        return new Resp.Builder<List<LeftJoinDto>>().setData(select).total(select.size()).ok();
    }


    @Override
    public Resp<TestCycle> queryById(String id) {
        String masterId = jwtUserService.getMasterId();
        TestCycle testCycle = testCycleDao.queryById(id,masterId);

        //查询testCase 关联的 feature
        List<Feature> features = featureDao.queryTitlesByTestCycleId(testCycle.getId());
        testCycle.setFeatures(features);
        //查询sprint 的title
        if (features!=null && features.size() > 0){
            List<Sprint> sprints = sprintDao.queryTitlesInFeatureId(features);
            testCycle.setSprints(sprints);
        }
        return new Resp.Builder<TestCycle>().setData(testCycle).ok();
    }

    @Override
    public Resp<List<TestCycle>> queryList(TestCycleDto testCycle) {
        testCycle.queryListVerify();
        String masterId = jwtUserService.getMasterId();
        testCycle.setUserId(masterId);

        testCycle.setFilter(queryFilterService.mysqlFilterProcess(testCycle.getViewTreeDto(),masterId));

        List<TestCycle> select = testCycleDao.queryAll(testCycle);
        return new Resp.Builder<List<TestCycle>>().setData(select).total(select).ok();
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





    @Override
    public Resp<List<TestCase>> queryBindCaseList(String testCycleId) {
        List<TestCase> select = testCycleJoinTestCaseDao.queryBindCaseList(testCycleId);
        return new Resp.Builder< List<TestCase>>().setData(select).total(select).ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> bindCaseInsert(TestCycleJoinTestCase testCycleJoinTestCase) {
        try {
            List<TestCycleJoinTestCase> select = testCycleJoinTestCaseDao.queryList(testCycleJoinTestCase);
            if (select != null && select.size() > 0){
                throw new BizException(SysConstantEnum.DATE_EXIST.getCode(),"测试用例" + SysConstantEnum.DATE_EXIST.getValue());
            }
            int count = 0; //计数
            TestCycle testCycle = new TestCycle();
            List<String> strings = testCycleJoinTestCaseDao.queryTestCycleStatus(testCycleJoinTestCase.getTestCycleId());
            for (String s : strings) {
                if ("0".equals(s)){
                    count++;
                }
            }
            //如果全部为0 表示都没运行，所以记录 未运行状态，1 已执行但为执行外状态
            testCycle.setStatus(count == strings.size() ? 0 : 1);
            testCycle.setId(testCycleJoinTestCase.getTestCycleId());
            return Result.updateResult(testCycleJoinTestCaseDao.insert(testCycleJoinTestCase),testCycleDao.update(testCycle));
        }catch (BizException e){
            logger.error("class: TestCycleServiceImpl#bindCaseInsert,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> bindCaseDelete(String testCaseId) {
        try {
            return Result.deleteResult(testCycleJoinTestCaseDao.bindCaseDelete(testCaseId));
        }catch (BizException e){
            logger.error("class: TestCycleServiceImpl#bindCaseDelete,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> executeTestCase(ExecuteTestCaseDto executeTestCaseDto) {
        String userId = jwtUserService.getMasterId();
        Date date = new Date();
        int testCycleStatus; //执行过
        int testCycleRunStatus; //失败
        int count = 0;
        boolean flag = executeTestCaseDto.getStepStatus() == 2; //查看当前步骤用户选择成功还是失败
        try {
            //1 修改test case 最后一次执行的状态
            TestCase testCase = new TestCase();
            testCase.setId(executeTestCaseDto.getTestCaseId());
            testCase.setLastRunStatus(executeTestCaseDto.getStepStatus());
            testCase.setExecutedDate(date);
            testCase.setUserId(userId);
            //2 判断是否所有的test case 都被执行过，全部执行过后修改 test cycle 的status 为 complete
            List<Map<String,String>> select = testCycleJoinTestCaseDao.queryBindCaseRunStatus(executeTestCaseDto.getTestCycleId());
            for (Map<String, String> map : select) {
                //查看执行状态,1 为已运行，为 1 计数加1 count 数等于 list 查询结果数则表示全部已执行过
                if ("1".equals(map.get("executeStatus"))) {
                    count++;
                }
                //3 判断test cycle 下边的 testcase 是否都执行成功，凡有一个失败 则状态为失败
                if (flag){
                    String runStatus = map.get("runStatus");
                    if (runStatus != null){
                        String[] split = runStatus.split(",");
                        for (String s : split) {
                            if (!"2".equals(s)) {
                                flag = false;
                                break;
                            }
                        }
                    }
                }
            }
            //状态2 为已执行完，1 为未执行完
            testCycleStatus = count == select.size() ? 2 : 1;
            testCycleRunStatus = flag ? 2 : 1;
            TestCycle testCycle = new TestCycle();
            testCycle.setId(executeTestCaseDto.getTestCycleId());
            testCycle.setRunStatus(testCycleRunStatus);
            testCycle.setStatus(testCycleStatus);
            testCycle.setLastRunDate(date);
            testCycle.setUserId(userId);
            //4 步骤状态
            TestCaseStep testCaseStep = new TestCaseStep();
            testCaseStep.setId(executeTestCaseDto.getTestCaseStepId());
            testCaseStep.setTestCaseId(executeTestCaseDto.getTestCaseId());
            testCaseStep.setTestDate(date);
            testCaseStep.setStatus(executeTestCaseDto.getStepStatus());
            testCaseStep.setActualResult(executeTestCaseDto.getActualResult());
            //开始更新
            return Result.updateResult(testCycleDao.update(testCycle),
                    testCaseDao.update(testCase),
                    testCaseStepDao.update(testCaseStep));
        }catch (BizException e){
            logger.error("class: TestCycleServiceImpl#executeTestCase,error []" + e.getMessage());
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
                        || field.equals("description")
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
            modifyRecordsService.insert(modifyRecords);
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
            case "feature":
                return "故事";
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
