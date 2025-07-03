package com.hu.oneclick.controller;

import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.common.page.BaseController;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.Sprint;
import com.hu.oneclick.model.domain.dto.SprintSaveDto;
import com.hu.oneclick.model.param.SprintParam;
import com.hu.oneclick.server.service.SprintService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import cn.hutool.core.bean.BeanUtil;

import java.util.Arrays;
import java.util.List;

/**
 * @author qingyang
 */
@RestController
@RequestMapping("sprint")
@Slf4j
public class SprintController extends BaseController {

    private final SprintService sprintService;

    public SprintController(SprintService sprintService) {
        this.sprintService = sprintService;
    }


    @Operation(summary="列表")
    @PostMapping("/list")
    public Resp<PageInfo<Sprint>> list(@RequestBody java.util.Map<String, Object> param,
                                       @RequestParam(value = "pageNum", required = false) Integer urlPageNum,
                                       @RequestParam(value = "pageSize", required = false) Integer urlPageSize) {
        int pageNum = urlPageNum != null ? urlPageNum : (param.get("pageNum") != null ? Integer.parseInt(param.get("pageNum").toString()) : 1);
        int pageSize = urlPageSize != null ? urlPageSize : (param.get("pageSize") != null ? Integer.parseInt(param.get("pageSize").toString()) : 20);

        log.info("SprintController.list - URL参数: urlPageNum={}, urlPageSize={}", urlPageNum, urlPageSize);
        log.info("SprintController.list - 请求体参数: param.pageNum={}, param.pageSize={}", param.get("pageNum"), param.get("pageSize"));
        log.info("SprintController.list - 最终使用: pageNum={}, pageSize={}", pageNum, pageSize);

        // 3. 字段过滤
        if (param.containsKey("fieldNameEn") && param.containsKey("value") && param.containsKey("scopeName")) {
            String fieldNameEn = param.get("fieldNameEn").toString();
            String value = param.get("value").toString();
            String scopeName = param.get("scopeName").toString();
            String scopeId = param.get("scopeId") != null ? param.get("scopeId").toString() : null;
            log.info("SprintController.list - 第三种参数类型: fieldNameEn={}, value={}, scopeName={}, scopeId={}", fieldNameEn, value, scopeName, scopeId);
            PageInfo<Sprint> pageInfo = sprintService.queryByFieldAndValue(fieldNameEn, value, scopeName, scopeId, pageNum, pageSize);
            return new Resp.Builder<PageInfo<Sprint>>().setData(pageInfo).ok();
        }
        // 2. 视图过滤
        else if (param.containsKey("viewId") && param.get("viewId") != null && !param.get("viewId").toString().isEmpty()) {
            String viewId = param.get("viewId").toString();
            String projectId = param.get("projectId").toString();
            log.info("SprintController.list - 第二种参数类型: viewId={}, projectId={}", viewId, projectId);
            PageInfo<Sprint> pageInfo = sprintService.listWithBeanSearcher(viewId, projectId, pageNum, pageSize);
            return new Resp.Builder<PageInfo<Sprint>>().setData(pageInfo).ok();
        }
        // 1. 普通
        else if (param.containsKey("projectId")) {
            SprintParam sprintParam = BeanUtil.toBean(param, SprintParam.class);
            log.info("SprintController.list - 第一种参数类型: projectId={}", sprintParam.getProjectId());
            PageInfo<Sprint> pageInfo = sprintService.listWithViewFilter(sprintParam, pageNum, pageSize);
            return new Resp.Builder<PageInfo<Sprint>>().setData(pageInfo).ok();
        } else {
            return new Resp.Builder<PageInfo<Sprint>>().fail();
        }
    }

    @Operation(summary="新增")
    @PostMapping("/save")
    public Resp<?> save(@RequestBody @Validated SprintSaveDto dto) {
        try {
            Sprint sprint = this.sprintService.add(dto);
            return new Resp.Builder<Sprint>().setData(sprint).ok();
        } catch (Exception e) {
            log.error("新增迭代失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<Sprint>().fail();
        }
    }

    @Operation(summary="修改")
    @PutMapping("/update")
    public Resp<Sprint> update(@RequestBody @Validated SprintSaveDto dto) {
        try {
            if (null == dto.getId()) {
                throw new BaseException("id不能为空");
            }
            Sprint feature = this.sprintService.edit(dto);
            return new Resp.Builder<Sprint>().setData(feature).ok();
        } catch (Exception e) {
            log.error("修改迭代失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<Sprint>().fail();
        }
    }

    @Operation(summary="详情")
    @GetMapping("/info/{id}")
    public Resp<Sprint> info(@PathVariable Long id) {
        Sprint sprint = this.sprintService.info(id);
        return new Resp.Builder<Sprint>().setData(sprint).ok();
    }

    @Operation(summary="删除")
    @DeleteMapping("/delete/{ids}")
    public Resp<?> delete(@PathVariable Long[] ids) {
        try {
            this.sprintService.removeByIds(Arrays.asList(ids));
        } catch (Exception e) {
            log.error("删除故事用例失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<Sprint>().fail();
        }
        return new Resp.Builder<Sprint>().ok();
    }


    @Operation(summary="克隆")
    @PostMapping("/clone")
    public Resp<?> clone(@RequestBody @Validated Long[] ids) {
        try {
            this.sprintService.clone(Arrays.asList(ids));
            return new Resp.Builder<>().ok();
        } catch (Exception e) {
            log.error("克隆故事用例失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<>().fail();
        }
    }


}
