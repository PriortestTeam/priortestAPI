package com.hu.oneclick.controller;

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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author qingyang
 */
@Slf4j
@RestController
@RequestMapping("feature")
@Tag(name = "故事", description = "故事相关接口")
public class FeatureController extends BaseController {

    private final FeatureService featureService;

    public FeatureController(FeatureService featureService) {
        this.featureService = featureService;
    }

    @Operation"列表"
    @PostMapping("/list")
    public Resp<PageInfo<Feature>> list(@RequestBody FeatureParam param) {
        if (null == param) {
            param = new FeatureParam();
        }
        startPage();
        List<Feature> dataList = this.featureService.list(param);
        return new Resp.Builder<PageInfo<Feature>>().setData(PageInfo.of(dataList)).ok();
    }

    @Operation"新增"
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

    @Operation"修改"
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

    @Operation"详情"
    @GetMapping("/info/{id}")
    public Resp<Feature> info(@PathVariable Long id) {
        Feature feature = this.featureService.info(id);
        return new Resp.Builder<Feature>().setData(feature).ok();
    }

    @Operation"删除"
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


    @Operation"克隆"
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

    @Operation"模糊查询故事标题"
    @GetMapping("/getFeatureByTitle")
    public Resp<List<Map<String, String>>> getFeatureByTitle(@RequestParam String title, @RequestParam Long projectId) {
        List<Map<String, String>> feature = this.featureService.getFeatureByTitle(title, projectId);
        if (CollectionUtils.isEmpty(feature)) {
            return new Resp.Builder<List<java.util.Map<String, String>>>().buildResult("查无记录", 404);
        }
        return new Resp.Builder<List<java.util.Map<String, String>>>().setData(feature).ok();
    }

}
