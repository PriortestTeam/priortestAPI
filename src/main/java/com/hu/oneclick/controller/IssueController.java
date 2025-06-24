package com.hu.oneclick.controller;

import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.common.page.BaseController;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.Issue;
import com.hu.oneclick.model.domain.dto.IssueSaveDto;
import com.hu.oneclick.model.param.IssueParam;
import com.hu.oneclick.server.service.IssueService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("issue")
@Tag(name = "问题管理", description = "问题管理相关接口")
@Slf4j
public class IssueController extends BaseController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @Operation(summary = "列表", description = "获取问题列表")
    @PostMapping("/list")
    public Resp<PageInfo<Issue>> list(@RequestBody IssueParam param) {
        if (null == param) {
            param = new IssueParam();
        }
        startPage();
        List<Issue> dataList = this.issueService.list(param);
        return new Resp.Builder<PageInfo<Issue>>().setData(PageInfo.of(dataList)).ok();
    }


    @Operation(summary = "新增", description = "新增问题")
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

    @Operation(summary = "修改", description = "修改问题")
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

    @Operation(summary = "详情", description = "获取问题详情")
    @GetMapping("/info/{id}")
    public Resp<Issue> info(@PathVariable Long id) {
        Issue issue = this.issueService.info(id);
        return new Resp.Builder<Issue>().setData(issue).ok();
    }

    @Operation(summary = "删除", description = "删除问题")
    @DeleteMapping("/delete/{ids}")
    public Resp<?> delete(@PathVariable Long[] ids) {
        try {
            this.issueService.removeByIds(Arrays.asList(ids));
        } catch (Exception e) {
            log.error("删除缺陷用例失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<Issue>().fail();
        }
        return new Resp.Builder<Issue>().ok();
    }


    @Operation(summary = "克隆", description = "克隆问题")
    @PostMapping("/clone")
    public Resp<?> clone(@RequestBody @Validated Long[] ids) {
        try {
            this.issueService.clone(Arrays.asList(ids));
            return new Resp.Builder<>().ok();
        } catch (Exception e) {
            log.error("克隆缺陷用例失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<>().fail();
        }
    }


}