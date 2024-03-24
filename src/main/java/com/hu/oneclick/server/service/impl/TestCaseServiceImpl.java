package com.hu.oneclick.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hu.oneclick.common.constant.ActionConstant;
import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.util.DateUtil;
import com.hu.oneclick.dao.*;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.*;
import com.hu.oneclick.model.domain.dto.*;
import com.hu.oneclick.model.domain.param.TestCaseParam;
import com.hu.oneclick.relation.service.RelationService;
import com.hu.oneclick.server.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qingyang
 */
@Service
@Slf4j
public class TestCaseServiceImpl extends ServiceImpl<TestCaseDao, TestCase> implements TestCaseService {

    @Resource
    private ModifyRecordsService modifyRecordsService;
    @Resource
    private JwtUserServiceImpl jwtUserService;
    @Resource
    private QueryFilterService queryFilterService;
    @Resource
    private FeatureDao featureDao;
    @Resource
    private SysCustomFieldService sysCustomFieldService;
    @Resource
    private TestCaseStepDao testCaseStepDao;
    @Resource
    private MailService mailService;
    @Resource
    private TestCycleJoinTestCaseDao testCycleJoinTestCaseDao;
    @Resource
    private CustomFieldsService customFieldsService;
    @Resource
    private  TestCaseDao testCaseDao;
    @Resource
    private TestCaseStepService testCaseStepService;

    @Resource
    TestCycleTcDao testCycleTcDao;

    @Resource
    private RelationService relationService;

    @Override
    public Resp<List<LeftJoinDto>> queryTitles(String projectId, String title) {
        List<TestCase> list = this.lambdaQuery()
                .eq(TestCase::getProjectId, projectId)
                .eq(TestCase::getCreateUserId, jwtUserService.getMasterId())
                .like(StrUtil.isNotBlank(title), TestCase::getTitle, title)
                .list();
        if (CollUtil.isEmpty(list)) {
            return new Resp.Builder<List<LeftJoinDto>>().ok();
        }
        List<LeftJoinDto> select = list.stream().map(i -> BeanUtil.copyProperties(i, LeftJoinDto.class)).collect(Collectors.toList());
        return new Resp.Builder<List<LeftJoinDto>>().setData(select).total(select).ok();
    }


    /** update testcase custom
     * @Param: [id]
     * @return: com.hu.oneclick.model.base.Resp<com.hu.oneclick.model.domain.TestCase>
     * @Author: MaSiyi
     * @Date: 2021/12/28
     */
    @Override
    public Resp<TestCase> queryById(Long id) {
        TestCase testCase = this.getById(id);
//        testCase.setCustomFieldDatas(customFieldDataService.testCaseRenderingCustom(id.toString()));
        return new Resp.Builder<TestCase>().setData(testCase).ok();
    }

