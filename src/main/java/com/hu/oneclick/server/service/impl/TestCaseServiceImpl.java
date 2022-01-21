package com.hu.oneclick.server.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.util.DateUtil;
import com.hu.oneclick.common.util.SnowFlakeUtil;
import com.hu.oneclick.dao.FeatureDao;
import com.hu.oneclick.dao.TestCaseDao;
import com.hu.oneclick.dao.TestCaseStepDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.*;
import com.hu.oneclick.model.domain.dto.*;
import com.hu.oneclick.server.service.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * @author qingyang
 */
@Service
public class TestCaseServiceImpl implements TestCaseService {

    private final static Logger logger = LoggerFactory.getLogger(TestCaseServiceImpl.class);

    @Autowired
    private TestCaseDao testCaseDao;

    @Autowired
    private ModifyRecordsService modifyRecordsService;

    @Autowired
    private JwtUserServiceImpl jwtUserService;

    @Autowired
    private QueryFilterService queryFilterService;

    @Autowired
    private FeatureDao featureDao;

    @Autowired
    private SysCustomFieldService sysCustomFieldService;

    @Autowired
    private TestCaseStepDao testCaseStepDao;

    @Autowired
    private MailService mailService;

    @Autowired
    private ViewService viewService;


    @Value("${onclick.default.frontUrl}")
    private String frontUrl;



    /*public TestCaseServiceImpl(TestCaseDao testCaseDao, ModifyRecordsService modifyRecordsService, JwtUserServiceImpl jwtUserService
            , QueryFilterService queryFilterService, FeatureDao featureDao,SysCustomFieldService sysCustomFieldService
            , TestCaseStepDao testCaseStepDao,MailService mailService,ViewService viewService) {
        this.testCaseDao = testCaseDao;
        this.modifyRecordsService = modifyRecordsService;
        this.jwtUserService = jwtUserService;
        this.queryFilterService = queryFilterService;
        this.featureDao = featureDao;
        this.sysCustomFieldService = sysCustomFieldService;
        this.testCaseStepDao = testCaseStepDao;
        this.mailService = mailService;
        this.viewService = viewService;
    }*/


    @Override
    public Resp<List<LeftJoinDto>> queryTitles(String projectId, String title) {
        List<LeftJoinDto> select = testCaseDao.queryTitles(projectId, title, jwtUserService.getMasterId());
        return new Resp.Builder<List<LeftJoinDto>>().setData(select).total(select).ok();
    }


    @Override
    public Resp<TestCase> queryById(String id) {
        String masterId = jwtUserService.getMasterId();
        TestCase testCase = testCaseDao.queryById(id, masterId);
        return new Resp.Builder<TestCase>().setData(testCase).ok();
    }

