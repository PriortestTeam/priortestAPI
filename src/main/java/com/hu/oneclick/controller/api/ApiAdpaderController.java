package com.hu.oneclick.controller.api;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;

import com.github.pagehelper.util.StringUtil;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.Issue;
import com.hu.oneclick.model.domain.TestCase;
import com.hu.oneclick.model.domain.TestCycleJoinTestCase;
import com.hu.oneclick.model.domain.dto.IssueSaveDto;
import com.hu.oneclick.model.domain.dto.TestCaseRunDto;
import com.hu.oneclick.model.domain.dto.TestCycleJoinTestCaseDto;
import com.hu.oneclick.model.domain.vo.TestCycleVo;
import com.hu.oneclick.relation.domain.Relation;
import com.hu.oneclick.relation.service.RelationService;
import com.hu.oneclick.server.service.IssueService;
import com.hu.oneclick.server.service.RetrieveTestCycleAsTitleService;
import com.hu.oneclick.server.service.TestCaseService;
import com.hu.oneclick.server.service.TestCycleJoinTestCaseService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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

import java.util.Map;

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
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "caseId projectId cycleId 不能为空",
                    HttpStatus.BAD_REQUEST.value());
        }
        log.info("hasCaseId ==> caseId: {}, projectId: {}, cycleId: {}", testCaseId, projectId, testCycleId);
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

    @ApiOperation("修改")
    @PutMapping("/{projectId}/Issue/udpateIssue")
    public Resp<Issue> update(@PathVariable Long projectId, @RequestBody @Validated IssueSaveDto dto) {
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
    public Resp<TestCase> getByCaseIdAndProjectId(@PathVariable("projectId") Long projectId, @RequestParam Long testCaseId) {

        TestCase testCase = testCaseService.getByIdAndProjectId(projectId, testCaseId);
        return new Resp.Builder<TestCase>().setData(testCase).ok();
    }

    @ApiOperation("根据CaseId、projectId、cycleId查找")
    @GetMapping("/{projectId}/retrieveRunCase")
    public Resp<TestCycleJoinTestCase> getByCaseIdAndProjectIdAndCycleId(@PathVariable("projectId") Long projectId,
                                                                         @RequestParam Long testCaseId,
                                                                         @RequestParam Long testCycleId) {

        TestCycleJoinTestCase testCycleJoinTestCase = testCycleJoinTestCaseService.getCycleJoinTestCaseByCaseId(testCaseId, projectId, testCycleId);
        return new Resp.Builder<TestCycleJoinTestCase>().setData(testCycleJoinTestCase).ok();
    }

    @ApiOperation("根据id、categoty查询relation")
    @GetMapping("/{projectId}/retrieveBugAsperRunCaseId")
    public Resp<Map> getRelationByCaseIdAndCategory(@PathVariable("projectId") Long projectId,
                                                    @RequestParam Long testCaseId) {

        Map<String, Object> result = relationService.getRelationListByObjectIdAndTargetIdAndCategory(testCaseId);
        return new Resp.Builder<Map>().setData(result).ok();
    }

    @ApiOperation("更改runCaseStatus")
    @PostMapping("/{projectId}/testCycle/runCaseStatusUpdate")
    public Resp runCaseStatusUpdate(@PathVariable("projectId") Long projectId,
                                         @RequestBody TestCycleJoinTestCaseDto testCycleJoinTestCaseDto) {

        return testCycleJoinTestCaseService.runCaseStatusUpdate(projectId, testCycleJoinTestCaseDto);
    }
}
