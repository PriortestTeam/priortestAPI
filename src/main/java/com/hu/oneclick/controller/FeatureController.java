package com.hu.oneclick.controller;

import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.common.page.BaseController;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.Feature;
import com.hu.oneclick.model.domain.dto.FeatureSaveDto;
import com.hu.oneclick.model.param.FeatureParam;
import com.hu.oneclick.server.service.FeatureService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author qingyang
 */
@Tag(name = "故事", description = "故事相关接口")
@RestController
@RequestMapping("feature")
@Slf4j
public class FeatureController extends BaseController {

    private final FeatureService featureService;

    public FeatureController(FeatureService featureService) {
        this.featureService = featureService;
    }

    @Operation(summary="列表")
    @PostMapping("/list")
    public Resp<PageInfo<Feature>> list(@RequestBody Map<String, Object> param,
                                       @RequestParam(value = "pageNum", required = false) Integer urlPageNum,
                                       @RequestParam(value = "pageSize", required = false) Integer urlPageSize) {
        // 优先使用 URL 参数，如果没有则使用请求体参数
        int pageNum = urlPageNum != null ? urlPageNum : (param.get("pageNum") != null ? Integer.parseInt(param.get("pageNum").toString()) : 1);
        int pageSize = urlPageSize != null ? urlPageSize : (param.get("pageSize") != null ? Integer.parseInt(param.get("pageSize").toString()) : 20);

        // 添加调试日志
        log.info("FeatureController.list - URL参数: urlPageNum={}, urlPageSize={}", urlPageNum, urlPageSize);
        log.info("FeatureController.list - 请求体参数: param.pageNum={}, param.pageSize={}", param.get("pageNum"), param.get("pageSize"));
        log.info("FeatureController.list - 最终使用: pageNum={}, pageSize={}", pageNum, pageSize);

        // 3. 子视图字段过滤参数
        if (param.containsKey("fieldNameEn") && param.containsKey("value") && param.containsKey("scopeName")) {
            String fieldNameEn = param.get("fieldNameEn").toString();
            String value = param.get("value").toString();
            String scopeName = param.get("scopeName").toString();
            String scopeId = param.get("scopeId") != null ? param.get("scopeId").toString() : null;
            log.info("FeatureController.list - 第三种参数类型: fieldNameEn={}, value={}, scopeName={}, scopeId={}", fieldNameEn, value, scopeName, scopeId);
            PageInfo<Feature> pageInfo = featureService.queryByFieldAndValue(fieldNameEn, value, scopeName, scopeId, pageNum, pageSize);
            return new Resp.Builder<PageInfo<Feature>>().setData(pageInfo).ok();
        }
        // 2. 视图过滤参数
        else if (param.containsKey("viewId") && param.get("viewId") != null && !param.get("viewId").toString().isEmpty()) {
            String viewId = param.get("viewId").toString();
            String projectId = param.get("projectId").toString();
            log.info("FeatureController.list - 第二种参数类型: viewId={}, projectId={}", viewId, projectId);
            PageInfo<Feature> pageInfo = featureService.listWithBeanSearcher(viewId, projectId, pageNum, pageSize);
            return new Resp.Builder<PageInfo<Feature>>().setData(pageInfo).ok();
        }
        // 1. 普通列表参数
        else if (param.containsKey("projectId")) {
            FeatureParam featureParam = BeanUtil.toBean(param, FeatureParam.class);
            log.info("FeatureController.list - 第一种参数类型: projectId={}", featureParam.getProjectId());
            PageInfo<Feature> pageInfo = featureService.listWithViewFilter(featureParam, pageNum, pageSize);
            return new Resp.Builder<PageInfo<Feature>>().setData(pageInfo).ok();
        } else {
            return new Resp.Builder<PageInfo<Feature>>().fail();
        }
    }

    @Operation(summary="新增")
    @PostMapping("/save")
    public Resp<?> save(@RequestBody @Validated FeatureSaveDto dto) {
        try {
            Feature feature = this.featureService.add(dto);
            return new Resp.Builder<Feature>().setData(feature).ok();
        } catch (Exception e) {
            log.error("新增故事失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<Feature>().fail();
        }
    }

    @Operation(summary="修改")
    @PutMapping("/update")
    public Resp<Feature> update(@RequestBody @Validated FeatureSaveDto dto) {
        try {
            if (null == dto.getId()) {
                throw new BaseException("id不能为空");
            }
            Feature feature = this.featureService.edit(dto);
            return new Resp.Builder<Feature>().setData(feature).ok();
        } catch (Exception e) {
            log.error("修改故事失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<Feature>().fail();
        }
    }

    @Operation(summary="详情")
    @GetMapping("/info/{id}")
    public Resp<Feature> info(@PathVariable Long id) {
        Feature feature = this.featureService.info(id);
        return new Resp.Builder<Feature>().setData(feature).ok();
    }

    @Operation(summary="删除")
    @DeleteMapping("/delete/{ids}")
    public Resp<?> delete(@PathVariable Long[] ids) {
        try {
            this.featureService.removeByIds(Arrays.asList(ids));
        } catch (Exception e) {
            log.error("删除故事用例失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<Feature>().fail();
        }
        return new Resp.Builder<Feature>().ok();
    }


    @Operation(summary="克隆")
    @PostMapping("/clone")
    public Resp<?> clone(@RequestBody @Validated Long[] ids) {
        try {
            this.featureService.clone(Arrays.asList(ids));
            return new Resp.Builder<>().ok();
        } catch (Exception e) {
            log.error("克隆故事用例失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<>().fail();
        }
    }

    @Operation(summary="模糊查询故事标题")
    @GetMapping("/getFeatureByTitle")
    public Resp<List<Map<String, String>>> getFeatureByTitle(@RequestParam String title, @RequestParam Long projectId) {
        List<Map<String, String>> feature = this.featureService.getFeatureByTitle(title, projectId);
        if (CollectionUtils.isEmpty(feature)) {
            return new Resp.Builder<List<java.util.Map<String, String>>>().buildResult("查无记录", 404);
        }
        return new Resp.Builder<List<java.util.Map<String, String>>>().setData(feature).ok();
    }

}