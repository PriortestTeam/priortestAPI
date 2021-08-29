package com.hu.oneclick.server.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.FeatureDao;
import com.hu.oneclick.dao.TestCaseDao;
import com.hu.oneclick.dao.TestCaseStepDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.Feature;
import com.hu.oneclick.model.domain.ModifyRecord;
import com.hu.oneclick.model.domain.TestCase;
import com.hu.oneclick.model.domain.TestCaseStep;
import com.hu.oneclick.model.domain.dto.*;
import com.hu.oneclick.server.service.ModifyRecordsService;
import com.hu.oneclick.server.service.QueryFilterService;
import com.hu.oneclick.server.service.SysCustomFieldService;
import com.hu.oneclick.server.service.TestCaseService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

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

    private final SysCustomFieldService sysCustomFieldService;

    private final TestCaseStepDao testCaseStepDao;

    public TestCaseServiceImpl(TestCaseDao testCaseDao, ModifyRecordsService modifyRecordsService, JwtUserServiceImpl jwtUserService
            , QueryFilterService queryFilterService, FeatureDao featureDao,SysCustomFieldService sysCustomFieldService, TestCaseStepDao testCaseStepDao) {
        this.testCaseDao = testCaseDao;
        this.modifyRecordsService = modifyRecordsService;
        this.jwtUserService = jwtUserService;
        this.queryFilterService = queryFilterService;
        this.featureDao = featureDao;
        this.sysCustomFieldService = sysCustomFieldService;
        this.testCaseStepDao = testCaseStepDao;
    }


    @Override
    public Resp<List<LeftJoinDto>> queryTitles(String projectId, String title) {
        List<LeftJoinDto> select = testCaseDao.queryTitles(projectId,title,jwtUserService.getMasterId());
        return new Resp.Builder<List<LeftJoinDto>>().setData(select).total(select).ok();
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<ImportTestCaseDto> importTestCase(MultipartFile multipartFile, String param) {
        try {
            //1.取出文件并验证文件；
            //原始文件名称
            String originalFilename = multipartFile.getOriginalFilename();
            //解析到文件后缀，判断是否合法
            int lastIndexOf = originalFilename.lastIndexOf(".");
            String suffix = null;
            if(lastIndexOf==-1||(suffix=originalFilename.substring(lastIndexOf+1)).isEmpty()){
                //文件后缀不能为空
                throw new BizException(SysConstantEnum.UPLOAD_FILE_FAILED.getCode(),"文件后缀不能为空！");
            }
            //支持.et, .xlsx, .xls, .csv格式
            Set<String> allowSuffix = new HashSet<>(Arrays.asList("et", "xlsx", "xls", "csv"));
            if (!allowSuffix.contains(suffix.toLowerCase())) {
                throw new BizException(SysConstantEnum.UPLOAD_FILE_FAILED.getCode(),"非法的文件，不允许的文件类型:"+suffix);
            }
            //解析excel文件
            JSONObject jsonObject = JSONObject.parseObject(param);
            //是否忽略第一行表头 1是 0否
            Integer ifIgnorFirstRow = jsonObject.getInteger("ifIgnorFirstRow");
            //构建导入测试模板获取列对应的cell下标
            JSONObject cellIndexObject = buildCellIndexByTemplateTestCase(jsonObject);
            List<String> allowPriority =Arrays.asList("高", "中", "低");
            List<String> allowBrowser = Arrays.asList("Google Chrome", "Fire Fox", "IE");
            List<String> allowPlatform = Arrays.asList("window", "mac");
            List<String> statusPlatform = Arrays.asList("成功", "准备","草稿");
            List<String> moudleMergeValues = sysCustomFieldService.getSysCustomField("moudle").getData().getMergeValues();
            List<String> versionsMergeValues = sysCustomFieldService.getSysCustomField("versions").getData().getMergeValues();
            List<String> testCategoryMergeValues = sysCustomFieldService.getSysCustomField("testCategory").getData().getMergeValues();
            List<String> testTypeMergeValues = sysCustomFieldService.getSysCustomField("testType").getData().getMergeValues();
            List<String> testEnvMergeValues = sysCustomFieldService.getSysCustomField("testEnv").getData().getMergeValues();
            List<String> testDeviceMergeValues = sysCustomFieldService.getSysCustomField("testDevice").getData().getMergeValues();
            List<String> testMethodMergeValues = sysCustomFieldService.getSysCustomField("testMethod").getData().getMergeValues();
            Map<SysConstantEnum, Map<String, String>> errorTipsMap = new HashMap<>();
            int successCount = 0;
            int errorCount = 0;
            //判断文件后缀，根据不同后缀操作数据
            if(suffix.equals("xlsx")||suffix.equals("xls")||suffix.equals("et")){
                Workbook workbook= null;
               // 如果是xls，使用HSSFWorkbook；如果是xlsx，使用XSSFWorkbook
                try {
                    if(suffix.equals("xls")){
                        workbook = new HSSFWorkbook(multipartFile.getInputStream());
                    } else {
                        workbook = new XSSFWorkbook(multipartFile.getInputStream());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int numberOfSheets = workbook.getNumberOfSheets();
                for (int i = 0; i < numberOfSheets; i++) {
                    Sheet sheet = workbook.getSheetAt(i);
                    int lastRowNum = sheet.getPhysicalNumberOfRows();
                    for (int rowNum = 0; rowNum < lastRowNum; rowNum++) {
                        if (1==ifIgnorFirstRow&&rowNum==0) {
                            continue;
                        }
                        Boolean errorFlag = false;
                        Row row = sheet.getRow(rowNum);
                        TestCase testCase = new TestCase();
                        //处理Feature 故事
                        if (cellIndexObject.containsKey("featureCol")) {
                            Cell cell = row.getCell(cellIndexObject.getInteger("featureCol"));
                            String title = null;
                            if (null == cell || StringUtils.isBlank((title=cell.getStringCellValue()))) {
                                //记录错误，故事标题不能为空
                                buildErrorTips(errorTipsMap,SysConstantEnum.IMPORT_TESTCASE_ERROR_REQUIRED
                                        ,jsonObject.getString("featureCol"),rowNum,null);
                                errorFlag = true;
                            }
                            Feature feature = queryFeatureByTitle(title);
                            if(null==feature){
                                //记录错误，未查询到此项目的标题
                                buildErrorTips(errorTipsMap,SysConstantEnum.IMPORT_TESTCASE_ERROR_NOFEATURE
                                        ,jsonObject.getString("featureCol"),rowNum,null);
                                errorFlag = true;
                            }else{
                                testCase.setProjectId(feature.getProjectId());
                                testCase.setFeature(feature.getId());
                            }
                        }
                        //处理文本字段
                        //title
                        setValue(cellIndexObject,"testTitleCol",row,testCase
                                ,jsonObject,errorTipsMap,"title",true,errorFlag);
                        //Pre-condition 测试条件
                        setValue(cellIndexObject,"preConditionCol",row,testCase
                                ,jsonObject,errorTipsMap,"preCondition",false,errorFlag);
                        //Comments 备注
                        setValue(cellIndexObject,"descriptionCol",row,testCase
                                ,jsonObject,errorTipsMap,"description",false,errorFlag);
                        //ExternalID
                        setValue(cellIndexObject,"externalIdCol",row,testCase
                                ,jsonObject,errorTipsMap,"externaId",false,errorFlag);
                        //处理固定字典类型
                       // Priority 优先级
                        setSelectValue(cellIndexObject,"priorityCol",row,allowPriority,testCase
                                ,jsonObject,errorTipsMap,"priority",errorFlag,true);
                        //Browser 浏览器
                        setSelectValue(cellIndexObject,"browserCol",row,allowBrowser,testCase
                                ,jsonObject,errorTipsMap,"browser",errorFlag,false);
                        //Platform 平台
                        setSelectValue(cellIndexObject,"platformCol",row,allowPlatform,testCase
                                ,jsonObject,errorTipsMap,"platform",errorFlag,false);
                        //处理动态字典类型
                        //Module 模块
                        setSelectValue(cellIndexObject,"moduleCol",row,moudleMergeValues,testCase
                                ,jsonObject,errorTipsMap,"module",errorFlag,true);
                        //DeviceType 测试设备
                        setSelectValue(cellIndexObject,"deviceTypeCol",row,testDeviceMergeValues,testCase
                                ,jsonObject,errorTipsMap,"testDevice",errorFlag,false);
                        //Env 测试环境
                        setSelectValue(cellIndexObject,"envCol",row,testEnvMergeValues,testCase
                                ,jsonObject,errorTipsMap,"env",errorFlag,true);
                        //Version
                        setSelectValue(cellIndexObject,"versionCol",row,versionsMergeValues,testCase
                                ,jsonObject,errorTipsMap,"version",errorFlag,true);
                        //CaseCategory  测试分类
                        setSelectValue(cellIndexObject,"caseCategoryCol",row,testCategoryMergeValues,testCase
                                ,jsonObject,errorTipsMap,"caseCategory",errorFlag,true);
                        //CaseType 测试类型
                        setSelectValue(cellIndexObject,"caseTypeCol",row,testTypeMergeValues,testCase
                                ,jsonObject,errorTipsMap,"testType",errorFlag,true);
                        //Automation 测试方法
                        setSelectValue(cellIndexObject,"automationCol",row,testMethodMergeValues,testCase
                                ,jsonObject,errorTipsMap,"testMethod",errorFlag,true);

                        //处理 Step
                        List<TestCaseStep> testCaseSteps = new ArrayList<>();
                        if (cellIndexObject.containsKey("stepCol")) {
                            String setpValue = getCellValue(cellIndexObject, errorTipsMap, jsonObject,
                                    row, "stepCol", true, errorFlag);

                            //测试数据
                            String cellTestDataValue = getCellValue(cellIndexObject, errorTipsMap, jsonObject,
                                    row, "stepTestDataCol", true, errorFlag);

                            //Expected Result 预期结果
                            String cellExpectedResultValue = getCellValue(cellIndexObject, errorTipsMap, jsonObject,
                                    row, "stepExpectResultCol", true, errorFlag);

                            //Actual Result 实际结果
                            String cellActualResultValue = getCellValue(cellIndexObject, errorTipsMap, jsonObject,
                                    row, "stepActualResultCol", true, errorFlag);
                            Integer ifSplitTestStep=jsonObject.getInteger("ifSplitTestStep");
                            //是否分隔
                            if(1==ifSplitTestStep){
                                String splitTestStep = jsonObject.getString("splitTestStep");
                                String[] steps = setpValue.split(splitTestStep);
                                String[] testDatas = cellTestDataValue.split(splitTestStep);
                                String[] expectedResults = cellExpectedResultValue.split(splitTestStep);
                                String[] actualResult = cellActualResultValue.split(splitTestStep);
                                for (int i1 = 0; i1 < steps.length; i1++) {
                                    int stepsLength = steps.length;
                                    TestCaseStep testCaseStep = new TestCaseStep();
                                    testCaseStep.setTestCaseId(testCase.getId());
                                    testCaseStep.setCreateTime(new Date());
                                    testCaseStep.setStatus(0);
                                    testCaseStep.setStep(steps[i1]);
                                    //如果测试数据的长度与测试名称一致 则分开存储
                                    if(stepsLength==testDatas.length){
                                        testCaseStep.setTestData(testDatas[i1]);
                                    }else{          //如果不相等则每个步骤都插入一致的测试数据
                                        testCaseStep.setTestData(cellTestDataValue);
                                    }
                                    //预期结果的长度与测试名称一致 则分开存储
                                    if(stepsLength==expectedResults.length){
                                        testCaseStep.setExpectedResult(expectedResults[i1]);
                                    }else{          //如果不相等则每个步骤都插入一致的测试数据
                                        testCaseStep.setExpectedResult(cellExpectedResultValue);
                                    }
                                    //实际结果的长度与测试名称一致 则分开存储
                                    if(stepsLength==actualResult.length){
                                        //testCaseStep.setStatus(actualResult[i1]);
                                    }else{          //如果不相等则每个步骤都插入一致的测试数据
                                        //testCaseStep.setStatus(cellActualResultValue);
                                    }
                                    testCaseSteps.add(testCaseStep);
                                }
                            }else{
                                TestCaseStep testCaseStep = new TestCaseStep();
                                testCaseStep.setTestCaseId(testCase.getId());
                                testCaseStep.setTestData(setpValue);
                                testCaseStep.setTestData(cellTestDataValue);
                                testCaseStep.setExpectedResult(cellExpectedResultValue);
                                testCaseStep.setStatus(0);
                                testCaseStep.setCreateTime(new Date());
                                //testCaseStep.setStatus(cellActualResultValue);
                                testCaseSteps.add(testCaseStep);
                            }
                        }else{
                            buildErrorTips(errorTipsMap,SysConstantEnum.IMPORT_TESTCASE_ERROR_REQUIRED
                                    ,jsonObject.getString("stepCol"),rowNum,null);
                            errorFlag = true;
                        }
                        //判断是否异常
                        if(!errorFlag){
                            Resp<String> insert = this.insert(testCase);
                            if (insert.getCode().equals("200")) {
                                for (TestCaseStep testCaseStep : testCaseSteps) {
                                    testCaseStepDao.insert(testCaseStep);
                                }
                                successCount++;
                            }else{
                                throw new BizException(insert.getCode(),insert.getMsg());
                            }
                        }else{
                            errorCount++;
                        }
                    }
                }
            }
            return new Resp.Builder<ImportTestCaseDto>().setData(buildImportTestCaseDto(errorTipsMap, successCount)).ok();
        }catch (BizException e){
            logger.error("class: TestCaseServiceImpl#importTestCase,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<ImportTestCaseDto>().buildResult(e.getCode(),e.getMessage());
        }
    }

    /**
     * 构建导入返回参数
     * @param errorTipsMap
     * @param successCount
     * @return
     */
    private ImportTestCaseDto buildImportTestCaseDto(Map<SysConstantEnum, Map<String, String>> errorTipsMap,
                                                     int successCount){
        ImportTestCaseDto importTestCaseDto = new ImportTestCaseDto();
        importTestCaseDto.setSuccess(new ArrayList());
        importTestCaseDto.getSuccess().add("成功导入"+successCount+"条测试用例");
        importTestCaseDto.setError(new ArrayList());
        for (SysConstantEnum sysConstantEnum : errorTipsMap.keySet()) {
            Map<String, List<String>> errorMap = new HashMap<>();
            List<String> strings = new ArrayList<>();
            Map<String, String> stringStringMap = errorTipsMap.get(sysConstantEnum);
            for (String s : stringStringMap.keySet()) {
                if(sysConstantEnum.equals(SysConstantEnum.IMPORT_TESTCASE_ERROR_NOTSELECT)){//如果是下拉菜单，取出key进行进行分割
                    String[] split = s.split("-");
                    strings.add("列" + split[0] + "的值应该【" + (split.length>=2? split[1]:"")+ "】,错误行:"+stringStringMap.get(s));
                }else{
                    strings.add("错误行:"+stringStringMap.get(s));
                }
            }
            errorMap.put(sysConstantEnum.getValue(), strings);
            importTestCaseDto.getError().add(errorMap);
        }
        return importTestCaseDto;
    }

    /**
     * 获取cell的值，如果为required为true，则将错误信息插入
     * @param cellIndexObject  构建导入测试模板获取列对应的cell下标
     * @param errorTipsMap 错误提示的map
     * @param row 行
     * @param colKey 导入列的名称
     * @param required 是否必填
     * @param errorFlag 错误标识
     * @return
     */
    private String getCellValue(JSONObject cellIndexObject,
                                Map<SysConstantEnum, Map<String, String>> errorTipsMap
                                ,JSONObject jsonObjectParam
                                ,Row row,String colKey,
                                boolean required,Boolean errorFlag){
        if (cellIndexObject.containsKey(colKey)) {
            Cell cell = row.getCell(cellIndexObject.getInteger(colKey));
            String value = cell.getStringCellValue();
            if(required){
                if (StringUtils.isBlank(value)) {
                    buildErrorTips(errorTipsMap,SysConstantEnum.IMPORT_TESTCASE_ERROR_REQUIRED
                            ,jsonObjectParam.getString(colKey),row.getRowNum(),null);
                    if(errorFlag) {
                        errorFlag = false;
                    }
                }
            }
            return value;
        }
        return null;
    }

    private void setValue(JSONObject cellIndexObject,String colKey,
                                Row row,TestCase testCase,JSONObject jsonObjectParam,
                                Map<SysConstantEnum, Map<String, String>> errorTipsMap
                                ,String field,boolean required,Boolean errorFlag){
        try {
            if (cellIndexObject.containsKey(colKey)) {
                Cell cell = row.getCell(cellIndexObject.getInteger(colKey));
                String value = cell.getStringCellValue();
                if(StringUtils.isNotBlank(value)){
                    Class<?> fieldType = testCase.getClass().getDeclaredField(field).getType();
                    testCase.getClass().getMethod("set" + field.substring(0, 1).toUpperCase() + field.substring(1),
                            fieldType).invoke(testCase,value);
                }else{
                    if(required){
                        buildErrorTips(errorTipsMap,SysConstantEnum.IMPORT_TESTCASE_ERROR_REQUIRED
                                ,jsonObjectParam.getString(colKey),row.getRowNum(),null);
                        if(errorFlag) {
                            errorFlag = false;
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new BizException(e.getMessage());
        }
    }

    /**
     * 设置下拉的字段
     * @param cellIndexObject  构建导入测试模板获取列对应的cell下标
     * @param colKey 导入列的名称
     * @param row 行
     * @param allow 筛选的字典
     * @param testCase 要设置字段的对象
     * @param jsonObjectParam 前端传入的导入模板
     * @param errorTipsMap 错误提示的map
     * @param field 要设置的字段
     * @param errorFlag 是否错误
     * @throws NoSuchFieldException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void setSelectValue(JSONObject cellIndexObject,String colKey,
                      Row row,List<String> allow,TestCase testCase
                        ,JSONObject jsonObjectParam,
                      Map<SysConstantEnum, Map<String, String>> errorTipsMap
                        ,String field,Boolean errorFlag,boolean required){
        try {
            if (cellIndexObject.containsKey(colKey)) {
                Cell cell = row.getCell(cellIndexObject.getInteger(colKey));
                cell.setCellType(CellType.STRING);
                String stringCellValue = cell.getStringCellValue();
                if (StringUtils.isNotBlank(stringCellValue)) {
                    if (allow.contains(stringCellValue)) {
                        Class<?> fieldType = testCase.getClass().getDeclaredField(field).getType();
                        testCase.getClass().getMethod("set" + field.substring(0, 1).toUpperCase() + field.substring(1),
                                fieldType).invoke(testCase,stringCellValue);
                    }else {
                        buildErrorTips(errorTipsMap, SysConstantEnum.IMPORT_TESTCASE_ERROR_NOTSELECT
                                , jsonObjectParam.getString(colKey), row.getRowNum(), String.join(",",allow));
                        if(errorFlag) {
                            errorFlag = false;
                        }
                    }
                }else{
                    if(required){
                        buildErrorTips(errorTipsMap,SysConstantEnum.IMPORT_TESTCASE_ERROR_REQUIRED
                                ,jsonObjectParam.getString(colKey),row.getRowNum(),null);
                        if(errorFlag) {
                            errorFlag = false;
                        }
                    }
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
     * @param col             列号
     * @param row             行数
     * @param selectVal       下拉内容，错误类型为下拉必填
     */
    private void buildErrorTips(Map<SysConstantEnum, Map<String,String>> tipsMap, SysConstantEnum sysConstantEnum,
                                String col, int row, String selectVal) {
        Map<String,String> errorMaps = tipsMap.get(sysConstantEnum);
        //判断此错误类型是否已经添加
        if (null==errorMaps) {
            tipsMap.put(sysConstantEnum, new HashMap<>());
            errorMaps = tipsMap.get(sysConstantEnum);
        }
        //如存在下拉项，则与列拼接成为key，方便后面截取拼接返回消息
        if (StringUtils.isNoneBlank(selectVal)) {
            col += "-" + selectVal;
        }
        row ++;
        //存储错误列的行数
        String colMap = errorMaps.get(col);
        if (StringUtils.isBlank(colMap)) {
            errorMaps.put(col, row+"");
        } else {
            errorMaps.put(col, errorMaps.get(col)+"," + row);
        }
    }

    /**
     * 构建导入测试模板获取列对应的cell下标
     * @param templateTestCase
     * @return
     */
    private JSONObject buildCellIndexByTemplateTestCase(JSONObject templateTestCase){
        //返回数据：key:字段，value: 列号
        JSONObject res = new JSONObject();
        //Set<String> letter=new HashSet<>(Arrays.asList("A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"));
        String letter = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        //1.取出导入模板json中的属性，以_col结尾的说明为导入字段
        for (String s : templateTestCase.keySet()) {
            if(s.endsWith("Col")){
                Object o = null;
                if((o = templateTestCase.get(s))!=null){
                    res.put(s,letter.indexOf(o.toString()));
                }
            }
        }
        return res;
    }

    /**
     * 查询关联故事
     * @param title
     * @return
     */
    private Feature queryFeatureByTitle(String title){
        String projectId = jwtUserService.getUserLoginInfo().getSysUser().getUserUseOpenProject().getProjectId();
        Feature feature = new Feature();
        feature.setTitle(title);
        feature.setProjectId(projectId);
        feature.setId(null);
        return featureDao.selectOne(feature);
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
