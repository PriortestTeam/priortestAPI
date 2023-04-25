package com.hu.oneclick.controller;

import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.common.page.BaseController;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.Issue;
import com.hu.oneclick.model.domain.dto.IssueSaveDto;
import com.hu.oneclick.model.domain.param.IssueParam;
import com.hu.oneclick.server.service.IssueService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("issue")
@Api(tags = "缺陷")
@Slf4j
public class IssueController extends BaseController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @ApiOperation("列表")
    @PostMapping("/list")
    public Resp<PageInfo<Issue>> list(@RequestBody IssueParam param) {
        if (null == param) {
            param = new IssueParam();
        }
        startPage();
        List<Issue> dataList = this.issueService.list(param);
        return new Resp.Builder<PageInfo<Issue>>().setData(PageInfo.of(dataList)).ok();
    }


    @ApiOperation("新增")
    @PostMapping("/save")
    public Resp<?> save(@RequestBody @Validated IssueSaveDto dto) {
        try {
            Issue issue = this.issueService.add(dto);
            return new Resp.Builder<Issue>().setData(issue).ok();
        } catch (Exception e) {
            log.error("新增缺陷失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<Issue>().fail();
        }
    }

    @ApiOperation("修改")
    @PutMapping("/update")
    public Resp<Issue> update(@RequestBody @Validated IssueSaveDto dto) {
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

    @ApiOperation("详情")
    @GetMapping("/info/{id}")
    public Resp<Issue> info(@PathVariable Long id) {
        Issue issue = this.issueService.info(id);
        return new Resp.Builder<Issue>().setData(issue).ok();
    }

    @ApiOperation("删除")
    @DeleteMapping("/delete/{ids}")
    public Resp<?> delete(@PathVariable Long[] ids) {
        try {
            this.issueService.removeByIds(Arrays.asList(ids));
        } catch (Exception e) {
            log.error("删除测试用例失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<Issue>().fail();
        }
        return new Resp.Builder<Issue>().ok();
    }


    @ApiOperation("克隆")
    @PostMapping("/clone")
    public Resp<?> clone(@RequestBody @Validated Long[] ids) {
        try {
            this.issueService.clone(Arrays.asList(ids));
            return new Resp.Builder<>().ok();
        } catch (Exception e) {
            log.error("克隆测试用例失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<>().fail();
        }
    }


}
