package com.hu.oneclick.controller;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.Feature;
import com.hu.oneclick.model.domain.Sprint;
import com.hu.oneclick.model.domain.dto.FeatureDto;
import com.hu.oneclick.server.service.FeatureService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author qingyang
 */
@RestController
@RequestMapping("feature")
@Api(tags = "故事")
public class FeatureController {

    private final FeatureService featureService;

    public FeatureController(FeatureService featureService) {
        this.featureService = featureService;
    }

    @GetMapping("queryById/{id}")
    @ApiOperation("查询故事")
    public Resp<Feature> queryById(@PathVariable String id) {
        return featureService.queryById(id);
    }


    @PostMapping("queryList")
    public Resp<List<Feature>> queryList(@RequestBody FeatureDto feature) {
        return featureService.queryList(feature);
    }

    @PostMapping("insert")
    @ApiOperation("插入故事")
    public Resp<String> insert(@RequestBody FeatureDto feature) {
        return featureService.insert(feature);
    }

    @PostMapping("update")
    public Resp<String> update(@RequestBody Feature feature) {
        return featureService.update(feature);
    }

    @PostMapping("closeUpdate")
    public Resp<String> closeUpdate(@RequestParam String id) {
        return featureService.closeUpdate(id);
    }

    @DeleteMapping("delete/{id}")
    public Resp<String> delete(@PathVariable String id) {
        return featureService.delete(id);
    }


    /**
     * 关联迭代接口
     */
    @PostMapping("querySprintList")
    public Resp<List<Sprint>> querySprintList(@RequestParam(required = false) String title) {
        return featureService.querySprintList(title);
    }
    @PostMapping("queryBindSprints")
    public Resp<List<Sprint>> queryBindSprints(@RequestParam String featureId) {
        return featureService.queryBindSprints(featureId);
    }

//    @PostMapping("bindSprintInsert")
//    public Resp<String> bindSprintInsert(@RequestBody FeatureJoinSprint featureJoinSprint) {
//        return featureService.bindSprintInsert(featureJoinSprint);
//    }
//
//    @PostMapping("bindSprintDelete")
//    public Resp<String> bindSprintDelete(@RequestParam String sprint,@RequestParam String featureId) {
//        return featureService.bindSprintDelete(sprint,featureId);
//    }

}