    @Override
    public Resp<List<TestCase>> queryList(TestCaseDto testCase) {
        try {
            testCase.queryListVerify();
            String masterId = jwtUserService.getMasterId();
            testCase.setUserId(masterId);

            testCase.setFilter(queryFilterService.mysqlFilterProcess(testCase.getViewTreeDto(), masterId));

            List<TestCase> select = testCaseDao.queryList(testCase);
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
            testCase.verify();
            //验证是否存在
            verifyIsExist(testCase.getFeature(), testCase.getProjectId(), testCase.getExternaId(), null);
            testCase.setUserId(jwtUserService.getMasterId());
            testCase.setAuthorName(jwtUserService.getUserLoginInfo().getSysUser().getUserName());
            //判断创建时间是否传入，如未传入自动生成
            if (null == testCase.getCreateTime()) {
                Date date = new Date();
                testCase.setCreateTime(date);
                testCase.setUpdateTime(date);
            }
            return Result.addResult(testCaseDao.insert(testCase));
        } catch (BizException e) {
            logger.error("class: TestCaseServiceImpl#insert,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> update(TestCase testCase) {
        try {
            //验证是否存在
            verifyIsExist(testCase.getFeature(), testCase.getProjectId(), testCase.getExternaId(), testCase.getId());
            testCase.setUserId(jwtUserService.getMasterId());
            //新增修改字段记录
            modifyRecord(testCase);
            if (null == testCase.getUpdateTime()) {
                testCase.setUpdateTime(new Date());
            }
            return Result.updateResult(testCaseDao.update(testCase));
        } catch (BizException e) {
            logger.error("class: TestCaseServiceImpl#update,error []" + e.getMessage());
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
        try {
            TestCase query = testCaseDao.queryById(testCase.getId(), testCase.getUserId());
            if (query == null) {
                throw new RuntimeException();
            }

            Field[] fields = testCase.getClass().getDeclaredFields();

            Field[] fields2 = query.getClass().getDeclaredFields();
            List<ModifyRecord> modifyRecords = new ArrayList<>();
            for (int i = 0, len = fields.length; i < len; i++) {
                String field = fields[i].getName(); //获取字段名

                fields[i].setAccessible(true);
                fields2[i].setAccessible(true);

                if (field.equals("id")
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
            if (modifyRecords.size() <= 0) {
                return;
            }
            modifyRecordsService.insert(modifyRecords);
        } catch (IllegalAccessException e) {
            throw new BizException(SysConstantEnum.ADD_FAILED.getCode(), "修改字段新增失败！");
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> delete(String id) {
        try {
            TestCase testCase = new TestCase();
            testCase.setId(id);
            return Result.deleteResult(testCaseDao.delete(testCase));
        } catch (BizException e) {
            logger.error("class: TestCaseServiceImpl#delete,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    @Override
    public Resp<Feature> queryTestNeedByFeatureId(String featureId) {
        String masterId = jwtUserService.getMasterId();
        Feature featureDto = featureDao.queryById(featureId, masterId);
        return new Resp.Builder<Feature>().setData(featureDto).ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importTestCase(File multipartFile, String param) {

        FileInputStream fileInputStream = null;
        Map<SysConstantEnum, Map<String, String>> errorTipsMap = new HashMap<>();
        //解析excel文件
        JSONObject jsonObject = JSONObject.parseObject(param);
        //判断是否发送email
        Boolean ifSendEmail = jsonObject.getBooleanValue("ifSendEmail");
        int successCount = 0;
        int errorCount = 0;
        int updateCount = 0;
        ImportTestCaseDto importTestCaseDto = null;
        String viewTreeDtoId = "";
        String projectId = jwtUserService.getUserLoginInfo().getSysUser().getUserUseOpenProject().getProjectId();
        try {
            //1.取出文件并验证文件；
            //原始文件名称
            String originalFilename = multipartFile.getName();
            //解析到文件后缀，判断是否合法
            int lastIndexOf = originalFilename.lastIndexOf(".");
            String suffix = null;
            if (lastIndexOf == -1 || (suffix = originalFilename.substring(lastIndexOf + 1)).isEmpty()) {
                //文件后缀不能为空
                throw new BizException(SysConstantEnum.UPLOAD_FILE_FAILED.getCode(), "文件后缀不能为空！");
            }
            //支持.et, .xlsx, .xls, .csv格式
            Set<String> allowSuffix = new HashSet<>(Arrays.asList("et", "xlsx", "xls", "csv"));
            if (!allowSuffix.contains(suffix.toLowerCase())) {
                throw new BizException(SysConstantEnum.UPLOAD_FILE_FAILED.getCode(), "非法的文件，不允许的文件类型:" + suffix);
            }

            //是否忽略第一行表头 1是 0否
            Integer ifIgnorFirstRow = jsonObject.getInteger("ifIgnorFirstRow");
            //构建导入测试模板获取列对应的cell下标
            JSONObject cellIndexObject = buildCellIndexByTemplateTestCase(jsonObject);
            //List<String> allowPriority = Arrays.asList("高", "中", "低");
            //ist<String> allowBrowser = Arrays.asList("Google Chrome", "Fire Fox", "IE");
            //List<String> allowPlatform = Arrays.asList("window", "mac");
            List<String> statusPlatform = Arrays.asList("待执行", "草稿");

            List<String> priorityValues = sysCustomFieldService.getSysCustomField("priority").getData().getMergeValues();
            List<String> allowBrowserValues = sysCustomFieldService.getSysCustomField("browser").getData().getMergeValues();
            List<String> allowPlatformValues = sysCustomFieldService.getSysCustomField("test_platform").getData().getMergeValues();
            List<String> moudleMergeValues = sysCustomFieldService.getSysCustomField("moudle").getData().getMergeValues();
            List<String> versionsMergeValues = sysCustomFieldService.getSysCustomField("versions").getData().getMergeValues();
            List<String> testCategoryMergeValues = sysCustomFieldService.getSysCustomField("testCategory").getData().getMergeValues();
            List<String> testTypeMergeValues = sysCustomFieldService.getSysCustomField("testType").getData().getMergeValues();
            List<String> testEnvMergeValues = sysCustomFieldService.getSysCustomField("testEnv").getData().getMergeValues();
            List<String> testDeviceMergeValues = sysCustomFieldService.getSysCustomField("testDevice").getData().getMergeValues();
            List<String> testMethodMergeValues = sysCustomFieldService.getSysCustomField("testMethod").getData().getMergeValues();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.set(Calendar.MILLISECOND, 0);
            Date now = calendar.getTime();

            //判断文件后缀，根据不同后缀操作数据
            fileInputStream = new FileInputStream(multipartFile);
            JSONArray rowValueArray = buildRowValueArray(suffix, fileInputStream,
                    cellIndexObject, ifIgnorFirstRow);
            List<TestCase> testCases = new ArrayList<>();
            Map<String, List<TestCaseStep>> testCaseStepsMap = new HashMap<>();

            //jwtUserService.getUserLoginInfo().get
            for (Object o : rowValueArray) {
                JSONObject rowValue = (JSONObject) o;
                TestCase testCase = new TestCase();
                //处理Feature 故事
                if (rowValue.containsKey("featureCol")) {
                    JSONObject featureCol = rowValue.getJSONObject("featureCol");
                    String title = featureCol.getString("value");
                    if (StringUtils.isBlank(title)) {
                        //记录错误，故事标题不能为空
                        buildErrorTips(errorTipsMap, SysConstantEnum.IMPORT_TESTCASE_ERROR_REQUIRED
                                , featureCol, null);
                    }
                    Feature feature = queryFeatureByTitle(title);
                    if (null == feature) {
                        //记录错误，未查询到此项目的标题
                        buildErrorTips(errorTipsMap, SysConstantEnum.IMPORT_TESTCASE_ERROR_NOFEATURE
                                , featureCol, null);
                    } else {
                        testCase.setProjectId(projectId);
                        testCase.setFeature(feature.getId());
                    }
                }
                //处理文本字段
                //title
                setValue(rowValue.getJSONObject("testTitleCol"), testCase
                        , errorTipsMap, "title", true);
                //Pre-condition 测试条件
                setValue(rowValue.getJSONObject("preConditionCol"), testCase
                        , errorTipsMap, "preCondition", false);
                //description 描述
                setValue(rowValue.getJSONObject("descriptionCol"), testCase
                        , errorTipsMap, "description", false);
                //ExternalID
                setValue(rowValue.getJSONObject("externalIdCol"), testCase
                        , errorTipsMap, "externaId", false);
                //Comments 备注
                setValue(rowValue.getJSONObject("commentsCol"), testCase
                        , errorTipsMap, "comments", false);
                //测试数据
                setValue(rowValue.getJSONObject("preDataCol"), testCase
                        , errorTipsMap, "testData", false);
                //处理固定字典类型
                //status 状态
                setSelectValue(rowValue.getJSONObject("statusCol"), statusPlatform, testCase
                        , errorTipsMap, "status", false);
                //处理动态字典类型
                // Priority 优先级
                setSelectValue(rowValue.getJSONObject("priorityCol"), priorityValues, testCase
                        , errorTipsMap, "priority", true);
                //Browser 浏览器
                setSelectValue(rowValue.getJSONObject("browserCol"), allowBrowserValues, testCase
                        , errorTipsMap, "browser", false);
                //Platform 平台
                setSelectValue(rowValue.getJSONObject("platformCol"), allowPlatformValues, testCase
                        , errorTipsMap, "platform", false);
                //Module 模块
                setSelectValue(rowValue.getJSONObject("moduleCol"), moudleMergeValues, testCase
                        , errorTipsMap, "module", true);
                //DeviceType 测试设备
                setSelectValue(rowValue.getJSONObject("deviceTypeCol"), testDeviceMergeValues, testCase
                        , errorTipsMap, "testDevice", false);
                //Env 测试环境
                setSelectValue(rowValue.getJSONObject("envCol"), testEnvMergeValues, testCase
                        , errorTipsMap, "env", true);
                //Version
                setSelectValue(rowValue.getJSONObject("versionCol"), versionsMergeValues, testCase
                        , errorTipsMap, "version", false);
                //CaseCategory  测试分类
                setSelectValue(rowValue.getJSONObject("caseCategoryCol"), testCategoryMergeValues, testCase
                        , errorTipsMap, "caseCategory", true);
                //CaseType 测试类型
                setSelectValue(rowValue.getJSONObject("caseTypeCol"), testTypeMergeValues, testCase
                        , errorTipsMap, "testType", true);
                //Automation 测试方法
                setSelectValue(rowValue.getJSONObject("automationCol"), testMethodMergeValues, testCase
                        , errorTipsMap, "testMethod", false);
                ///判断是否新增或者更新，根据故事ID+ExternalID查询测试用例，如果存在则进行更新；

                //判断ExternalI是否存在，进行判断下一步是否更新
                if (jsonObject.containsKey("ifUpdateCase")) {
                    JSONObject externalIdCol = rowValue.getJSONObject("externalIdCol");
                    if (null != externalIdCol) {
                        String externalId = externalIdCol.getString("value");
                        TestCase queryFeaturExternalIDTestCase = new TestCase();
                        queryFeaturExternalIDTestCase.setFeature(testCase.getFeature());
                        queryFeaturExternalIDTestCase.setExternaId(externalId);
                        queryFeaturExternalIDTestCase.setProjectId(projectId);
                        queryFeaturExternalIDTestCase.setId(null);
                        TestCase featurExternalIDTestCase = this.testCaseDao.selectOne(queryFeaturExternalIDTestCase);
                        if (null != featurExternalIDTestCase) {
                            Boolean ifUpdateCase = jsonObject.getBooleanValue("ifUpdateCase");
                            if (ifUpdateCase) {           //进行更新
                                //将已存在ID打上标识，后续判断新增或插入
                                testCase.setId("UPDATE" + featurExternalIDTestCase.getId());
                            } else {  //如果存在，并且更新标识为否，提示用户此故事下，已经存在此ExternalID，无法进行插入
                                buildErrorTips(errorTipsMap, SysConstantEnum.IMPORT_TESTCASE_ERROR_EXIST_FEATURE_EXTERNALID
                                        , externalIdCol, null);
                            }
                        }
                    }
                }

                //处理 Step
                List<TestCaseStep> testCaseSteps = new ArrayList<>();
                if (cellIndexObject.containsKey("stepCol")) {
                    String setpValue = getCellValue(errorTipsMap,
                            rowValue.getJSONObject("stepCol"), false);

                    //测试数据
                    String cellTestDataValue = getCellValue(errorTipsMap,
                            rowValue.getJSONObject("stepTestDataCol"), false);

                    //Expected Result 预期结果
                    String cellExpectedResultValue = getCellValue(errorTipsMap,
                            rowValue.getJSONObject("stepExpectResultCol"), false);
                    Boolean ifSplitTestStep = jsonObject.getBoolean("ifSplitTestStep");
                    String splitTestStep = jsonObject.getString("splitTestStep");
                    //是否分隔
                    if (ifSplitTestStep) {

                        String[] steps = setpValue.split(splitTestStep);
                        String[] testDatas = cellTestDataValue.split(splitTestStep);
                        String[] expectedResults = cellExpectedResultValue.split(splitTestStep);

                        for (int i1 = 0; i1 < steps.length; i1++) {
                            int stepsLength = steps.length;
                            TestCaseStep testCaseStep = new TestCaseStep();
                            testCaseStep.setCreateTime(now);
                            testCaseStep.setStatus(0);
                            testCaseStep.setStep(steps[i1]);
                            //如果测试数据的长度与测试名称一致 则分开存储
                            if (stepsLength == testDatas.length) {
                                testCaseStep.setTestData(testDatas[i1].trim());
                            } else {          //如果不相等则每个步骤都插入一致的测试数据
                                testCaseStep.setTestData(cellTestDataValue.trim());
                            }
                            //预期结果的长度与测试名称一致 则分开存储
                            if (stepsLength == expectedResults.length) {
                                testCaseStep.setExpectedResult(expectedResults[i1]);
                            } else {          //如果不相等则每个步骤都插入一致的测试数据
                                testCaseStep.setExpectedResult(cellExpectedResultValue);
                            }
                            testCaseSteps.add(testCaseStep);
                        }
                    } else {
                        TestCaseStep testCaseStep = new TestCaseStep();
                        testCaseStep.setStep(setpValue);
                        testCaseStep.setTestData(cellTestDataValue.trim());
                        testCaseStep.setExpectedResult(cellExpectedResultValue);
                        testCaseStep.setStatus(0);
                        testCaseStep.setCreateTime(now);
                        testCaseSteps.add(testCaseStep);
                    }
                }/*else{
                    buildErrorTips(errorTipsMap,SysConstantEnum.IMPORT_TESTCASE_ERROR_REQUIRED
                            ,rowValue.getJSONObject("stepCol"),null);
                }*/
                testCases.add(testCase);
                testCaseStepsMap.put(testCase.getId().replace("UPDATE", ""), testCaseSteps);
            }

            //判断是否异常,如出现异常，则全部进行操作db
            if (errorTipsMap.isEmpty()) {
                //判断是否新增或者更新，根据故事ID+ExternalID查询测试用例，如果存在则进行更新；
                for (TestCase testCase : testCases) {
                    Resp<String> insertOrUpdate = null;
                    //如ID为空则进行新增
                    if (!testCase.getId().startsWith("UPDATE")) {
                        testCase.setCreateTime(now);
                        testCase.setUpdateTime(now);
                        insertOrUpdate = this.insert(testCase);
                        successCount++;
                    } else {          //如ID不为空更新
                        testCase.setId(testCase.getId().replace("UPDATE", ""));
                        testCase.setUpdateTime(now);
                        insertOrUpdate = this.update(testCase);
                        //删除测试用例步骤重新插入
                        TestCaseStep delTestCase = new TestCaseStep();
                        delTestCase.setTestCaseId(testCase.getId());
                        delTestCase.setId(null);
                        this.testCaseStepDao.delete(delTestCase);
                        updateCount++;
                    }
                    List<TestCaseStep> testCaseSteps = testCaseStepsMap.get(testCase.getId());
                    if (insertOrUpdate.getCode().equals("200")) {
                        for (TestCaseStep testCaseStep : testCaseSteps) {
                            testCaseStep.setTestCaseId(testCase.getId());
                            testCaseStepDao.insert(testCaseStep);
                        }
                    }
                }
                //判断是否创建视图
                Boolean ifCreateView = jsonObject.getBooleanValue("ifCreateView");
                if (ifCreateView && successCount > 0) {
                    viewTreeDtoId = createViewImportTestCase(now).getId();
                }
            } else {
                errorCount = rowValueArray.size();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("class: TestCaseServiceImpl#importTestCase,error []" + e.getMessage());
            buildErrorTips(errorTipsMap, SysConstantEnum.OTHER
                    , SysConstantEnum.SYSTEM_BUSY.getCode(), e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } finally {
            //刪除临时文件
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (!multipartFile.delete()) {
                    logger.error("删除文件失败");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            importTestCaseDto = buildImportTestCaseDto(errorTipsMap, successCount, updateCount, errorCount);
            importTestCaseDto.setProjectId(projectId);
            importTestCaseDto.setViewTreeDtoId(viewTreeDtoId);
            if (ifSendEmail) {
                sendEmailImportTestCase(importTestCaseDto);
            }
        }
    }

    /**
     * 导入测试用例发送emial
     *
     * @param importTestCaseDto
     */
    private void sendEmailImportTestCase(ImportTestCaseDto importTestCaseDto) {
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
        attachmentMap.put("importTestCaseDto", importTestCaseDto);
        attachmentMap.put("frontUrl", this.frontUrl+"/#/testcase/testcase?projectId="+importTestCaseDto.getProjectId()+"&viewTreeDtoId="+importTestCaseDto.getViewTreeDtoId());
        mailDto.setAttachment(attachmentMap);
        mailService.sendTemplateMail(mailDto);
    }

    /**
     * 导入测试创建视图
     *
     * @param now 导入时间
     */
    private View createViewImportTestCase(Date now) {
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
        view.setScope("TestCase");
        view.setIsPrivate(0);
        view.setTitle("创建时间：" + nowString + "~" + nowString);
        viewService.addView(view);
        return view;
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
        InputStreamReader is = new InputStreamReader(inputStream, "GBK");
        if (suffix.equals("csv")) {
            //编码格式要是用GBK
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
                    Integer colIndex = cellIndexObject.getInteger(colKey)-1;
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
                    Cell cell = row.getCell(colIndex-1);
                    String stringCellValue = "";
                    if (null != cell) {
                        cell.setCellType(CellType.STRING);
                        stringCellValue = cell.getStringCellValue();
                    }
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
                } else if (sysConstantEnum.equals(SysConstantEnum.OTHER)) {
                    strings.add(stringStringMap.get(s));
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
            , String field, boolean required) throws Exception {

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
     * 构建其他类型的错误
     *
     * @param tipsMap         原map
     * @param sysConstantEnum 错误类型
     * @param key             错误key
     * @param value           错误描述
     */
    private void buildErrorTips(Map<SysConstantEnum, Map<String, String>> tipsMap,
                                SysConstantEnum sysConstantEnum,
                                String key, String value) {
        Map<String, String> errorMaps = tipsMap.get(sysConstantEnum);
        //判断此错误类型是否已经添加
        if (null == errorMaps) {
            tipsMap.put(sysConstantEnum, new HashMap<>());
            errorMaps = tipsMap.get(sysConstantEnum);
        }
        errorMaps.put(key, value);
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
       // String letter = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        //1.取出导入模板json中的属性，以_col结尾的说明为导入字段
        for (String s : templateTestCase.keySet()) {
            if (s.endsWith("Col")) {
                String o = (String) templateTestCase.get(s);
                if (StringUtils.isNotBlank(o)) {
                    res.put(s, excelColStrToNum(o,o.length()));
                }
            }
        }
        return res;
    }

    /**
     * 根据字母字母获取列的下标
     * @param colStr
     * @param length
     * @return
     */
   private  int excelColStrToNum(String colStr, int length) {
                 int num = 0;
                 int result = 0;
                 for(int i = 0; i < length; i++) {
                     char ch = colStr.charAt(length - i - 1);
                    num = (int)(ch - 'A' + 1) ;
                    num *= Math.pow(26, i);
                  result += num;
                }
         return result;
      }

    /**
     * 根据下标获取列的字母字母
     * @param columnIndex
     * @return
     */
    private String getColLetterByIndex(int columnIndex) {
        if (columnIndex <= 0) {
            return null;
        }
        String columnStr = "";
        columnIndex--;
        do {
            if (columnStr.length() > 0) {
                columnIndex--;
            }
            columnStr = ((char) (columnIndex % 26 + (int) 'A')) + columnStr;
            columnIndex = (int) ((columnIndex - columnIndex % 26) / 26);
        } while (columnIndex > 0);
        return columnStr;
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
        feature.setProjectId(projectId);
        feature.setId(null);
        return featureDao.selectOne(feature);
    }


    /**
     * 查重,根据projectId+feature+externaId 是否存在 如存在则不能添加
     */
    private void verifyIsExist(String feature, String projectId, String externaId, String testCaseId) {
        if (StringUtils.isNotBlank(externaId)) {
            TestCase testCase = new TestCase();
            testCase.setFeature(feature);
            testCase.setExternaId(externaId);
            testCase.setProjectId(projectId);
            testCase.setId(null);
            TestCase testCaseOne = testCaseDao.selectOne(testCase);
            //如果testCaseId不为空则判断查询出ID是否与传入ID一致说明不重复
            if (testCaseId != null && testCaseOne != null && !testCaseOne.getId().equals(testCaseId)) {
                throw new BizException(SysConstantEnum.DATE_EXIST.getCode(), testCase.getTitle() + SysConstantEnum.DATE_EXIST.getValue());
            } else if (testCaseId == null && testCaseOne != null) {
                throw new BizException(SysConstantEnum.DATE_EXIST.getCode(), testCase.getTitle() + SysConstantEnum.DATE_EXIST.getValue());
            }
        }
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
        testCase.setExternaId(externaID);
        testCase.setFeature(feature);
        testCase.setId(null);
        TestCase testCaseOne = testCaseDao.selectOne(testCase);
        //如果testCaseId不为空则判断查询出ID是否与传入ID一致说明不重复
        if (testCaseId != null && testCaseOne != null && !testCaseOne.getId().equals(testCaseId)) {
            throw new BizException(SysConstantEnum.DATE_EXIST.getCode(), "externaID:" + externaID + SysConstantEnum.DATE_EXIST.getValue());
        } else if (testCaseId == null && testCaseOne != null) {
            throw new BizException(SysConstantEnum.DATE_EXIST.getCode(), "externaID:" + externaID + SysConstantEnum.DATE_EXIST.getValue());
        }
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


}
