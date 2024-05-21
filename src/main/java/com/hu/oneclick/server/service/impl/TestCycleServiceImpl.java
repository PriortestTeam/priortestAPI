package com.hu.oneclick.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.*;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.TestCycle;
import com.hu.oneclick.model.domain.dto.LeftJoinDto;
import com.hu.oneclick.model.domain.dto.SignOffDto;
import com.hu.oneclick.model.domain.dto.TestCycleSaveDto;
import com.hu.oneclick.model.domain.param.TestCycleParam;
import com.hu.oneclick.server.service.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class TestCycleServiceImpl extends ServiceImpl<TestCycleDao, TestCycle> implements TestCycleService {

    private final static Logger logger = LoggerFactory.getLogger(TestCycleServiceImpl.class);


    @Resource
    private ModifyRecordsService modifyRecordsService;
    @Resource
    private JwtUserServiceImpl jwtUserService;
    @Resource
    private TestCycleDao testCycleDao;
    @Resource
    private TestCaseDao testCaseDao;
    @Resource
    private TestCaseStepDao testCaseStepDao;
    @Resource
    private FeatureDao featureDao;
    @Resource
    private TestCycleJoinTestCaseDao testCycleJoinTestCaseDao;
    @Resource
    private SprintDao sprintDao;
    @Resource
    private QueryFilterService queryFilterService;
    @Resource
    private TestCaseExcutionDao testCaseExcutionDao;
    @Resource
    private TestCycleJoinTestStepDao testCycleJoinTestStepDao;
    @Resource
    private IssueDao issueDao;
    @Resource
    private TestCycleScheduleModelDao testCycleScheduleModelDao;
    @Resource
    private TestCycleScheduleDao testCycleScheduleDao;
    @Resource
    private SystemConfigService systemConfigService;
    @Resource
    private CustomFieldDataService customFieldDataService;


    @Override
    public Resp<List<LeftJoinDto>> queryTitles(String projectId, String title) {
        List<LeftJoinDto> select = testCycleDao.queryTitles(projectId, title, jwtUserService.getMasterId());
        return new Resp.Builder<List<LeftJoinDto>>().setData(select).total(select.size()).ok();
    }
//
//
    @Override
    public Resp<TestCycle> queryById(String id) {
        String masterId = jwtUserService.getMasterId();
        TestCycle testCycle = testCycleDao.queryById(id, masterId);
//
//        //查询testCase 关联的 feature
//        testCycle = Optional.ofNullable(testCycle).orElse(new TestCycle());
//        List<Feature> features = featureDao.queryTitlesByTestCycleId(testCycle.getId());
//        testCycle.setFeatures(features);
//        //查询sprint 的title
//        if (features != null && features.size() > 0) {
//            List<Sprint> sprints = sprintDao.queryTitlesInFeatureId(features);
//            testCycle.setSprints(sprints);
//        }
//
//        testCycle.setCustomFieldDatas(customFieldDataService.testCycleRenderingCustom(id));
        return new Resp.Builder<TestCycle>().setData(testCycle).ok();
    }
//
//    @Override
//    public Resp<List<TestCycle>> queryList(TestCycleDto testCycle) {
//        testCycle.queryListVerify();
//        String masterId = jwtUserService.getMasterId();
//        testCycle.setUserId(masterId);
//
//        testCycle.setFilter(queryFilterService.mysqlFilterProcess(testCycle.getViewTreeDto(), masterId));
//
//        List<TestCycle> select = testCycleDao.queryAll(testCycle);
//        return new Resp.Builder<List<TestCycle>>().setData(select).total(select).ok();
//    }
//
//    /**
//     * update customfiled
//     *
//     * @Param: [testCycle]
//     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
//     * @Author: MaSiyi
//     * @Date: 2021/12/27
//     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Resp<String> insert(TestCycle testCycle) {
//        try {
//            //验证参数
//            testCycle.verify();
//            //验证是否存在
//            verifyIsExist(testCycle.getTitle(), testCycle.getProjectId());
//            testCycle.setUserId(jwtUserService.getMasterId());
//            testCycle.setAuthorName(jwtUserService.getUserLoginInfo().getSysUser().getUserName());
//            Date date = new Date();
//            testCycle.setCreateTime(date);
//            testCycle.setUpdateTime(date);
//            int insertFlag = testCycleDao.insert(testCycle);
//            if (insertFlag > 0) {
//                List<CustomFieldData> customFieldDatas = testCycle.getCustomFieldDatas();
//                insertFlag = customFieldDataService.insertTestCycleCustomData(customFieldDatas, testCycle);
//            }
//            return Result.addResult(insertFlag);
//        } catch (BizException e) {
//            logger.error("class: TestCycleServiceImpl#insert,error []" + e.getMessage());
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
//        }
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Resp<String> update(TestCycle testCycle) {
//        try {
//            //验证是否存在
//            verifyIsExist(testCycle.getTitle(), testCycle.getProjectId());
//            testCycle.setUserId(jwtUserService.getMasterId());
//            //新增修改字段记录
//            modifyRecord(testCycle);
//            return Result.updateResult(testCycleDao.update(testCycle));
//        } catch (BizException e) {
//            logger.error("class: TestCycleServiceImpl#update,error []" + e.getMessage());
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
//        }
//    }
//
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Resp<String> delete(String id) {
//        try {
//            TestCycle testCycle = new TestCycle();
//            testCycle.setId(id);
//            return Result.deleteResult(testCycleDao.delete(new LambdaQueryWrapper<TestCycle>().eq(TestCycle::getId, id)));
//        } catch (BizException e) {
//            logger.error("class: TestCycleServiceImpl#delete,error []" + e.getMessage());
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
//        }
//    }
//
//
//    @Override
//    public Resp<List<TestCase>> queryBindCaseList(String testCycleId) {
//        List<TestCase> select = testCycleJoinTestCaseDao.queryBindCaseList(testCycleId);
//        return new Resp.Builder<List<TestCase>>().setData(select).total(select).ok();
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Resp<String> bindCaseInsert(TestCycleJoinTestCase testCycleJoinTestCase) {
//        try {
//            List<TestCycleJoinTestCase> select = testCycleJoinTestCaseDao.queryList(testCycleJoinTestCase);
//            if (select != null && select.size() > 0) {
//                throw new BizException(SysConstantEnum.DATE_EXIST.getCode(), "测试用例" + SysConstantEnum.DATE_EXIST.getValue());
//            }
//            int count = 0; //计数
//            TestCycle testCycle = new TestCycle();
//            List<String> strings = testCycleJoinTestCaseDao.queryTestCycleStatus(testCycleJoinTestCase.getTestCycleId());
//            for (String s : strings) {
//                if ("0".equals(s)) {
//                    count++;
//                }
//            }
//            //如果全部为0 表示都没运行，所以记录 未运行状态，1 已执行但为执行外状态
//            testCycle.setStatus(count == strings.size() ? 0 : 1);
//            testCycle.setId(testCycleJoinTestCase.getTestCycleId());
//            return Result.updateResult(testCycleJoinTestCaseDao.insert(testCycleJoinTestCase), testCycleDao.update(testCycle));
//        } catch (BizException e) {
//            logger.error("class: TestCycleServiceImpl#bindCaseInsert,error []" + e.getMessage());
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
//        }
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Resp<String> bindCaseDelete(String testCaseId) {
//        try {
//            return Result.deleteResult(testCycleJoinTestCaseDao.bindCaseDelete(testCaseId));
//        } catch (BizException e) {
//            logger.error("class: TestCycleServiceImpl#bindCaseDelete,error []" + e.getMessage());
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
//        }
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Resp<String> executeTestCase(ExecuteTestCaseDto executeTestCaseDto) {
//        String userId = jwtUserService.getMasterId();
//        Date date = new Date();
//        int testCycleStatus; //执行过
//        int testCycleRunStatus; //失败
//        int count = 0;
//        boolean flag = executeTestCaseDto.getStepStatus() == 2; //查看当前步骤用户选择成功还是失败
//        try {
//            //1 修改test case 最后一次执行的状态
//            //TestCase testCase = new TestCase();
//            //testCase.setId(executeTestCaseDto.getTestCaseId());
//            //testCase.setLastRunStatus(executeTestCaseDto.getStepStatus());
//            //testCase.setExecutedDate(date);
//            //testCase.setUserId(userId);
//            //2 判断是否所有的test case 都被执行过，全部执行过后修改 test cycle 的status 为 complete
//            List<Map<String, String>> select = testCycleJoinTestCaseDao.queryBindCaseRunStatus(executeTestCaseDto.getTestCycleId());
//            for (Map<String, String> map : select) {
//                //查看执行状态,1 为已运行，为 1 计数加1 count 数等于 list 查询结果数则表示全部已执行过
//                if ("1".equals(map.get("executeStatus"))) {
//                    count++;
//                }
//                //3 判断test cycle 下边的 testcase 是否都执行成功，凡有一个失败 则状态为失败
//                if (flag) {
//                    String runStatus = map.get("runStatus");
//                    if (runStatus != null) {
//                        String[] split = runStatus.split(",");
//                        for (String s : split) {
//                            if (!"2".equals(s)) {
//                                flag = false;
//                                break;
//                            }
//                        }
//                    }
//                }
//            }
//            //状态2 为已执行完，1 为未执行完
//            testCycleStatus = count == select.size() ? 2 : 1;
//            testCycleRunStatus = flag ? 2 : 1;
//            TestCycle testCycle = new TestCycle();
//            testCycle.setId(executeTestCaseDto.getTestCycleId());
//            testCycle.setRunStatus(testCycleRunStatus);
//            testCycle.setStatus(testCycleStatus);
//            testCycle.setLastRunDate(date);
//            testCycle.setUserId(userId);
//            //4 步骤状态
//            TestCaseStep testCaseStep = new TestCaseStep();
//            //testCaseStep.setId(executeTestCaseDto.getTestCaseStepId());
//            //testCaseStep.setTestCaseId(executeTestCaseDto.getTestCaseId());
//            //testCaseStep.setTestDate(date);
//            //testCaseStep.setStatus(executeTestCaseDto.getStepStatus());
//            //testCaseStep.setActualResult(executeTestCaseDto.getActualResult());
//            //开始更新
//            //return Result.updateResult(testCycleDao.update(testCycle),
//            //        testCaseDao.update(testCase),
//            //        testCaseStepDao.update(testCaseStep));
//        } catch (BizException e) {
//            logger.error("class: TestCycleServiceImpl#executeTestCase,error []" + e.getMessage());
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
//        }
////        try {
////            // 当前用户下，当前测试周期下的当前测试用例前面步骤是否有执行失败的
////            TestCaseStep testCaseStep = new TestCaseStep();
////            testCaseStep.setId(executeTestCaseDto.getTestCaseStepId());
////            testCaseStep.setTestCaseId(executeTestCaseDto.getTestCaseId());
////            testCaseStep.setUserId(userId);
////            testCaseStep.setTestCycleId(executeTestCaseDto.getTestCycleId());
////            int count = 0;
////            count = testCaseStepDao.queryCount(testCaseStep);
////            if (count > 0 ) {
////                throw new BizException("","当前测试用例有执行失败步骤，不可继续执行");
////            }
////
////
////            // TODO 将该用例此步骤后面的步骤都置为未执行状态 如果返回ccount=1则代表后面还有步骤
////            int ccount = 0;
////
////            // 更新测试周期和测试用例关联信息表
////            TestCycleJoinTestCase testCycleJoinTestCase = new TestCycleJoinTestCase();
////            if (flag) {
////                if (ccount == 1) { // 执行成功但未完成
////                    testCycleJoinTestCase.setExecuteStatus("1");// 0:未执行 1:未完成  2:执行完成
////                    testCycleJoinTestCase.setRunStatus("2");// 执行成功 0:未执行 1:执行失败 2:执行成功
////                } else {// 执行成功且完成
////                    testCycleJoinTestCase.setExecuteStatus("2");// 0:未执行 1:未完成  2:执行完成
////                    testCycleJoinTestCase.setRunStatus("2");// 执行成功 0:未执行 1:执行失败 2:执行成功
////                }
////            } else {
////                testCycleJoinTestCase.setExecuteStatus("2");// 0:未执行 1:未完成  2:执行完成
////                testCycleJoinTestCase.setRunStatus("1");// 执行成功 0:未执行 1:执行失败 2:执行成功
////            }
////            //testCycleJoinTestCase.setExecuteStatus(StringUtil.isEmpty(String.valueOf(executeTestCaseDto.getStepStatus())) ? "0" : String.valueOf(executeTestCaseDto.getStepStatus()));
////            testCycleJoinTestCase.setTestCaseId(executeTestCaseDto.getTestCaseId());
////            testCycleJoinTestCase.setTestCycleId(executeTestCaseDto.getTestCycleId());
////            testCycleJoinTestCaseDao.update(testCycleJoinTestCase);
////
////            int testCycleRunStatus = 2;// test_cycle默认执行状态为成功 0:未执行 1:执行失败 2:执行成功
////            int testCycleStatus = 1;// test_cycle默认状态为完成 0:未执行 1:未完成  2:执行完成
////            // 查询test_cycle_join_test_case表该周期下所有用例的执行状态
////            testCycleJoinTestCase.setExecuteStatus(null);// 查询条件去掉状态
////            testCycleJoinTestCase.setTestCaseId(null);
////            List<TestCycleJoinTestCase> list = testCycleJoinTestCaseDao.queryList(testCycleJoinTestCase);
////            if (flag) {// 该测试用例当前步骤执行成功
////                for(TestCycleJoinTestCase testCJTC : list){
////                    if ("1".equals(testCJTC.getRunStatus())) {// 只要有失败的用例，则该测试周期执行状态为失败
////                        testCycleRunStatus = 1;// 失败
////                    }
////                    if (!"2".equals(testCJTC.getExecuteStatus())) {// 只要有未执行或者未执行完成，测试周期状态为未完成
////                        testCycleStatus = 1;
////                    }
////                }
////
////
////            } else {// 该测试用例当前步骤执行失败
////                testCycleRunStatus = 1;// 失败
////                for(TestCycleJoinTestCase testCJTC : list){
////                    if (!"2".equals(testCJTC.getExecuteStatus())) {// 只要有未执行或者未执行完成，测试周期状态为未完成
////                        testCycleStatus = 1;
////                    }
////                }
////            }
////            TestCycle testCycle = new TestCycle();
////            testCycle.setId(executeTestCaseDto.getTestCycleId());
////            testCycle.setRunStatus(testCycleRunStatus);
////            testCycle.setStatus(testCycleStatus);
////            testCycle.setLastRunDate(date);
////            testCycle.setUserId(userId);
////            testCycleDao.update(testCycle);
////
////            // 更新测试用例最后执行人，最终执行状态
////            TestCase testCase = new TestCase();
////
////            // 更新测试用例步骤表
////            testCaseStepDao.update(testCaseStep);
////
////
////
////            testCaseDao.update(testCase);
////            testCaseStepDao.update(testCaseStep);
////        }catch (BizException e) {
////            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
////            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
////        }
////        return Result.updateResult();
//        return Result.updateResult();
//    }


//    /**
//     * 查重
//     */
//    private void verifyIsExist(String title, String projectId) {
//        if (StringUtils.isEmpty(title)) {
//            return;
//        }
//        TestCycle testCycle = new TestCycle();
//        testCycle.setTitle(title);
//        testCycle.setProjectId(projectId);
//        testCycle.setId(null);
//        if (testCycleDao.selectOne(new LambdaQueryWrapper<TestCycle>().eq(TestCycle::getTitle, testCycle.getTitle()).eq(TestCycle::getProjectId, testCycle.getProjectId())) != null) {
//            throw new BizException(SysConstantEnum.DATE_EXIST.getCode(), testCycle.getTitle() + SysConstantEnum.DATE_EXIST.getValue());
//        }
//    }
//
//    /**
//     * 修改字段，进行记录
//     *
//     * @param testCycle
//     */
//    private void modifyRecord(TestCycle testCycle) {
//        try {
//            TestCycle query = testCycleDao.queryById(testCycle.getId(), testCycle.getUserId());
//            if (query == null) {
//                throw new RuntimeException();
//            }
//
//            Field[] fields = testCycle.getClass().getDeclaredFields();
//
//            Field[] fields2 = query.getClass().getDeclaredFields();
//            List<ModifyRecord> modifyRecords = new ArrayList<>();
//            for (int i = 0, len = fields.length; i < len; i++) {
//                String field = fields[i].getName(); //获取字段名
//
//                fields[i].setAccessible(true);
//                fields2[i].setAccessible(true);
//
//                if (field.equals("id")
//                        || field.equals("projectId")
//                        || field.equals("userId")
//                        || field.equals("updateTime")
//                        || field.equals("createTime")
//                        || field.equals("scope")
//                        || field.equals("serialVersionUID")
//                        || field.equals("description")
//                        || fields[i].get(testCycle) == null
//                        || fields[i].get(testCycle) == "") {
//                    continue;
//                }
//
//                String after = fields[i].get(testCycle).toString(); //获取用户需要修改的字段
//                String before = fields2[i].get(query) == null || fields2[i].get(query) == ""
//                        ? "" : fields2[i].get(query).toString();//获取数据库的原有的字段
//
//                //值不相同
//                if (!before.equals(after)) {
//
//                    ModifyRecord mr = new ModifyRecord();
//                    mr.setProjectId(query.getProjectId());
//                    mr.setUserId(query.getUserId());
//                    mr.setScope(OneConstant.SCOPE.ONE_TEST_CYCLE);
//                    mr.setModifyDate(new Date());
//                    mr.setModifyUser(jwtUserService.getUserLoginInfo().getSysUser().getUserName());
//                    mr.setBeforeVal(before);
//                    mr.setAfterVal(after);
//                    mr.setLinkId(query.getId());
//                    mr.setModifyField(getCnField(field));
//                    modifyRecords.add(mr);
//                }
//            }
//            if (modifyRecords.size() <= 0) {
//                return;
//            }
//            modifyRecordsService.insert(modifyRecords);
//        } catch (IllegalAccessException e) {
//            throw new BizException(SysConstantEnum.ADD_FAILED.getCode(), "修改字段新增失败！");
//        }
//    }
//
//
//    /**
//     * 获取字段对应中文字义
//     *
//     * @param args
//     * @return
//     */
//    private String getCnField(String args) {
//        switch (args) {
//            case "title":
//                return "名称";
//            case "runStatus":
//                return "运行状态";
//            case "feature":
//                return "故事";
//            case "status":
//                return "状态";
//            case "lastRunDate":
//                return "最后一次运行时间";
//            case "lastModify":
//                return "最后修改时间";
//            case "featureId":
//                return "关联故事";
//            case "sprintId":
//                return "关联迭代";
//            case "version":
//                return "版本";
//            case "ped":
//                return "ped";
//        }
//        return args;
//    }
//
    @Override
    public Resp<List<Map<String, String>>> getTestCycleVersion(String projectId, String env, String version) {
        List<Map<String, String>> testCycleVersion = testCycleDao.getTestCycleVersion(projectId, env, version);
        return new Resp.Builder<List<Map<String, String>>>().setData(testCycleVersion).ok();
    }
//
    @Override
    public List<Map<String, String>> getAllTestCycle(SignOffDto signOffDto) {

        List<Map<String, String>> allTestCycle = testCycleDao.getAllTestCycle(signOffDto.getProjectId(), signOffDto.getVersion(), signOffDto.getEnv(), signOffDto.getTestCycle());
        return allTestCycle;
    }
//
//    /**
//     * 点击测试周期中某个测试用例前面的run按钮
//     *
//     * @param executeTestCaseDto
//     * @return
//     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Resp<Map<String, Object>> runTestCycleTc(ExecuteTestCaseDto executeTestCaseDto) {
////        logger.error("class: TestCycleServiceImpl#runTestCycleTc,error []" );
//        String userId = jwtUserService.getMasterId();
//        Date date = new Date();
//        int cycleRunCount = 0;
//        Map<String, Object> resultMap = new HashMap<String, Object>();
//
//        TestCycleJoinTestStep testCycleJoinTestStep = new TestCycleJoinTestStep();
//        testCycleJoinTestStep.setTestCaseId(executeTestCaseDto.getTestCaseId());
//        testCycleJoinTestStep.setTestCycleId(executeTestCaseDto.getTestCycleId());
//
//        try {
//            List<TestCycleJoinTestCase> list = queryTestCycleJoinTestCaseList(executeTestCaseDto);
//            if (list == null || list.size() == 0) {
//                throw new BizException("", "未查询到当前测试用例执行记录");
//            }
//            TestCycleJoinTestCase resultTestCycleJoinTestCase = list.get(0);
//            cycleRunCount = resultTestCycleJoinTestCase.getRunCount();
//            if (cycleRunCount == 0) {// 当此测试用例从未执行时，点击run，不做任何改变,只返回當前測試用例步驟
//                List<TestCycleJoinTestStep> testCycleJoinTestStepList = testCycleJoinTestStepDao.queryList(testCycleJoinTestStep);
//                resultMap.put("testCycleJoinTestStepList", testCycleJoinTestStepList);
//                return new Resp.Builder<Map<String, Object>>().setData(resultMap).total(resultMap).ok();
//            }
//
//            // 更新test_cycle_join_test_case的Run Count +1 , Run Duration =00;不更新执行状态和运行状态，否则之前执行过的步骤会作废
////            testCycleJoinTestCase.setStepStatus(0);// 0:Not Run; 1:PASS; 2:Fail
////            testCycleJoinTestCase.setRunStatus(0);// 0:Not Run; 1:PASS; 2:Fail; 3:Un_Complete
//            TestCycleJoinTestCase testCycleJoinTestCase = new TestCycleJoinTestCase();
//            testCycleJoinTestCase.setTestCaseId(executeTestCaseDto.getTestCaseId());
//            testCycleJoinTestCase.setTestCycleId(executeTestCaseDto.getTestCycleId());
//            testCycleJoinTestCase.setRunCount(cycleRunCount + 1);
//            testCycleJoinTestCase.setUserId(userId);
//            testCycleJoinTestCase.setRunDuration(0);
//            testCycleJoinTestCase.setUpdateTime(date);
//            testCycleJoinTestCaseDao.updateTestCycleJoinTestCase(testCycleJoinTestCase);
//
//            // 更新测试用例test_case表的状态为not run、执行时间、执行人等
//            //TestCase testCase = new TestCase();
//            //testCase.setId(executeTestCaseDto.getTestCaseId());
//            //testCase.setExecutedDate(date);
//            //testCase.setStepStatus(0);// 0:Not Run; 1:PASS; 2:Fail
//            //testCase.setRunStatus(0);// 0:Not Run; 1:PASS; 2:Fail; 3:Un_Complete
//            //testCase.setUserId(userId);
//            //testCaseDao.update(testCase);
//
//            // 更新测试周期下测试用例步骤运行次数
//
//            testCycleJoinTestStepDao.updateRunCount(testCycleJoinTestStep);// 当前测试周期下测试用例步骤表所有步骤运行次数 runCount+1
//
//            // 从test_cycl_Join_Test_Step insert(每次运行都新增一条记录)数据到TestCaseExcution表,并将TestCaseExcution全部置为初始状态
//            TestCaseExcution testCaseExcution = new TestCaseExcution();
//            testCaseExcutionDao.createTestCaseExcutionDate();
//
//            // 更新test_cycle
//            updateTestCycle(executeTestCaseDto, userId);
//
//            // 返回信息 testCycleJoinTestStepList、history
//            List<TestCycleJoinTestStep> testCycleJoinTestStepList2 = testCycleJoinTestStepDao.queryList(testCycleJoinTestStep);
//            List<TestCaseExcution> excutions = testCaseExcutionDao.queryHistoryByTestCaseId(testCaseExcution);
//            resultMap.put("testCycleJoinTestStepList", testCycleJoinTestStepList2);
//            resultMap.put("history", excutions);
//            return new Resp.Builder<Map<String, Object>>().setData(resultMap).total(resultMap).ok();
//        } catch (BizException e) {
//            logger.error("class: TestCycleServiceImpl#runTestCycleTc,error []" + e.getMessage());
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            return new Resp.Builder<Map<String, Object>>().buildResult(e.getCode(), e.getMessage());
//        }
//    }
//
//    /**
//     * 执行当前测试周期下某个测试用例的步骤
//     *
//     * @param executeTestCaseDto
//     * @return
//     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Resp<Map<String, Object>> excute(ExecuteTestCaseDto executeTestCaseDto) {
//        String userId = jwtUserService.getMasterId();
//        Date date = new Date();
//        try {
//            //查询当前测试用例下的测试步骤表TestCycleJoin_TestStep，前面步骤是否有执行失败的，如果有则不让执行
//            TestCycleJoinTestStep testCycleJoinTestStep = new TestCycleJoinTestStep();
//            testCycleJoinTestStep.setTestCaseId(executeTestCaseDto.getTestCaseId());
//            testCycleJoinTestStep.setTestCycleId(executeTestCaseDto.getTestCycleId());
//            List<TestCycleJoinTestStep> testCycleJoinTestStepList = testCycleJoinTestStepDao.queryList(testCycleJoinTestStep);
//            if (testCycleJoinTestStepList == null || testCycleJoinTestStepList.size() <= 0) {
//                throw new BizException("", "当前测试用例无内容");
//            }
//            for (TestCycleJoinTestStep tcjts : testCycleJoinTestStepList) {
//                if (Integer.valueOf((String) tcjts.getStep()) < executeTestCaseDto.getStep()) {
//                    if (tcjts.getStepStatus() == 2) {// 0:Not Run; 1:PASS; 2:Fail
//                        throw new BizException("", "当前测试用例有执行失败步骤，不可继续执行");
//                    }
//
//                }
//            }
//
//            int cycleRunCount = 0;
//            List<TestCycleJoinTestCase> list = queryTestCycleJoinTestCaseList(executeTestCaseDto);
//            if (list == null || list.size() == 0) {
//                throw new BizException("", "为查询到当前测试用例执行记录");
//            }
//
//            TestCase testCase = new TestCase();
//            TestCycleJoinTestCase resultTestCycleJoinTestCase = list.get(0);
//            cycleRunCount = resultTestCycleJoinTestCase.getRunCount();
//            if (cycleRunCount == 0) {// 该测试周期下测试用例首次运行
//
//                resultTestCycleJoinTestCase.setRunDuration(executeTestCaseDto.getRunDuration());
//                resultTestCycleJoinTestCase.setStepStatus(executeTestCaseDto.getStepStatus());// 0:Not Run; 1:PASS; 2:Fail
//
//                //testCase.setStepStatus(executeTestCaseDto.getStepStatus());// 0:Not Run; 1:PASS; 2:Fail
//
//                /**
//                 * resultTestCycleJoinTestCase、testCycleJoinTestStep和TestCaseExcution的runCount同步
//                 * TODO 保证第一次run进来后从第一步开始执行
//                 */
//                if (executeTestCaseDto.getStep() == 1) {// 第一步 TestCycleJoinTestCase：Run Count +1, 执行状态，完成状态，Run duration（前端直接送过来）
//                    resultTestCycleJoinTestCase.setRunCount(cycleRunCount + 1);
//                    testCycleJoinTestStep.setRunCount(cycleRunCount + 1);
//                }
//            }
//
//            if (executeTestCaseDto.getStep() == testCycleJoinTestStepList.size()) {
//                resultTestCycleJoinTestCase.setRunStatus(executeTestCaseDto.getStepStatus());// 执行完毕，当前步骤状态就是测试用例最终状态
//                testCase.setRunStatus(executeTestCaseDto.getStepStatus());
//            } else if (executeTestCaseDto.getStep() < testCycleJoinTestStepList.size()) {// 未执行完
//                resultTestCycleJoinTestCase.setRunStatus(3);// 0:Not Run; 1:PASS; 2:Fail; 3:Un_Complete
//                testCase.setRunStatus(3);
//            }
//            testCycleJoinTestCaseDao.updateTestCycleJoinTestCase(resultTestCycleJoinTestCase);
//
//            testCase.setId(Convert.toLong(executeTestCaseDto.getTestCaseId()));
//            testCase.setCreateUserId(Convert.toLong(userId));
//            testCase.setUpdateTime(date);
//            testCaseDao.update(testCase);
//
//            // 如果执行失败则创建issue
//            String issueId = getRandom(10000);
//            if (executeTestCaseDto.getStepStatus() == 2) {// 如果失败则新增issue表
//                Issue issue = new Issue();
////                issue.setUserId(jwtUserService.getMasterId());
////                issue.setAuthor(jwtUserService.getUserLoginInfo().getSysUser().getUserName());
//                issue.setCreateTime(date);
//                issue.setUpdateTime(date);
////                issue.setId(issueId);
//                testCaseExcutionDao.insertIssue(issue);
//            }
//
//            // 更新TestCycleJoin_TestStep对应步骤的 执行状态，更新时间，执行人，如果是run进来的首次执行则要更新执行次数runCount
//            testCycleJoinTestStep.setStep((String.valueOf(executeTestCaseDto.getStep())));
//            testCycleJoinTestStep.setStepStatus(executeTestCaseDto.getStepStatus());
//            testCycleJoinTestStep.setRunner(userId);
//            testCycleJoinTestStep.setUpdateTime(date);
//            testCycleJoinTestStep.setIssueId(issueId);
//            testCycleJoinTestStepDao.update(testCycleJoinTestStep);
//
//            // 更新test_cycle
//            int runStatus = updateTestCycle(executeTestCaseDto, userId);
//
//            // 更新TestCaseExcution表，如果是首次执行并且是第一步则创建，否则更新
//            TestCaseExcution testCaseExcution = new TestCaseExcution();
//            if (cycleRunCount == 0 && executeTestCaseDto.getStep() == 1) {// 测试用例从未执行并且是执行第一步，直接从test_cycl_Join_Test_Step查询出来插进去
//                testCaseExcutionDao.createTestCaseExcutionDate();
//            } else {
//                testCaseExcution.setIssueId(issueId);
//                testCaseExcution.setStep((String.valueOf(executeTestCaseDto.getStep())));
//                testCaseExcution.setTestCaseId(executeTestCaseDto.getTestCaseId());
//                testCaseExcution.setTestCaseId(executeTestCaseDto.getTestCycleId());
//                testCaseExcution.setStepStatus(executeTestCaseDto.getStepStatus());
//                testCaseExcution.setUserId(userId);
//                testCaseExcution.setRunStatus(runStatus);// 与testCycle的runStatus一致，方便直接从testCaseExcution取出最终执行状态，返回history
//                testCaseExcutionDao.update(testCaseExcution);
//            }
//
//            List<TestCaseExcution> excutions = testCaseExcutionDao.queryHistoryByTestCaseId(testCaseExcution);
//            Map<String, Object> resultMap = new HashMap<String, Object>();
//            resultMap.put("history", excutions);// testCaseExcution
//            return new Resp.Builder<Map<String, Object>>().setData(resultMap).total(resultMap).ok();
//        } catch (BizException e) {
//            logger.error("class: TestCycleServiceImpl#runTestCycleTc,error []" + e.getMessage());
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            return new Resp.Builder<Map<String, Object>>().buildResult(e.getCode(), e.getMessage());
//        }
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Resp<Map<String, Object>> queryIssueByIdOrName(Issue issue) {
//        try {
////            issue.setUserId(jwtUserService.getMasterId());
//            List<Issue> list = testCaseExcutionDao.queryIssueList(issue);
//            Map<String, Object> resultMap = new HashMap<String, Object>();
//            resultMap.put("issueList", list);
//            return new Resp.Builder<Map<String, Object>>().setData(resultMap).total(resultMap).ok();
//        } catch (BizException e) {
//            logger.error("class: TestCycleServiceImpl#runTestCycleTc,error []" + e.getMessage());
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            return new Resp.Builder<Map<String, Object>>().buildResult(e.getCode(), e.getMessage());
//        }
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Resp<String> mergeIssue(Issue issue) {
//
//        try {
//            //验证是否存在
////            issue.setUserId(jwtUserService.getMasterId());
//            return Result.updateResult(testCaseExcutionDao.mergeIssue(issue));
//        } catch (BizException e) {
//            logger.error("class: IssueServiceImpl#update,error []" + e.getMessage());
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
//        }
//    }
//
//    public List<TestCycleJoinTestCase> queryTestCycleJoinTestCaseList(ExecuteTestCaseDto executeTestCaseDto) {
//        TestCycleJoinTestCase testCycleJoinTestCase = new TestCycleJoinTestCase();
//        testCycleJoinTestCase.setTestCaseId(executeTestCaseDto.getTestCaseId());
//        testCycleJoinTestCase.setTestCycleId(executeTestCaseDto.getTestCycleId());
//        List<TestCycleJoinTestCase> list = testCycleJoinTestCaseDao.queryAllDate(testCycleJoinTestCase);
//        return list;
//    }
//
//    public Integer updateTestCycle(ExecuteTestCaseDto executeTestCaseDto, String userId) {
//        Date date = new Date();
//        // 更新test_cycle
//        TestCycle testCycle = new TestCycle();
//        TestCycleJoinTestCase queryTestCycleJoinTestCase = new TestCycleJoinTestCase();
//        queryTestCycleJoinTestCase.setTestCycleId(executeTestCaseDto.getTestCycleId());
//        List<TestCycleJoinTestCase> testCycleJoinTestCaseList = testCycleJoinTestCaseDao.queryList(queryTestCycleJoinTestCase);// 查询test_cycle_join_test_case表该周期下所有用例的执行状态
//        int count = 0;
//        int count1 = 0;
//        int count2 = 0;
//        for (TestCycleJoinTestCase tcjt : testCycleJoinTestCaseList) {
//            if (tcjt.getRunStatus() == 0) {
//                count++;
//            } else if (tcjt.getRunStatus() == 1) {
//                count1++;
//            } else if (tcjt.getRunStatus() == 2) {
//                count2++;
//            } else if (tcjt.getRunStatus() == 3) {// 只要有Un_Complete的状态，test_cycle的完成结果肯定为Un_Complete
//                testCycle.setTestResult(3);
//            }
//        }
//        if (count2 != 0) {// 只要有失败的测试用例，则测试周期执行结果为失败
//            testCycle.setRunStatus(2);
//        } else if (count != 0 && count1 != 0 && count2 == 0) {// 有未执行和执行成功且没有执行失败的，执行结果为成功
//            testCycle.setRunStatus(1);
//        }
//
//        if (count != 0 && count != testCycleJoinTestCaseList.size()) {// 有未执行也有执行的，完成结果为未完成un_complete
//            testCycle.setTestResult(3);// un_complete
//        } else if (count == testCycleJoinTestCaseList.size()) {// 全部未执行，执行结果为not run，完成结果为not run
//            testCycle.setTestResult(0);
//            testCycle.setRunStatus(0);
//        } else if (count1 == testCycleJoinTestCaseList.size()) {// 全部成功，执行结果为成功，完成结果为成功
//            testCycle.setTestResult(1);
//            testCycle.setRunStatus(1);
//        } else if (count2 == testCycleJoinTestCaseList.size()) {// 全部失败，执行结果为失败，完成结果为失败
//            testCycle.setTestResult(2);
////                testCycle.setRunStatus(2);
//        } else if (count1 != 0 && count2 != 0 && (count1 + count2 == testCycleJoinTestCaseList.size())) {// 有成功也有失败，没有未执行，最终完成结果为失败
//            testCycle.setTestResult(2);
////                testCycle.setRunStatus(2);
//        }
//
//        testCycle.setId(executeTestCaseDto.getTestCycleId());
//        testCycle.setLastRunDate(date);
//        testCycle.setUserId(userId);
//        testCycle.setUpdateTime(date);
//        testCycleDao.update(testCycle);
//
//        return testCycle.getRunStatus();
//    }
//
//    public String getRandom(int num) {
//        Calendar cal = Calendar.getInstance();
//        String year = String.valueOf(cal.get(Calendar.YEAR));
//        String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
//        String day = String.valueOf(cal.get(Calendar.DATE));
//        String hour = String.valueOf(cal.get(Calendar.HOUR));
//        String min = String.valueOf(cal.get(Calendar.MINUTE));
//        String sec = String.valueOf(cal.get(Calendar.SECOND));
//        String random = String.valueOf((int) ((Math.random() * 9 + 1) * num));
//        return random;
//    }
//
//    public static void main(String[] args) {
////        System.out.println((int)((Math.random()*9+1)*100000));
////        System.out.println(Math.random()*9+1);
//        int a = 1;
//        Calendar cal = Calendar.getInstance();
//        String year = String.valueOf(cal.get(Calendar.YEAR));
//        String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
//        String day = String.valueOf(cal.get(Calendar.DATE));
//        String hour = String.valueOf(cal.get(Calendar.HOUR));
//        String min = String.valueOf(cal.get(Calendar.MINUTE));
//        String sec = String.valueOf(cal.get(Calendar.SECOND));
//        String random = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
//
////        issueId = year + month + day + hour + min + sec + random;//  生成issueId
//        System.out.println(year + month + day + hour + min + sec + random);
//        logger.error("class: IssueServiceImpl#update,error []");
//    }
//
//    /**
//     * 添加计划
//     *
//     * @param model
//     * @Param: [model]
//     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
//     * @Author: MaSiyi
//     * @Date: 2021/12/9
//     */
//    @Override
//    public Resp<String> addSchedule(TestCycleScheduleModel model) {
//        testCycleScheduleModelDao.insert(model);
//        //计算运行时间
//        Date startTimeDate = model.getStartTimeDate();
//        Date endTime = model.getEndTime();
//        //重复方式每日，每月，每年，不重复
//        String frequency = model.getFrequency();
//        if (StringUtils.isEmpty(frequency)) {
//            return new Resp.Builder<String>().buildResult("请选择重复方式");
//        }
//
//        if (!ObjectUtils.isEmpty(startTimeDate)) {
//
//            Date runTime = model.getRunTime();
//
//            if ("1".equals(frequency)) {
//                long betweenDay = DateUtil.between(startTimeDate, endTime, DateUnit.DAY);
//                int i;
//                if (startTimeDate.before(runTime)) {
//                    i = 0;
//                } else {
//                    i = 1;
//                }
//                for (; i <= betweenDay; i++) {
//                    TestCycleSchedule testCycleSchedule = new TestCycleSchedule();
//                    testCycleSchedule.setRunTime(new Date(runTime.getTime() + i * 24 * 60 * 60 * 1000L));
//                    testCycleSchedule.setRunStatus("0");
//                    testCycleSchedule.setTestCycleId(model.getTestCycleId());
//                    testCycleSchedule.setScheduleModelId(model.getId());
//                    testCycleScheduleDao.insert(testCycleSchedule);
//                }
//            } else if ("7".equals(frequency)) {
//                int i;
//                if (startTimeDate.before(runTime)) {
//                    i = 0;
//                } else {
//                    i = 1;
//                }
//                long betweenWeek = DateUtil.between(startTimeDate, endTime, DateUnit.WEEK);
//
//                for (; i <= betweenWeek; i++) {
//                    TestCycleSchedule testCycleSchedule = new TestCycleSchedule();
//                    testCycleSchedule.setRunTime(new Date(runTime.getTime() + i * 7 * 24 * 60 * 60 * 1000L));
//                    testCycleSchedule.setRunStatus("0");
//                    testCycleSchedule.setTestCycleId(model.getTestCycleId());
//                    testCycleSchedule.setScheduleModelId(model.getId());
//                    testCycleScheduleDao.insert(testCycleSchedule);
//                }
//            } else if ("30".equals(frequency)) {
//                int i;
//                if (startTimeDate.before(runTime)) {
//                    i = 0;
//                } else {
//                    i = 1;
//                }
//                long betweenMoon = (endTime.getTime() - startTimeDate.getTime()) / 30 / 24 / 60 / 60 / 1000;
//
//                for (; i <= betweenMoon; i++) {
//                    TestCycleSchedule testCycleSchedule = new TestCycleSchedule();
//                    testCycleSchedule.setRunTime(new Date(runTime.getTime() + i * 30 * 24 * 60 * 60 * 1000L));
//                    testCycleSchedule.setRunStatus("0");
//                    testCycleSchedule.setTestCycleId(model.getTestCycleId());
//                    testCycleSchedule.setScheduleModelId(model.getId());
//                    testCycleScheduleDao.insert(testCycleSchedule);
//                }
//            }
//
//
//        }
//
//        return new Resp.Builder<String>().ok();
//    }
//
    @Override
    public List<String> getTestCycleByProjectIdAndEvn(String projectId, String env, String testCycle) {
        return testCycleDao.getTestCycleByProjectIdAndEvn(projectId, env, testCycle);
    }

    @Override
    public List<TestCycle> list(TestCycleParam param) {
        return this.lambdaQuery()
                .eq(TestCycle::getProjectId, param.getProjectId())
                .like(StrUtil.isNotBlank(param.getTitle()), TestCycle::getTitle, param.getTitle())
                .orderByDesc(TestCycle::getCreateTime)
                .list();
    }

    @Override
    public TestCycle save(TestCycleSaveDto dto) {
        TestCycle testCycle = new TestCycle();
        BeanUtil.copyProperties(dto, testCycle);
        // 保存自定义字段
        if (!JSONUtil.isNull(dto.getCustomFieldDatas())) {
            testCycle.setTestcycleExpand(JSONUtil.toJsonStr(dto.getCustomFieldDatas()));
        }
        if(StringUtils.isNotBlank(testCycle.getTitle())){
            List<TestCycle> testCycles = listByTitle(testCycle.getTitle());
            if(Objects.nonNull(testCycles) && !testCycles.isEmpty()){
                return null;
            }
        }
        baseMapper.insert(testCycle);
        return testCycle;
    }

    @Override
    public TestCycle update(TestCycleSaveDto dto) {
        TestCycle testCycle = baseMapper.getByIdAndProjectId(dto.getId(), dto.getProjectId());
        if (testCycle == null) {
            throw new BaseException(StrUtil.format("测试周期查询不到。ID：{} projectId：{}", dto.getId(), dto.getProjectId()));
        }
        BeanUtil.copyProperties(dto, testCycle);
        // 修改自定义字段
        if (!JSONUtil.isNull(dto.getCustomFieldDatas())) {
            testCycle.setTestcycleExpand(JSONUtil.toJsonStr(dto.getCustomFieldDatas()));
        }
        if(StringUtils.isNotBlank(testCycle.getTitle())){
            List<TestCycle> testCycles = listByTitle(testCycle.getTitle());
            if(Objects.nonNull(testCycles) && !testCycles.isEmpty()){
                return null;
            }
        }
        baseMapper.updateById(testCycle);
        return testCycle;
    }

    @Override
    public TestCycle info(Long id) {
        TestCycle testCycle = baseMapper.selectById(id);
        if (testCycle == null) {
            throw new BizException(StrUtil.format("测试周期查询不到。ID：{}", id));
        }
        return testCycle;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clone(List<Long> ids) {
        List<TestCycle> testCycleList = new ArrayList<>();
        for (Long id : ids) {
            TestCycle testCycle = baseMapper.selectById(id);
            if (testCycle == null) {
                throw new BaseException(StrUtil.format("测试周期查询不到。ID：{}", id));
            }
            TestCycle testCaseClone = new TestCycle();
            BeanUtil.copyProperties(testCycle, testCaseClone);
            testCaseClone.setId(null);
            testCycleList.add(testCaseClone);
        }
        // 批量克隆
        this.saveBatch(testCycleList);
    }

    private List<TestCycle> listByTitle(String title){
       return  this.lambdaQuery().eq(TestCycle::getTitle, title).list();
    }
}
