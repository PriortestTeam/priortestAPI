package com.hu.oneclick.controller.api;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.util.StringUtil;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.util.Convert;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.Issue;
import com.hu.oneclick.model.domain.TestCase;
import com.hu.oneclick.model.domain.TestCycle;
import com.hu.oneclick.model.domain.TestCycleJoinTestCase;
import com.hu.oneclick.model.domain.dto.*;
import com.hu.oneclick.model.domain.vo.IssueStatusVo;
import com.hu.oneclick.model.domain.vo.TestCycleJoinTestCaseVo;
import com.hu.oneclick.model.domain.vo.TestCycleVo;
import com.hu.oneclick.relation.service.RelationService;
import com.hu.oneclick.server.service.*;
import io.swagger.annotations.ApiOperation;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apiAdpater")
@Slf4j
public class ApiAdpaderController {

  @Autowired
  private IssueService issueService;

  @Resource
  private TestCycleJoinTestCaseService testCycleJoinTestCaseService;
  @Resource
  private RetrieveTestCycleAsTitleService rtcatService;

  @Resource
  private TestCycleService testCycleService;

  @Resource
  private TestCaseService testCaseService;

  @Resource
  private RelationService relationService;

  @GetMapping("/{projectId}/testCycle/retrieveTestCycleAsTitle/getId")
  public Resp<TestCycleVo> getIdByTitle(@RequestParam String title, @PathVariable Long projectId) {
    if (StringUtil.isEmpty(title)) {
      throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "title不能为空",
          HttpStatus.BAD_REQUEST.value());
    }
    if (projectId == null || projectId == 0L) {
      throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "projectId不能为空",
          HttpStatus.BAD_REQUEST.value());
    }
    log.info("getIdByTitle ==> title:{}", JSON.toJSONString(title));
    log.info("getIdByTitle ==> projectId:{}", JSON.toJSONString(projectId));
    return rtcatService.getIdForTitle(title, projectId);
  }

  @GetMapping("/{projectId}/testRun/retrieveTCInTestCycle/getCaseId")
  public Resp<TestCycleVo> hasCaseId(
      @PathVariable Long projectId, @RequestParam Long testCaseId, @RequestParam Long testCycleId
  ) {
    if (testCaseId == null || projectId == null || testCycleId == null) {
      throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),
          "caseId projectId cycleId 不能为空",
          HttpStatus.BAD_REQUEST.value());
    }
    log.info("hasCaseId ==> caseId: {}, projectId: {}, cycleId: {}", testCaseId, projectId,
        testCycleId);
    TestCycleJoinTestCase cycle = testCycleJoinTestCaseService.getCycleJoinTestCaseByCaseId(
        testCaseId, projectId, testCycleId);
    TestCycleVo cycleVo = new TestCycleVo();
    if (cycle != null) {
      cycleVo.setId(String.valueOf(cycle.getId()));
    } else {
      cycleVo.setId(Strings.EMPTY);
    }
    return new Resp.Builder<TestCycleVo>().setData(cycleVo).ok();
  }

  @ApiOperation("新增")
  @PostMapping("/{projectId}/Issue/createIssue")
  public Resp<?> save(@PathVariable Long projectId, @RequestBody @Validated IssueSaveDto dto) {
    try {
      Issue issue = this.issueService.add(dto);
      return new Resp.Builder<Issue>().setData(issue).ok();
    } catch (Exception e) {
      log.error("新增缺陷失败，原因：" + e.getMessage(), e);
      return new Resp.Builder<Issue>().fail();
    }
  }

  @ApiOperation("更新缺陷")
  @PutMapping("/{projectId}/issue/statusUpdate")
  public Resp<Issue> statusUpdate(@PathVariable Long projectId,
      @RequestBody @Validated IssueStatusDto issueStatusDto) {
    try {
      if (issueStatusDto.getId() == null) {
        throw new BaseException("id不能为空");
      }
      Issue issue = this.issueService.info(issueStatusDto.getId());
      if (!Objects.equals(issue.getProjectId(), projectId)) {
//                throw new BaseException("项目id与id不匹配");
        return new Resp.Builder<Issue>().buildResult("查无缺陷", 404);
      }
      this.issueService.studusedit(issue, issueStatusDto);
      return new Resp.Builder<Issue>().setData(null).ok();

    } catch (Exception e) {
      log.error("更新缺陷失败，原因：" + e.getMessage(), e);
      return new Resp.Builder<Issue>().fail();
    }
  }

  @ApiOperation("修改")
  @PutMapping("/{projectId}/Issue/udpateIssue")
  public Resp<Issue> update(@PathVariable Long projectId,
      @RequestBody @Validated IssueSaveDto dto) {
    try {
      if (null == dto.getId()) {
        throw new BaseException("id不能为空");
      }
      Issue issue = this.issueService.edit(dto);
      return new Resp.Builder<Issue>().setData(issue).ok();
    } catch (Exception e) {
      log.error("修改缺陷失败，原因：" + e.getMessage(), e);
      return new Resp.Builder<Issue>().fail();
    }
  }

  @ApiOperation("根据CaseId、projectId查找")
  @GetMapping("/{projectId}/retrieveTestcase")
  public Resp<TestCase> getByCaseIdAndProjectId(@PathVariable("projectId") Long projectId,
      @RequestParam Long testCaseId) {

    // 参数校验
    if (testCaseId == null || String.valueOf(testCaseId).isBlank()) {
      return new Resp.Builder<TestCase>().buildResult("非法参数");
    }

    TestCase testCase = testCaseService.getByIdAndProjectId(projectId, testCaseId);
    return new Resp.Builder<TestCase>().setData(testCase).ok();
  }

  @ApiOperation("创建测试用例")
  @PostMapping("/{projectId}/createTestCase")
  public Resp<Map<String, Object>> createTestCase(@PathVariable("projectId") Long projectId,
      @RequestBody @Validated TestCaseSaveDto testCaseSaveDto) {

    final TestCase testCase = testCaseService.save(testCaseSaveDto);
    return new Resp.Builder<Map<String, Object>>().setData(
        Map.of(
            "id", testCase.getId(),
            "externalLinkId", testCase.getExternalLinkId()
        )
    ).ok();
  }

  @ApiOperation("根据CaseId、projectId、cycleId查找")
  @GetMapping("/{projectId}/retrieveRunCase")
  public Resp<TestCycleJoinTestCase> getByCaseIdAndProjectIdAndCycleId(
      @PathVariable("projectId") Long projectId,
      @RequestParam Long testCaseId,
      @RequestParam Long testCycleId) {
    // 参数校验
    if (testCaseId == null || testCycleId == null || (String.valueOf(testCaseId).isBlank()
        || String.valueOf(testCycleId).isBlank())) {
      return new Resp.Builder<TestCycleJoinTestCase>().buildResult("非法参数");
    }

    TestCycleJoinTestCase testCycleJoinTestCase =
        testCycleJoinTestCaseService.getCycleJoinTestCaseByCaseId(testCaseId, projectId,
            testCycleId);
    return new Resp.Builder<TestCycleJoinTestCase>().setData(testCycleJoinTestCase).ok();
  }

  @ApiOperation("根据id,category 查询relation")
  @GetMapping("/{projectId}/retrieveIssueAsPerTestCaseId")
  public Resp<Map> getRelationByCaseIdAndCategory(@PathVariable("projectId") Long projectId,
      @RequestParam Long testCaseId) {

    // 参数校验
    if (testCaseId == null || String.valueOf(testCaseId).isBlank()) {
      return new Resp.Builder<Map>().buildResult("非法参数");
    }

    Map<String, Object> result =
        relationService.getRelationListByObjectIdAndTargetIdAndCategory(testCaseId);
    return new Resp.Builder<Map>().setData(result).ok();
  }

  @ApiOperation("更改runCaseStatus")
  @PostMapping("/{projectId}/testCycle/runCaseStatusUpdate")
  public Resp runCaseStatusUpdate(@PathVariable("projectId") Long projectId,
      @RequestBody TestCycleJoinTestCaseDto testCycleJoinTestCaseDto) {

    try {
      return testCycleJoinTestCaseService.runCaseStatusUpdate(projectId, testCycleJoinTestCaseDto);
    } catch (Exception e) {
      log.error("",e);
      throw new RuntimeException(e);
    }
  }

  @ApiOperation("保存测试用例到测试周期")
  @PostMapping("/{projectId}/testCycle/instance/saveInstance")
  public Resp<Object> testCycleSaveInstance(@PathVariable("projectId") Long projectId,
      @RequestBody @Validated TestCycleJoinTestCaseSaveDto testCycleJoinTestCaseDto) {
    if (ArrayUtil.isEmpty(testCycleJoinTestCaseDto.getTestCaseIds())) {
      throw new BaseException("请选择至少一个测试用例进行绑定");
    }

    if (!Objects.equals(projectId, testCycleJoinTestCaseDto.getProjectId())) {
      throw new BizException(SysConstantEnum.TEST_CASE_PROJECT_ID_NOT_EXIST.getCode(),
          SysConstantEnum.TEST_CASE_PROJECT_ID_NOT_EXIST.getValue(), HttpStatus.BAD_REQUEST.value());
    }

    return testCycleJoinTestCaseService.strictlySaveInstance(testCycleJoinTestCaseDto);
  }

  @ApiOperation("通过定义的外部 ID 查询测试用例")
  @GetMapping("/{projectId}/retrieveTestcaseByExternalId")
  public Resp<TestCase> retrieveTestcaseByExternalId(
      @PathVariable("projectId") Long projectId,
      @RequestParam("externalId") String externalId) {
    return new Resp.Builder<TestCase>().setData(
        testCaseService.queryByProjectIdAndExteranlId(projectId, externalId)).ok();
  }

  @ApiOperation("获取缺陷的状态,通过缺陷Id")
  @GetMapping("/{projectId}/retrieveIssueStatusAsPerIssueId")
  public Resp<IssueStatusVo> retrieveIssueStatusAsPerIssueId(
          @PathVariable("projectId") Long projectId,
          @RequestParam("issueId") Long issueId) {
    IssueStatusVo issueStatusVo = testCaseService.retrieveIssueStatusAsPerIssueId(projectId, issueId);
    if(Objects.nonNull(issueStatusVo.getId())){
      return new Resp.Builder<IssueStatusVo>().setData(issueStatusVo).ok();
    }
    return new Resp.Builder<IssueStatusVo>().buildResult(
              SysConstantEnum.DATA_NOT_FOUND.getCode(),
              SysConstantEnum.DATA_NOT_FOUND.getValue(),
              HttpStatus.NOT_FOUND.value());
  }

  @ApiOperation("新建测试周期")
  @PostMapping("/{projectId}/testCycle/saveTestCycle")
  public Resp<TestCycle> saveTestCycle(@PathVariable("projectId") Long projectId,@RequestBody @Validated TestCycleSaveDto dto) {
    try {
        if(!StringUtils.equals(String.valueOf(projectId),String.valueOf(dto.getProjectId()))){
          return new Resp.Builder<TestCycle>().ok(String.valueOf(SysConstantEnum.TEST_CYCLE_NOT_MATE_PROJECT.getCode()),
                  SysConstantEnum.TEST_CYCLE_NOT_MATE_PROJECT.getValue(), HttpStatus.BAD_REQUEST.value());
        }
      if (Objects.nonNull(projectId)) {
        TestCycle testCycle = testCaseService.saveTestCycle(projectId, dto);
        if (Objects.isNull(testCycle)) {
          return new Resp.Builder<TestCycle>().ok(String.valueOf(HttpStatus.BAD_REQUEST.value()),
                  SysConstantEnum.DATE_EXIST_TITLE.getValue(), HttpStatus.BAD_REQUEST.value());
        }
        return new Resp.Builder<TestCycle>().setData(testCycle).ok();
      }
    } catch (Exception e) {
      log.error("新增测试周期失败，原因：" + e.getMessage(), e);
      return new Resp.Builder<TestCycle>().fail();
    }
    return new Resp.Builder<TestCycle>().fail();
  }

  @ApiOperation("移除多余测试周期用例")
  @PostMapping("/{projectId}/testCycle/instance/removeTCsFromTestCycle")
  public Resp<TestCycleJoinTestCaseVo> removeTCsFromTestCycle(@PathVariable("projectId") Long projectId,@RequestBody @Validated TestCycleJoinTestCaseSaveDto dto) {
    try {
      if(!StringUtils.equals(String.valueOf(projectId),String.valueOf(dto.getProjectId()))){
        return new Resp.Builder<TestCycleJoinTestCaseVo>().ok(String.valueOf(SysConstantEnum.TEST_CASE_PROJECT_ID_NOT_EXIST.getCode()),
                SysConstantEnum.TEST_CASE_PROJECT_ID_NOT_EXIST.getValue(), HttpStatus.BAD_REQUEST.value());
      }

      LambdaQueryWrapper<TestCycle> wapper = new LambdaQueryWrapper<TestCycle>()
              .eq(TestCycle::getProjectId, dto.getProjectId())
              .eq(TestCycle::getId, dto.getTestCycleId());
      List<TestCycle> list = testCycleService.list(wapper);
      if(Objects.isNull(list) || list.isEmpty()){
        return new Resp.Builder<TestCycleJoinTestCaseVo>().ok(String.valueOf(HttpStatus.BAD_REQUEST.value()),
                dto.getTestCycleId() + "不存在项目中", HttpStatus.BAD_REQUEST.value());
      }

      LambdaQueryWrapper<TestCase> in = new LambdaQueryWrapper<TestCase>()
              .eq(TestCase::getProjectId, dto.getProjectId())
              .in(TestCase::getId, dto.getTestCaseIds());
      List<TestCase> list1 = testCaseService.list(in);
      if(Objects.isNull(list1)){
        List<String> collect = list1.stream().map(l -> Convert.toStr(l.getId())).collect(Collectors.toList());
        String collect1 = collect.stream().collect(Collectors.joining(","));
        return new Resp.Builder<TestCycleJoinTestCaseVo>().ok(String.valueOf(HttpStatus.BAD_REQUEST.value()),
                collect1 + "不存在项目中 or 测试周期 中", HttpStatus.BAD_REQUEST.value());
      }

      if (Objects.nonNull(projectId)) {
        TestCycleJoinTestCaseVo vo =    testCycleJoinTestCaseService.removeTCsFromTestCycle(projectId, dto);
        return new Resp.Builder<TestCycleJoinTestCaseVo>().setData(vo).ok();
      }
    } catch (Exception e) {
      log.error("移除多余测试周期用例，原因：" + e.getMessage(), e);
      return new Resp.Builder<TestCycleJoinTestCaseVo>().fail();
    }
    return new Resp.Builder<TestCycleJoinTestCaseVo>().fail();
  }



  @ApiOperation(" 返回缺陷列表,以runcaseId")
  @PostMapping("/{projectId}/retrieveIssueAsPerRunCaseId")
  public Resp<List<Issue>> retrieveIssueAsPerRunCaseId(@PathVariable("projectId") Long projectId,@RequestParam Long runCaseId) {
    try {
      if(Objects.isNull(runCaseId)){
        return new Resp.Builder<List<Issue>>().ok(String.valueOf(SysConstantEnum.TEST_CASE_PROJECT_ID_NOT_EXIST.getCode()),
                "运行用例不可以为空", HttpStatus.BAD_REQUEST.value());
      }

      List<Issue> issueList = issueService.list(
              new LambdaQueryWrapper<Issue>()
                      .eq(Issue::getProjectId, projectId)
                      .eq(Issue::getRuncaseId, runCaseId)
      );
      if(CollectionUtil.isEmpty(issueList)){
        List<Issue> issueListByRuncaseId = issueService.list(
                new LambdaQueryWrapper<Issue>()
                        .eq(Issue::getRuncaseId, runCaseId)
        );
        if(CollectionUtil.isNotEmpty(issueList)){
          return new Resp.Builder<List<Issue>>().ok(String.valueOf(SysConstantEnum.TEST_CASE_PROJECT_ID_NOT_EXIST.getCode()),
                  "运行用例不存在", HttpStatus.BAD_REQUEST.value());
        }else{
          return new Resp.Builder<List<Issue>>().ok(String.valueOf(SysConstantEnum.TEST_CASE_PROJECT_ID_NOT_EXIST.getCode()),
                  "查无记录", HttpStatus.BAD_REQUEST.value());
        }
      }

      List<Issue> collect = issueList.stream().filter(issue -> !"关闭".equals(issue.getIssueStatus())).collect(Collectors.toList());
      if(CollectionUtil.isEmpty(collect)){
        return new Resp.Builder<List<Issue>>().ok(String.valueOf(SysConstantEnum.TEST_CASE_PROJECT_ID_NOT_EXIST.getCode()),
                "查无记录", HttpStatus.BAD_REQUEST.value());
      }
      return new Resp.Builder<List<Issue>>().setData(collect).ok();
    } catch (Exception e) {
      log.error("返回缺陷列表" + e.getMessage(), e);
      return new Resp.Builder<List<Issue>>().fail();
    }
  }

}
