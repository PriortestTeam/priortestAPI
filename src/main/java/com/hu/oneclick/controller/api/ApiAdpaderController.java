package com.hu.oneclick.controller.api;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;

import com.github.pagehelper.util.StringUtil;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.Issue;
import com.hu.oneclick.model.domain.TestCycleJoinTestCase;
import com.hu.oneclick.model.domain.dto.IssueSaveDto;
import com.hu.oneclick.model.domain.vo.TestCycleVo;
import com.hu.oneclick.server.service.IssueService;
import com.hu.oneclick.server.service.RetrieveTestCycleAsTitleService;
import com.hu.oneclick.server.service.TestCycleJoinTestCaseService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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
        cycleVo.setId(cycle.getId());
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

}
