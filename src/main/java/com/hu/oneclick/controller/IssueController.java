package com.hu.oneclick.controller;

import cn.hutool.core.bean.BeanUtil;
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
import java.util.Map;

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
    public Resp<PageInfo<Issue>> list(@RequestBody Map<String, Object> param,
                                     @RequestParam(value = "pageNum", required = false) Integer urlPageNum,
                                     @RequestParam(value = "pageSize", required = false) Integer urlPageSize) {
        // 优先使用 URL 参数，如果没有则使用请求体参数
        int pageNum = urlPageNum != null ? urlPageNum : (param.get("pageNum") != null ? Integer.parseInt(param.get("pageNum").toString()) : 1);
        int pageSize = urlPageSize != null ? urlPageSize : (param.get("pageSize") != null ? Integer.parseInt(param.get("pageSize").toString()) : 20);

        // 添加调试日志
        log.info("IssueController.list - URL参数: urlPageNum={}, urlPageSize={}", urlPageNum, urlPageSize);
        log.info("IssueController.list - 请求体参数: param.pageNum={}, param.pageSize={}", param.get("pageNum"), param.get("pageSize"));
        log.info("IssueController.list - 最终使用: pageNum={}, pageSize={}", pageNum, pageSize);

        // 3. 子视图字段过滤参数
        if (param.containsKey("fieldNameEn") && param.containsKey("value") && param.containsKey("scopeName")) {
            String fieldNameEn = param.get("fieldNameEn").toString();
            String value = param.get("value").toString();
            String scopeName = param.get("scopeName").toString();
            String scopeId = param.get("scopeId") != null ? param.get("scopeId").toString() : null;
            log.info("IssueController.list - 第三种参数类型: fieldNameEn={}, value={}, scopeName={}, scopeId={}", fieldNameEn, value, scopeName, scopeId);
            PageInfo<Issue> pageInfo = issueService.queryByFieldAndValue(fieldNameEn, value, scopeName, scopeId, pageNum, pageSize);
            return new Resp.Builder<PageInfo<Issue>>().setData(pageInfo).ok();
        }
        // 2. 视图过滤参数
        else if (param.containsKey("viewId") && param.get("viewId") != null && !param.get("viewId").toString().isEmpty()) {
            String viewId = param.get("viewId").toString();
            String projectId = param.get("projectId").toString();
            log.info("IssueController.list - 第二种参数类型: viewId={}, projectId={}", viewId, projectId);
            PageInfo<Issue> pageInfo = issueService.listWithBeanSearcher(viewId, projectId, pageNum, pageSize);
            return new Resp.Builder<PageInfo<Issue>>().setData(pageInfo).ok();
        }
        // 1. 普通列表参数
        else if (param.containsKey("projectId")) {
            IssueParam issueParam = BeanUtil.toBean(param, IssueParam.class);
            log.info("IssueController.list - 第一种参数类型: projectId={}", issueParam.getProjectId());
            PageInfo<Issue> pageInfo = issueService.listWithViewFilter(issueParam, pageNum, pageSize);
            return new Resp.Builder<PageInfo<Issue>>().setData(pageInfo).ok();
        } else {
            return new Resp.Builder<PageInfo<Issue>>().fail();
        }
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