    @Override
    public Resp<List<TestCase>> queryList(TestCaseDto testCase) {
        try {
            String masterId = jwtUserService.getMasterId();
            testCase.setCreateUserId(Long.valueOf(masterId));
            testCase.setFilter(queryFilterService.mysqlFilterProcess(testCase.getViewTreeDto(), masterId));
            List<TestCase> select = baseMapper.queryList(testCase);
            return new Resp.Builder<List<TestCase>>().setData(select).total(select).ok();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> insert(TestCase testCase) {
        try {
            //验证参数
            //验证是否存在
            //verifyIsExist(testCase.getTitle(), testCase.getProjectId(), null);
            //verifyIsExistExternaID(testCase.getExternaId(), testCase.getFeature(), null);
            //testCase.setUserId(jwtUserService.getMasterId());
            //testCase.setAuthorName(jwtUserService.getUserLoginInfo().getSysUser().getUserName());
            //判断创建时间是否传入，如未传入自动生成
            if (null == testCase.getCreateTime()) {
                Date date = new Date();
                testCase.setCreateTime(date);
                testCase.setUpdateTime(date);
            }
            return Result.addResult(baseMapper.insert(testCase));
        } catch (BizException e) {
            log.error("class: TestCaseServiceImpl#insert,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> update(TestCase testCase) {
        try {
            //验证是否存在
            //verifyIsExist(testCase.getTitle(), testCase.getProjectId(), testCase.getId());
            //verifyIsExistExternaID(testCase.getExternaId(), testCase.getFeature(), testCase.getId());
            //testCase.setUserId(jwtUserService.getMasterId());
            //新增修改字段记录
            modifyRecord(testCase);
            return Result.updateResult(baseMapper.updateByPrimaryKeySelective(testCase));
        } catch (BizException e) {
            log.error("class: TestCaseServiceImpl#update,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    /**
     * 修改字段，进行记录
     *
     * @param testCase
     */
    private void modifyRecord(TestCase testCase) {
        //try {
        //    TestCase query = baseMapper.queryById(testCase.getId(), testCase.getUserId());
        //    if (query == null) {
        //        throw new RuntimeException();
        //    }
        //
        //    Field[] fields = testCase.getClass().getDeclaredFields();
        //
        //    Field[] fields2 = query.getClass().getDeclaredFields();
        //    List<ModifyRecord> modifyRecords = new ArrayList<>();
        //    for (int i = 0, len = fields.length; i < len; i++) {
        //        String field = fields[i].getName(); //获取字段名
        //
        //        fields[i].setAccessible(true);
        //        fields2[i].setAccessible(true);
        //
        //        if (field.equals("id")
        //                || field.equals("projectId")
        //                || field.equals("userId")
        //                || field.equals("updateTime")
        //                || field.equals("createTime")
        //                || field.equals("scope")
        //                || field.equals("serialVersionUID")
        //                || field.equals("description")
        //                || fields[i].get(testCase) == null
        //                || fields[i].get(testCase) == "") {
        //            continue;
        //        }
        //
        //        String after = fields[i].get(testCase).toString(); //获取用户需要修改的字段
        //        String before = fields2[i].get(query) == null || fields2[i].get(query) == ""
        //                ? "" : fields2[i].get(query).toString();//获取数据库的原有的字段
        //
        //        //值不相同
        //        if (!before.equals(after)) {
        //
        //            ModifyRecord mr = new ModifyRecord();
        //            mr.setProjectId(query.getProjectId());
        //            mr.setUserId(query.getUserId());
        //            mr.setScope(OneConstant.SCOPE.ONE_TEST_CASE);
        //            mr.setModifyDate(new Date());
        //            mr.setModifyUser(jwtUserService.getUserLoginInfo().getSysUser().getUserName());
        //            mr.setBeforeVal(before);
        //            mr.setAfterVal(after);
        //            mr.setLinkId(query.getId());
        //            mr.setModifyField(getCnField(field));
        //            modifyRecords.add(mr);
        //        }
        //    }
        //    if (modifyRecords.size() <= 0) {
        //        return;
        //    }
        //    modifyRecordsService.insert(modifyRecords);
        //} catch (IllegalAccessException e) {
        //    throw new BizException(SysConstantEnum.ADD_FAILED.getCode(), "修改字段新增失败！");
        //}
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> delete(String id) {
        //try {
        //    TestCase testCase = new TestCase();
        //    testCase.setId(id);
        //    return Result.deleteResult(baseMapper.delete(testCase));
        //} catch (BizException e) {
        //    log.error("class: TestCaseServiceImpl#delete,error []" + e.getMessage());
        //    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        //    return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        //}
        return null;
    }

    @Override
    public Resp<Feature> queryTestNeedByFeatureId(String featureId) {
        String masterId = jwtUserService.getMasterId();
        Feature featureDto = featureDao.queryById(featureId, masterId);
        return new Resp.Builder<Feature>().setData(featureDto).ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<ImportTestCaseDto> importTestCase(MultipartFile multipartFile, String param) {
        //try {
        //    //1.取出文件并验证文件；
        //    //原始文件名称
        //    String originalFilename = multipartFile.getOriginalFilename();
        //    //解析到文件后缀，判断是否合法
        //    int lastIndexOf = originalFilename.lastIndexOf(".");
        //    String suffix = null;
        //    if (lastIndexOf == -1 || (suffix = originalFilename.substring(lastIndexOf + 1)).isEmpty()) {
        //        //文件后缀不能为空
        //        throw new BizException(SysConstantEnum.UPLOAD_FILE_FAILED.getCode(), "文件后缀不能为空！");
        //    }
        //    //支持.et, .xlsx, .xls, .csv格式
        //    Set<String> allowSuffix = new HashSet<>(Arrays.asList("et", "xlsx", "xls", "csv"));
        //    if (!allowSuffix.contains(suffix.toLowerCase())) {
        //        throw new BizException(SysConstantEnum.UPLOAD_FILE_FAILED.getCode(), "非法的文件，不允许的文件类型:" + suffix);
        //    }
        //    //解析excel文件
        //    JSONObject jsonObject = JSONObject.parseObject(param);
        //    //是否忽略第一行表头 1是 0否
        //    Integer ifIgnorFirstRow = jsonObject.getInteger("ifIgnorFirstRow");
        //    //构建导入测试模板获取列对应的cell下标
        //    JSONObject cellIndexObject = buildCellIndexByTemplateTestCase(jsonObject);
        //    List<String> allowPriority = Arrays.asList("高", "中", "低");
        //    List<String> allowBrowser = Arrays.asList("Google Chrome", "Fire Fox", "IE");
        //    List<String> allowPlatform = Arrays.asList("window", "mac");
        //    List<String> statusPlatform = Arrays.asList("Ready", "Draft");
        //    List<String> moudleMergeValues = sysCustomFieldService.getSysCustomField("moudle").getData().getMergeValues();
        //    List<String> versionsMergeValues = sysCustomFieldService.getSysCustomField("versions").getData().getMergeValues();
        //    List<String> testCategoryMergeValues = sysCustomFieldService.getSysCustomField("testCategory").getData().getMergeValues();
        //    List<String> testTypeMergeValues = sysCustomFieldService.getSysCustomField("testType").getData().getMergeValues();
        //    List<String> testEnvMergeValues = sysCustomFieldService.getSysCustomField("testEnv").getData().getMergeValues();
        //    List<String> testDeviceMergeValues = sysCustomFieldService.getSysCustomField("testDevice").getData().getMergeValues();
        //    List<String> testMethodMergeValues = sysCustomFieldService.getSysCustomField("testMethod").getData().getMergeValues();
        //    Date now = new Date();
        //    Map<SysConstantEnum, Map<String, String>> errorTipsMap = new HashMap<>();
        //    int successCount = 0;
        //    int errorCount = 0;
        //    int updateCount = 0;
        //    //判断文件后缀，根据不同后缀操作数据
        //    JSONArray rowValueArray = buildRowValueArray(suffix, multipartFile.getInputStream(),
        //            cellIndexObject, ifIgnorFirstRow);
        //    List<TestCase> testCases = new ArrayList<>();
        //    Map<String, List<TestCaseStep>> testCaseStepsMap = new HashMap<>();
        //    for (Object o : rowValueArray) {
        //        JSONObject rowValue = (JSONObject) o;
        //        TestCase testCase = new TestCase();
        //        //处理Feature 故事
        //        if (rowValue.containsKey("featureCol")) {
        //            JSONObject featureCol = rowValue.getJSONObject("featureCol");
        //            String title = featureCol.getString("value");
        //            if (StringUtils.isBlank(title)) {
        //                //记录错误，故事标题不能为空
        //                buildErrorTips(errorTipsMap, SysConstantEnum.IMPORT_TESTCASE_ERROR_REQUIRED
        //                        , featureCol, null);
        //            }
        //            Feature feature = queryFeatureByTitle(title);
        //            if (null == feature) {
        //                //记录错误，未查询到此项目的标题
        //                buildErrorTips(errorTipsMap, SysConstantEnum.IMPORT_TESTCASE_ERROR_NOFEATURE
        //                        , featureCol, null);
        //            } else {
        //                testCase.setProjectId(Long.valueOf(feature.getProjectId()));
        //                testCase.setFeature(feature.getId());
        //            }
        //        }
        //        //处理文本字段
        //        //title
        //        setValue(rowValue.getJSONObject("testTitleCol"), testCase
        //                , errorTipsMap, "title", true);
        //        //Pre-condition 测试条件
        //        setValue(rowValue.getJSONObject("preConditionCol"), testCase
        //                , errorTipsMap, "preCondition", false);
        //        //description 描述
        //        setValue(rowValue.getJSONObject("descriptionCol"), testCase
        //                , errorTipsMap, "description", false);
        //        //ExternalID
        //        setValue(rowValue.getJSONObject("externalIdCol"), testCase
        //                , errorTipsMap, "externaId", false);
        //        //Comments 备注
        //        setValue(rowValue.getJSONObject("commentsCol"), testCase
        //                , errorTipsMap, "comments", false);
        //        //处理固定字典类型
        //        // Priority 优先级
        //        setSelectValue(rowValue.getJSONObject("priorityCol"), allowPriority, testCase
        //                , errorTipsMap, "priority", true);
        //        //Browser 浏览器
        //        setSelectValue(rowValue.getJSONObject("browserCol"), allowBrowser, testCase
        //                , errorTipsMap, "browser", false);
        //        //Platform 平台
        //        setSelectValue(rowValue.getJSONObject("platformCol"), allowPlatform, testCase
        //                , errorTipsMap, "platform", false);
        //        //status 状态
        //        setSelectValue(rowValue.getJSONObject("statusCol"), statusPlatform, testCase
        //                , errorTipsMap, "status", false);
        //        //处理动态字典类型
        //        //Module 模块
        //        setSelectValue(rowValue.getJSONObject("moduleCol"), moudleMergeValues, testCase
        //                , errorTipsMap, "module", true);
        //        //DeviceType 测试设备
        //        setSelectValue(rowValue.getJSONObject("deviceTypeCol"), testDeviceMergeValues, testCase
        //                , errorTipsMap, "testDevice", false);
        //        //Env 测试环境
        //        setSelectValue(rowValue.getJSONObject("envCol"), testEnvMergeValues, testCase
        //                , errorTipsMap, "env", true);
        //        //Version
        //        setSelectValue(rowValue.getJSONObject("versionCol"), versionsMergeValues, testCase
        //                , errorTipsMap, "version", true);
        //        //CaseCategory  测试分类
        //        setSelectValue(rowValue.getJSONObject("caseCategoryCol"), testCategoryMergeValues, testCase
        //                , errorTipsMap, "caseCategory", true);
        //        //CaseType 测试类型
        //        setSelectValue(rowValue.getJSONObject("caseTypeCol"), testTypeMergeValues, testCase
        //                , errorTipsMap, "testType", true);
        //        //Automation 测试方法
        //        setSelectValue(rowValue.getJSONObject("automationCol"), testMethodMergeValues, testCase
        //                , errorTipsMap, "testMethod", true);
        //        ///判断是否新增或者更新，根据故事ID+ExternalID查询测试用例，如果存在则进行更新；
        //
        //        //判断ExternalI是否存在，进行判断下一步是否更新
        //        if (jsonObject.containsKey("ifUpdateCase")) {
        //            JSONObject externalIdCol = rowValue.getJSONObject("externalIdCol");
        //            String externalId = externalIdCol.getString("value");
        //            TestCase queryFeaturExternalIDTestCase = new TestCase();
        //            queryFeaturExternalIDTestCase.setFeature(testCase.getFeature());
        //            queryFeaturExternalIDTestCase.setExternalLinkId(externalId);
        //            queryFeaturExternalIDTestCase.setId(null);
        //            //TestCase featurExternalIDTestCase = this.baseMapper.selectOne(queryFeaturExternalIDTestCase);
        //            //if (null != featurExternalIDTestCase) {
        //            //    Boolean ifUpdateCase = jsonObject.getBooleanValue("ifUpdateCase");
        //            //    if (ifUpdateCase) {           //进行更新
        //            //        //将已存在ID打上标识，后续判断新增或插入
        //            //        testCase.setId("UPDATE" + featurExternalIDTestCase.getId());
        //            //    } else {  //如果存在，并且更新标识为否，提示用户此故事下，已经存在此ExternalID，无法进行插入
        //            //        buildErrorTips(errorTipsMap, SysConstantEnum.IMPORT_TESTCASE_ERROR_EXIST_FEATURE_EXTERNALID
        //            //                , externalIdCol, null);
        //            //    }
        //            //}
        //        }
        //
        //        //处理 Step
        //        List<TestCaseStep> testCaseSteps = new ArrayList<>();
        //        if (cellIndexObject.containsKey("stepCol")) {
        //            String setpValue = getCellValue(errorTipsMap,
        //                    rowValue.getJSONObject("stepCol"), true);
        //
        //            //测试数据
        //            String cellTestDataValue = getCellValue(errorTipsMap,
        //                    rowValue.getJSONObject("stepTestDataCol"), true);
        //
        //            //Expected Result 预期结果
        //            String cellExpectedResultValue = getCellValue(errorTipsMap,
        //                    rowValue.getJSONObject("stepExpectResultCol"), true);
        //            Boolean ifSplitTestStep = jsonObject.getBoolean("ifSplitTestStep");
        //            //是否分隔
        //            if (ifSplitTestStep) {
        //                String splitTestStep = jsonObject.getString("splitTestStep");
        //                String[] steps = setpValue.split(splitTestStep);
        //                String[] testDatas = cellTestDataValue.split(splitTestStep);
        //                String[] expectedResults = cellExpectedResultValue.split(splitTestStep);
        //
        //                for (int i1 = 0; i1 < steps.length; i1++) {
        //                    int stepsLength = steps.length;
        //                    TestCaseStep testCaseStep = new TestCaseStep();
        //                    testCaseStep.setCreateTime(now);
        //                    testCaseStep.setStatus(0);
        //                    testCaseStep.setStep(steps[i1]);
        //                    //如果测试数据的长度与测试名称一致 则分开存储
        //                    if (stepsLength == testDatas.length) {
        //                        testCaseStep.setTestData(testDatas[i1]);
        //                    } else {          //如果不相等则每个步骤都插入一致的测试数据
        //                        testCaseStep.setTestData(cellTestDataValue);
        //                    }
        //                    //预期结果的长度与测试名称一致 则分开存储
        //                    if (stepsLength == expectedResults.length) {
        //                        testCaseStep.setExpectedResult(expectedResults[i1]);
        //                    } else {          //如果不相等则每个步骤都插入一致的测试数据
        //                        testCaseStep.setExpectedResult(cellExpectedResultValue);
        //                    }
        //                    testCaseSteps.add(testCaseStep);
        //                }
        //            } else {
        //                TestCaseStep testCaseStep = new TestCaseStep();
        //                testCaseStep.setTestData(setpValue);
        //                testCaseStep.setTestData(cellTestDataValue);
        //                testCaseStep.setExpectedResult(cellExpectedResultValue);
        //                testCaseStep.setStatus(0);
        //                testCaseStep.setCreateTime(now);
        //                testCaseSteps.add(testCaseStep);
        //            }
        //        } else {
        //            buildErrorTips(errorTipsMap, SysConstantEnum.IMPORT_TESTCASE_ERROR_REQUIRED
        //                    , rowValue.getJSONObject("stepCol"), null);
        //        }
        //        testCases.add(testCase);
        //        //testCaseStepsMap.put(testCase.getId().replace("UPDATE", ""), testCaseSteps);
        //    }
        //
        //    //判断是否异常,如出现异常，则全部进行操作db
        //    if (errorTipsMap.isEmpty()) {
        //        //判断是否新增或者更新，根据故事ID+ExternalID查询测试用例，如果存在则进行更新；
        //        for (TestCase testCase : testCases) {
        //            Resp<String> insertOrUpdate = null;
        //            //如ID为空则进行新增
        //            //if (!testCase.getId().startsWith("UPDATE")) {
        //            //    testCase.setCreateTime(now);
        //            //    testCase.setUpdateTime(now);
        //            //    insertOrUpdate = this.insert(testCase);
        //            //    successCount++;
        //            //} else {          //如ID不为空更新
        //            //    testCase.setId(testCase.getId().replace("UPDATE", ""));
        //            //    insertOrUpdate = this.update(testCase);
        //            //    //删除测试用例步骤重新插入
        //            //    TestCaseStep delTestCase = new TestCaseStep();
        //            //    delTestCase.setTestCaseId(testCase.getId());
        //            //    delTestCase.setId(null);
        //            //    this.testCaseStepDao.delete(delTestCase);
        //            //    updateCount++;
        //            //}
        //            List<TestCaseStep> testCaseSteps = testCaseStepsMap.get(testCase.getId());
        //            if (insertOrUpdate.getCode().equals("200")) {
        //                for (TestCaseStep testCaseStep : testCaseSteps) {
        //                    //testCaseStep.setTestCaseId(testCase.getId());
        //                    testCaseStepDao.insert(testCaseStep);
        //                }
        //            } else {
        //                throw new BizException(insertOrUpdate.getCode(), insertOrUpdate.getMsg());
        //            }
        //        }
        //        //判断是否创建视图
        //        Boolean ifCreateView = jsonObject.getBooleanValue("ifCreateView");
        //        if (ifCreateView) {
        //            createViewImportTestCase(now);
        //        }
        //    } else {
        //        errorCount = rowValueArray.size();
        //    }
        //    //判断是否发送email
        //    Boolean ifSendEmail = jsonObject.getBooleanValue("ifSendEmail");
        //    if (ifSendEmail) {
        //        sendEmailImportTestCase(successCount, updateCount, errorCount);
        //    }
        //    return new Resp.Builder<ImportTestCaseDto>().setData(buildImportTestCaseDto(errorTipsMap, successCount, updateCount, errorCount)).ok();
        //} catch (Exception e) {
        //    log.error("class: TestCaseServiceImpl#importTestCase,error []" + e.getMessage());
        //    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        //    return new Resp.Builder<ImportTestCaseDto>().buildResult(SysConstantEnum.SYSTEM_BUSY.getCode(), e.getMessage());
        //}
        return new Resp.Builder<ImportTestCaseDto>().buildResult(SysConstantEnum.SYSTEM_BUSY.getCode(), "导入测试用例失败");
    }

    /**
     * 导入测试用例发送emial
     *
     * @param successCount
     * @param updateCount
     * @param errorCount
     */
    private void sendEmailImportTestCase(int successCount, int updateCount, int errorCount) {
        //获取当前登录人的邮箱
        String email = jwtUserService.getUserLoginInfo().getSysUser().getEmail();
        //判断是否子用户，根据分割标识
        if (email.indexOf(OneConstant.COMMON.SUB_USER_SEPARATOR) > 0) {
            email = email.split(OneConstant.COMMON.SUB_USER_SEPARATOR)[1];
        }
        MailDto mailDto = new MailDto();
        mailDto.setToEmail(email);
        mailDto.setTitle(OneConstant.EMAIL.TITLE_IMPORTTESTCASE);
        mailDto.setTemplateHtmlName(OneConstant.EMAIL.TEMPLATEHTMLNAME_IMPORTTESTCASE);
        Map<String, Object> attachmentMap = new HashMap<>();
        attachmentMap.put("importDateTime", DateUtil.format(new Date(), "yyyy年MM月dd日 HH:mm:ss"));
        attachmentMap.put("successCount", successCount);
        attachmentMap.put("errorCount", errorCount);
        attachmentMap.put("updateCount", updateCount);
        mailDto.setAttachment(attachmentMap);
        mailService.sendTemplateMail(mailDto);
    }

    /**
     * 导入测试创建视图
     *
     * @param now 导入时间
     */
    private void createViewImportTestCase(Date now) {
        String nowString = DateUtil.format(now, "yyyy-MM-dd HH:mm:ss");
        JSONObject filterJson = new JSONObject();
        filterJson.put("andOr", "and");
        filterJson.put("beginDate", nowString);
        filterJson.put("endDate", nowString);
        filterJson.put("fieldName", "createTime");
        filterJson.put("intVal", "");
        filterJson.put("sourceVal", "");
        filterJson.put("textVal", "");
        filterJson.put("type", "fDateTime");
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(filterJson);
        View view = new View();
        view.setFilter(jsonArray.toJSONString());
        view.setScopeName("TestCase");
        view.setIsPrivate(0);
        view.setTitle("创建时间：" + nowString + "-" + nowString);
//        viewService.addView(view);
    }

    /**
     * 根据文件封装col数据
     *
     * @param suffix          文件后缀
     * @param inputStream     文件流
     * @param cellIndexObject 列的下标JSON
     * @param ifIgnorFirstRow 是否忽略第一行
     * @return
     */
    public JSONArray buildRowValueArray(String suffix, InputStream inputStream,
                                        JSONObject cellIndexObject,
                                        Integer ifIgnorFirstRow) throws IOException {
        JSONArray rowValueArray = new JSONArray();
        if (suffix.equals("csv")) {
            //编码格式要是用GBK
            InputStreamReader is = new InputStreamReader(inputStream, "GBK");
            BufferedReader reader = new BufferedReader(is);
            CSVParser parser = CSVFormat.DEFAULT.parse(reader);
            int rownum = 0;

            // 读取文件每行内容
            for (CSVRecord record : parser.getRecords()) {
                //  跳过表头
                if (1 == ifIgnorFirstRow && rownum == 0) {
                    rownum++;
                    continue;
                }
                JSONObject rowValue = new JSONObject();
                //封装数据
                for (String colKey : cellIndexObject.keySet()) {
                    JSONObject colValue = new JSONObject();
                    Integer colIndex = cellIndexObject.getInteger(colKey);
                    String value = record.get(colIndex);
                    colValue.put("value", value);
                    colValue.put("rownum", rownum);
                    colValue.put("colIndex", colIndex);
                    colValue.put("colLetter", getColLetterByIndex(colIndex));
                    rowValue.put(colKey, colValue);
                    //colValueArray.add(colKey);
                }
                rowValueArray.add(rowValue);
                rownum++;
            }
        } else {
            Workbook workbook = null;
            if (suffix.equals("xlsx")) {
                workbook = new XSSFWorkbook(inputStream);
            } else {
                workbook = new HSSFWorkbook(inputStream);
            }
            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getPhysicalNumberOfRows();
            for (int rownum = 0; rownum < lastRowNum; rownum++) {
                if (1 == ifIgnorFirstRow && rownum == 0) {
                    continue;
                }
                Row row = sheet.getRow(rownum);
                JSONObject rowValue = new JSONObject();
                //封装数据
                for (String colKey : cellIndexObject.keySet()) {
                    JSONObject colValue = new JSONObject();
                    Integer colIndex = cellIndexObject.getInteger(colKey);
                    Cell cell = row.getCell(colIndex);
                    cell.setCellType(CellType.STRING);
                    String stringCellValue = cell.getStringCellValue();
                    colValue.put("value", stringCellValue);
                    colValue.put("rownum", rownum);
                    colValue.put("colIndex", colIndex);
                    colValue.put("colLetter", getColLetterByIndex(colIndex));
                    rowValue.put(colKey, colValue);
                }
                rowValueArray.add(rowValue);
            }
        }
        return rowValueArray;
    }

    /**
     * 构建导入返回参数
     *
     * @param errorTipsMap
     * @param successCount
     * @param updateCount
     * @return
     */
    private ImportTestCaseDto buildImportTestCaseDto(Map<SysConstantEnum, Map<String, String>> errorTipsMap,
                                                     int successCount, int updateCount, int errorCount) {
        ImportTestCaseDto importTestCaseDto = new ImportTestCaseDto();
        importTestCaseDto.setSuccess(new ArrayList());
        importTestCaseDto.getSuccess().add("导入成功" + successCount + "条测试用例");
        if (updateCount > 0) {
            importTestCaseDto.getSuccess().add("更新成功" + updateCount + "条测试用例");
        }
        if (errorCount > 0) {
            importTestCaseDto.getSuccess().add("导入异常" + errorCount + "条测试用例");
        }
        importTestCaseDto.setError(new ArrayList());
        importTestCaseDto.setWarning(new ArrayList());
        for (SysConstantEnum sysConstantEnum : errorTipsMap.keySet()) {
            Map<String, List<String>> errorMap = new HashMap<>();
            List<String> strings = new ArrayList<>();
            Map<String, String> stringStringMap = errorTipsMap.get(sysConstantEnum);
            for (String s : stringStringMap.keySet()) {
                if (sysConstantEnum.equals(SysConstantEnum.IMPORT_TESTCASE_ERROR_NOTSELECT)) {//如果是下拉菜单，取出key进行进行分割
                    String[] split = s.split("-");
                    strings.add("列" + split[0] + "的值应该【" + (split.length >= 2 ? split[1] : "") + "】,错误行:" + stringStringMap.get(s));
                } else {
                    strings.add("列" + s + ",错误行:" + stringStringMap.get(s));
                }
            }
            errorMap.put(sysConstantEnum.getValue(), strings);
            importTestCaseDto.getError().add(errorMap);
        }
        return importTestCaseDto;
    }

    /**
     * 获取cell的值，如果为required为true，则将错误信息插入
     *
     * @param errorTipsMap 错误提示的map
     * @param colValue     导入列的值
     * @param required     是否必填
     * @return
     */
    private String getCellValue(Map<SysConstantEnum, Map<String, String>> errorTipsMap
            , JSONObject colValue,
                                boolean required) {
        String value = "";
        if (null != colValue && StringUtils.isNotBlank(value = colValue.getString("value"))) {
            return value;
        } else {
            if (required) {
                buildErrorTips(errorTipsMap, SysConstantEnum.IMPORT_TESTCASE_ERROR_REQUIRED
                        , colValue, null);
            }
        }
        return value;
    }

    /**
     * 设置字符串的值
     *
     * @param colValue
     * @param testCase
     * @param errorTipsMap
     * @param field
     * @param required
     */
    private void setValue(JSONObject colValue, TestCase testCase,
                          Map<SysConstantEnum, Map<String, String>> errorTipsMap
            , String field, boolean required) {
        try {
            String value = null;
            if (null != colValue && StringUtils.isNotBlank(value = colValue.getString("value"))) {
                if (StringUtils.isNotBlank(value)) {
                    Class<?> fieldType = testCase.getClass().getDeclaredField(field).getType();
                    testCase.getClass().getMethod("set" + field.substring(0, 1).toUpperCase() + field.substring(1),
                            fieldType).invoke(testCase, value);
                }
            } else {
                if (required) {
                    buildErrorTips(errorTipsMap, SysConstantEnum.IMPORT_TESTCASE_ERROR_REQUIRED
                            , colValue, null);
                }
            }
        } catch (Exception e) {
            throw new BizException(e.getMessage());
        }
    }

    /**
     * 设置下拉的字段
     *
     * @param colValue     列的值
     * @param allow        筛选的字典
     * @param testCase     要设置字段的对象
     * @param errorTipsMap 错误提示的map
     * @param field        要设置的字段
     * @throws NoSuchFieldException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void setSelectValue(JSONObject colValue,
                                List<String> allow, TestCase testCase,
                                Map<SysConstantEnum, Map<String, String>> errorTipsMap
            , String field, boolean required) {
        try {
            String value = null;
            if (null != colValue && StringUtils.isNotBlank(value = colValue.getString("value"))) {
                if (StringUtils.isNotBlank(value)) {
                    if (allow.contains(value)) {
                        Class<?> fieldType = testCase.getClass().getDeclaredField(field).getType();
                        testCase.getClass().getMethod("set" + field.substring(0, 1).toUpperCase() + field.substring(1),
                                fieldType).invoke(testCase, value);
                    } else {
                        buildErrorTips(errorTipsMap, SysConstantEnum.IMPORT_TESTCASE_ERROR_NOTSELECT
                                , colValue, String.join(",", allow));
                    }
                }
            } else {
                if (required) {
                    buildErrorTips(errorTipsMap, SysConstantEnum.IMPORT_TESTCASE_ERROR_REQUIRED
                            , colValue, null);
                }
            }
        } catch (Exception e) {
            throw new BizException(e.getMessage());
        }
    }


    /**
     * 构建返回提示参数
     *
     * @param tipsMap         原map
     * @param sysConstantEnum 错误类型
     * @param colValue        列
     * @param selectVal       下拉内容，错误类型为下拉必填
     */
    private void buildErrorTips(Map<SysConstantEnum, Map<String, String>> tipsMap,
                                SysConstantEnum sysConstantEnum,
                                JSONObject colValue, String selectVal) {
        Map<String, String> errorMaps = tipsMap.get(sysConstantEnum);
        //判断此错误类型是否已经添加
        if (null == errorMaps) {
            tipsMap.put(sysConstantEnum, new HashMap<>());
            errorMaps = tipsMap.get(sysConstantEnum);
        }
        String col = colValue.getString("colLetter");
        Integer rownum = colValue.getInteger("rownum") + 1;
        //如存在下拉项，则与列拼接成为key，方便后面截取拼接返回消息
        if (StringUtils.isNoneBlank(selectVal)) {
            col += "-" + selectVal;
        }
        //存储错误列的行数
        String colMap = errorMaps.get(col);
        if (StringUtils.isBlank(colMap)) {
            errorMaps.put(col, rownum + "");
        } else {
            errorMaps.put(col, errorMaps.get(col) + "," + rownum);
        }
    }

    /**
     * 构建导入测试模板获取列对应的cell下标
     *
     * @param templateTestCase
     * @return
     */
    private JSONObject buildCellIndexByTemplateTestCase(JSONObject templateTestCase) {
        //返回数据：key:字段，value: 列号
        JSONObject res = new JSONObject();
        //Set<String> letter=new HashSet<>(Arrays.asList("A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"));
        String letter = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        //1.取出导入模板json中的属性，以_col结尾的说明为导入字段
        for (String s : templateTestCase.keySet()) {
            if (s.endsWith("Col")) {
                Object o = null;
                if ((o = templateTestCase.get(s)) != null) {
                    res.put(s, letter.indexOf(o.toString()));
                }
            }
        }
        return res;
    }

    /**
     * 根据下标获取列的字母字母
     *
     * @param colIndex
     * @return
     */
    private String getColLetterByIndex(int colIndex) {
        String letter = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        return letter.substring(colIndex, colIndex + 1);
    }

    /**
     * 查询关联故事
     *
     * @param title
     * @return
     */
    private Feature queryFeatureByTitle(String title) {
        String projectId = jwtUserService.getUserLoginInfo().getSysUser().getUserUseOpenProject().getProjectId();
        Feature feature = new Feature();
        feature.setTitle(title);
        feature.setProjectId(Long.valueOf(projectId));
        feature.setId(null);
        return featureDao.selectOne(new LambdaQueryWrapper<Feature>().eq(Feature::getTitle, feature.getTitle()).eq(Feature::getProjectId, feature.getProjectId()));
    }


    /**
     * 查重
     */
    private void verifyIsExist(String title, String projectId, String testCaseId) {
        //if (StringUtils.isEmpty(title)) {
        //    return;
        //}
        //TestCase testCase = new TestCase();
        //testCase.setTitle(title);
        //testCase.setProjectId(projectId);
        //testCase.setId(null);
        //TestCase testCaseOne = baseMapper.selectOne(testCase);
        ////如果testCaseId不为空则判断查询出ID是否与传入ID一致说明不重复
        //if (testCaseId != null && testCaseOne != null && !testCaseOne.getId().equals(testCaseId)) {
        //    throw new BizException(SysConstantEnum.DATE_EXIST.getCode(), testCase.getTitle() + SysConstantEnum.DATE_EXIST.getValue());
        //} else if (testCaseId == null && testCaseOne != null) {
        //    throw new BizException(SysConstantEnum.DATE_EXIST.getCode(), testCase.getTitle() + SysConstantEnum.DATE_EXIST.getValue());
        //}
    }

    /**
     * 判断externaID是否在故事下存在
     *
     * @param externaID
     * @param feature
     */
    private void verifyIsExistExternaID(String externaID, String feature, String testCaseId) {
        if (StringUtils.isEmpty(externaID)) {
            return;
        }
        TestCase testCase = new TestCase();
        testCase.setExternalLinkId(externaID);
        testCase.setFeature(feature);
        testCase.setId(null);
        //TestCase testCaseOne = baseMapper.selectOne(testCase);
        ////如果testCaseId不为空则判断查询出ID是否与传入ID一致说明不重复
        //if (testCaseId != null && testCaseOne != null && !testCaseOne.getId().equals(testCaseId)) {
        //    throw new BizException(SysConstantEnum.DATE_EXIST.getCode(), "externaID:" + externaID + SysConstantEnum.DATE_EXIST.getValue());
        //} else if (testCaseId == null && testCaseOne != null) {
        //    throw new BizException(SysConstantEnum.DATE_EXIST.getCode(), "externaID:" + externaID + SysConstantEnum.DATE_EXIST.getValue());
        //}
    }

    /**
     * 获取字段对应中文字义
     *
     * @param args
     * @return
     */
    private String getCnField(String args) {
        switch (args) {
            case "title":
                return "名称";
            case "priority":
                return "优先权";
            case "故事":
                return "feature";
            case "status":
                return "状态";
            case "description":
                return "描述";
            case "executedDate":
                return "执行时间";
            case "authorName":
                return "管理人";
            case "browser":
                return "浏览器";
            case "platform":
                return "平台";
            case "version":
                return "版本";
            case "caseCategory":
                return "用例类别";
            case "caseType":
                return "用例类型";
            case "externaId":
                return "外部ID";
            case "env":
                return "环境";
            case "preCondition":
                return "前提条件";
        }
        return args;
    }


    private List<String> getCommonList(SysCustomFieldExpand versions) {
        versions = Optional.ofNullable(versions).orElse(new SysCustomFieldExpand());
        List<String> list;
        if (StringUtils.isEmpty(versions.getLinkSysCustomField())) {
            list = new ArrayList<>();
        } else {
            list = Arrays.asList(versions.getLinkSysCustomField().split(","));
        }
        return list;
    }

    /**
     * 添加测试用例
     *
     * @param testCycleDto
     * @Param: [testCase]
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2021/12/1
     */
    @Override
    public Resp<String> addTestCase(TestCycleDto testCycleDto) {
//        String testCycleId = testCycleDto.getId();

        //查询testcycle是否存在
        //Resp<TestCycle> testCycleResp = testCycleService.queryById(testCycleId);
        //TestCycle data = testCycleResp.getData();
        ////如果为空，则说明先选择的testcase一起添加
        //if (ObjectUtils.isEmpty(data)) {
        //    data = new TestCycle();
        //    data.setTitle("新建测试周期" + DateUtil.getCurrDate());
        //    testCycleService.insert(data);
        //}
        ////已存在的testCycle添加testcase
        //List<TestCase> testCases = testCycleDto.getTestCases();
        //for (TestCase aCase : testCases) {
        //    TestCycleJoinTestCase tc = new TestCycleJoinTestCase();
        //    tc.setTestCycleId(testCycleId);
        //    tc.setTestCaseId(aCase.getId());
        //    testCycleJoinbaseMapper.insert(tc);
        //}

        //插入自定义字段值
//        List<CustomFieldData> customFieldDatas = testCycleDto.getCustomFieldDatas();
        //customFieldDataService.insertTestCaseCustomData(customFieldDatas, testCases);
        return new Resp.Builder<String>().ok();
    }

    /**
     * 更新action
     *
     * @param testCaseId
     * @param actionType
     * @param testCycleId
     * @Param: [testCaseId]
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2021/12/1
     */
    @Override
    public Resp<List<TestCase>> updateAction(List<String> testCaseId, String actionType, String testCycleId) {
        String masterId = jwtUserService.getMasterId();
        ArrayList<TestCase> testCases = new ArrayList<>();
        if (actionType.equals(ActionConstant.RELOAD)) {
            for (String id : testCaseId) {
                TestCase testCase = baseMapper.queryById(id, masterId);
                testCases.add(testCase);
            }
            //如果是刷新就返回数据给前端
            return new Resp.Builder<List<TestCase>>().setData(testCases).ok();
        } else if (actionType.equals(ActionConstant.REMOVE)) {
            for (String id : testCaseId) {
                TestCycleJoinTestCase tc = new TestCycleJoinTestCase();
//                tc.setTestCycleId(testCycleId);
//                tc.setTestCaseId(id);
                //如果是remove就删除
                //testCycleJoinbaseMapper.delete(tc);
            }
        }
        return new Resp.Builder<List<TestCase>>().fail();
    }

    @Override
    public List<TestCase> list(TestCaseParam param) {
        return this.lambdaQuery()
                .like(StrUtil.isNotBlank(param.getTitle()), TestCase::getTitle, param.getTitle())
                .eq(TestCase::getProjectId, param.getProjectId())
                .orderByDesc(TestCase::getCreateTime)
                .list();
    }

    @Override
    public List<TestCase> listExtend(TestCaseParam param) {
        return this.lambdaQuery()
                .in(CollUtil.isNotEmpty(param.getTestCaseIdList()), TestCase::getId, param.getTestCaseIdList())
                .orderByDesc(TestCase::getCreateTime)
                .list();
    }

    @Override
    public TestCase save(TestCaseSaveDto dto) {
        TestCase testCase = new TestCase();
        BeanUtil.copyProperties(dto, testCase);
        // 保存自定义字段
        if (!JSONUtil.isNull(dto.getCustomFieldDatas())) {
            testCase.setTestcaseExpand(JSONUtil.toJsonStr(dto.getCustomFieldDatas()));
        }
        baseMapper.insert(testCase);
        return testCase;
    }

    @Override
    public TestCase update(TestCaseSaveDto dto) {
        TestCase testCase = baseMapper.getByIdAndProjectId(dto.getId(), dto.getProjectId());
        if (testCase == null) {
            throw new BaseException(StrUtil.format("测试用例查询不到。ID：{} projectId：{}", dto.getId(), dto.getProjectId()));
        }
        BeanUtil.copyProperties(dto, testCase);
        // 修改自定义字段
        if (!JSONUtil.isNull(dto.getCustomFieldDatas())) {
            testCase.setTestcaseExpand(JSONUtil.toJsonStr(dto.getCustomFieldDatas()));
        }
        baseMapper.updateById(testCase);
        return testCase;
    }

    @Override
    public TestCase info(Long id) {
        TestCase testCase = baseMapper.selectById(id);
        if (testCase == null) {
            throw new BizException(StrUtil.format("测试用例查询不到。ID：{}", id));
        }
        //  查询测试用例关联步骤
        List<TestCaseStep> testCaseStepList = testCaseStepService.lambdaQuery().eq(TestCaseStep::getTestCaseId, testCase.getId()).list();
        testCase.setTestCaseStepList(testCaseStepList);
        return testCase;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clone(List<Long> ids) {
        List<TestCase> testCaseList = new ArrayList<>();
        for (Long id : ids) {
            TestCase testCase = baseMapper.selectById(id);
            if (testCase == null) {
                throw new BaseException(StrUtil.format("测试用例查询不到。ID：{}", id));
            }
            TestCase testCaseClone = new TestCase();
            BeanUtil.copyProperties(testCase, testCaseClone);
            testCaseClone.setId(null);
            testCaseList.add(testCaseClone);
        }
        // 批量克隆
        this.saveBatch(testCaseList);
    }

    @Resource
    TestCaseDao testCase;

    @Override
    public List<TestCaseBisDto> getTestCaseAllByCycleId(Long testCycleId) {
        List<TestCaseDataDto> list = testCase.getSelectAll(testCycleId);
        List<TestCaseBisDto> arrList = new ArrayList<>();
        for (TestCaseDataDto testCaseDataDto : list) {
            TestCaseBisDto testCaseBisDto = new TestCaseBisDto();
            testCaseBisDto.setTestCaseRun(testCaseDataDto.getRunCount(), testCaseDataDto.getRunStatus(), testCaseDataDto.getUpdateTime(), testCaseDataDto.getCreateUserId(), testCaseDataDto.getUpdateUserId(), testCaseDataDto.getCaseRunDuration(), testCaseDataDto.getCaseTotalPeriod());
            testCaseBisDto.setTestCase(testCaseDataDto.getId(), testCaseDataDto.getProjectId(), testCaseDataDto.getTitle(), testCaseDataDto.getPriority(), testCaseDataDto.getFeature(), testCaseDataDto.getDescription(), testCaseDataDto.getExecuteTime(), testCaseDataDto.getBrowser(), testCaseDataDto.getPlatform(), testCaseDataDto.getVersion(), testCaseDataDto.getCaseCategory(), testCaseDataDto.getTestType(), testCaseDataDto.getTestCondition(), testCaseDataDto.getEnv(), testCaseDataDto.getExternalLinkId(), testCaseDataDto.getLastRunStatus(), testCaseDataDto.getModule(), testCaseDataDto.getTestDevice(), testCaseDataDto.getTestData(), testCaseDataDto.getTestMethod(), testCaseDataDto.getTestStatus(), testCaseDataDto.getReportTo(), testCaseDataDto.getTestcaseExpand(), testCaseDataDto.getRemarks());
            arrList.add(testCaseBisDto);
        }
        return arrList;
    }

    /**
     * 根据CaseId、projectId查找
     *
     * @param projectId
     * @param testCaseId
     * @return
     */
    @Override
    public TestCase getByIdAndProjectId(Long projectId, Long testCaseId) {
        return baseMapper.getByIdAndProjectId(testCaseId, projectId);
    }


    @Override
    public List<TestCase> testCaseSearch(Long projectId, String title) {
        List<TestCase> list = this.lambdaQuery()
                .eq(TestCase::getProjectId, projectId)
                .like(StrUtil.isNotBlank(title), TestCase::getTitle, title)
                .select(TestCase::getId,TestCase::getTitle)
                .list();
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list;
    }

    @Override
    @Transactional
    public Resp<Map> removeAndChild(Long id) {
        //获取testCase信息
        TestCase testCase = this.getById(id);
        List<Long> testCaseIds = new ArrayList<>();
        testCaseIds.add(id);
        Integer testCycleJoinTestCaseDelNum = 0;
        Integer testCycleTcDelNum = 0;
        Integer relationDelNum = 0;
        Integer testCaseDelNum = 0;
        Map<String,Integer> res = new HashMap<>();
        try {
            // 删除关联表
            testCycleJoinTestCaseDelNum = testCycleJoinTestCaseDao.delete(Wrappers.<TestCycleJoinTestCase>lambdaUpdate()
                    .eq(TestCycleJoinTestCase::getTestCaseId, id)
                    .eq(TestCycleJoinTestCase::getProjectId, testCase.getProjectId())
            );
            testCycleTcDelNum = testCycleTcDao.delete(new LambdaQueryWrapper<TestCasesExecution>().in(TestCasesExecution::getTestCaseId, id).eq(TestCasesExecution::getProjectId, testCase.getProjectId()));
            relationDelNum = relationService.removeBatchByTestCaseIds(testCaseIds);
            testCaseDelNum = testCaseDao.deleteById(id);

            res.put("testCase",testCaseDelNum);
            res.put("testCycleJoinTestCase",testCycleJoinTestCaseDelNum);
            res.put("testCasesExecution",testCycleTcDelNum);
            res.put("relation",testCaseDelNum);
        } catch (Exception e) {
            log.error("删除测试用例失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<Map>().fail();
        }
        return new Resp.Builder<Map>().buildResult(SysConstantEnum.SUCCESS.getCode(),SysConstantEnum.SUCCESS.getValue(),res);
    }

}
