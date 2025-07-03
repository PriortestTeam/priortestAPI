package com.hu.oneclick.controller;

import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.page.BaseController;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.View;
import com.hu.oneclick.model.domain.dto.ViewScopeChildParams;
import com.hu.oneclick.model.domain.dto.ViewTreeDto;
import com.hu.oneclick.model.param.ViewGetSubViewRecordParam;
import com.hu.oneclick.server.service.ViewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Map;

/**
 * @author qingyang
 */
@RestController
@RequestMapping("view")
@Tag(name = "视图管理", description = "视图管理相关接口")
@Slf4j
public class ViewController extends BaseController {

    @Resource
    private ViewService viewService;
    @Resource
    private JwtUserServiceImpl jwtUserService;


    @GetMapping("queryDoesExistByTitle")
    public Resp<String> queryDoesExistByTitle(@RequestParam("projectId") String projectId,
                                              @RequestParam("title") String title,
                                              @RequestParam("scope") String scope) {
        return viewService.queryDoesExistByTitle(projectId, title, scope);
    }

    @GetMapping("getViewScopeChildParams")
    @Operation(summary="根据范围搜索所有字段(弃用请使用getViewScope")
    public Resp<List<ViewScopeChildParams>> getViewScopeChildParams(@RequestParam String scope) {
        return viewService.getViewScopeChildParams(scope);
    }

    /**
     * @Param: [scope]
     * @return: com.hu.oneclick.model.base.Resp<java.util.Map < java.lang.String, java.lang.Object>>
     * @Author: MaSiyi
     * @Date: 2021/12/29
     */
    @GetMapping("getViewScope")
    @Operation(summary="根据范围搜索所有字段")
    public Resp<Map<String, Object>> getViewScope(@RequestParam String scope) {
        return viewService.getViewScope(scope);
    }

    @PostMapping("queryViews")
    @Operation(summary="查询以当前项目的所有视图")
    private Resp<PageInfo<View>> queryViews(@RequestBody View view) {
        try {
            startPage();
            List<View> list = viewService.list(view);
            return new Resp.Builder<PageInfo<View>>().setData(PageInfo.of(list)).ok();
        } catch (Exception e) {
            log.error("查询失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<PageInfo<View>>().fail();
        }
    }

    @PostMapping("addView")
    @Operation(summary="添加视图(已弃用请使用addViewRE")
    private Resp<String> addView(@RequestBody View view) {
        return viewService.addView(view);
    }


    @PostMapping("addViewRE")
    @Operation(summary="添加视图(新")
    private Resp<?> addViewRE(@RequestBody View view) {
        try {
            view = viewService.addViewRE(view);
            return new Resp.Builder<>().ok();
        } catch (BizException e) {
            log.error("新增失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<>().buildResult(e.getCode(), e.getMessage(), 400);
        } catch (Exception e) {
            log.error("新增失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<>().fail();
        }
    }

    @PostMapping("updateView")
    @Operation(summary="修改视图")
    private Resp<?> updateView(@RequestBody View view) {
        try {
            view = viewService.updateView(view);
            return new Resp.Builder<>().ok();
        } catch (BizException e) {
            log.error("修改失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<>().buildResult(e.getCode(), e.getMessage(), 400);
        } catch (Exception e) {
            log.error("修改失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<>().fail();
        }
    }

    @DeleteMapping("deleteView/{viewId}")
    private Resp<String> deleteView(@PathVariable String viewId) {
        return viewService.deleteView(viewId);
    }


    @GetMapping("queryById/{viewId}")
    @Operation(summary="根据ID查询视图详细信息")
    private Resp<View> queryById(@PathVariable String viewId) {
        try {
            return viewService.queryById(viewId);
        } catch (Exception e) {
            log.error("查询视图详细信息失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<View>().fail();
        }
    }

    @GetMapping("queryViewParents")
    @Operation(summary="查询父视图")
    private Resp<List<View>> queryViewParents(@RequestParam String scope, @RequestParam String projectId) {
        try {
            List<View> views = viewService.queryViewParents(scope, projectId);
            return new Resp.Builder<List<View>>().setData(views).ok();
        } catch (Exception e) {
            log.error("查询失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<List<View>>().fail();
        }
    }

    @GetMapping("queryViewTrees")
    @Operation(summary="查询视图树")
    private Resp<List<ViewTreeDto>> queryViewTrees(@RequestParam String scope) {
        return viewService.queryViewTrees(scope);
    }

    @PostMapping("renderingView")
    @Operation(summary="渲染视图")
    public Resp<Object> renderingView(@RequestBody String viewId) {
        try {
            return viewService.renderingView(viewId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping("getViewFilter")
    @Operation(summary="获取filter字段")
    public Resp<Object> getViewFilter() {
        return viewService.getViewFilter();
    }

    @PostMapping("getSubViewRecord")
    public Object getSubViewRecord(
        @RequestParam(name = "pageNum", defaultValue = "1") @Min(1) int page,
        @RequestParam(name = "pageSize", defaultValue = "20") @Min(20) @Max(20) int offset,
        @RequestBody ViewGetSubViewRecordParam param)
    {
        return viewService.findSubViewRecordByScopeName(page, offset, param);
    }

    @GetMapping("getCountAsVersion")
    public Resp<Map<String, Object>> getCountAsVersion(@RequestParam String projectId, @RequestParam String version) {
        return viewService.getCountAsVersion(projectId, version);
    }
}
