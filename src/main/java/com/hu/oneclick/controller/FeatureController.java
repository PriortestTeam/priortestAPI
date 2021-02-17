package com.hu.oneclick.controller;

import com.hu.oneclick.model.annotation.Page;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.Feature;
import com.hu.oneclick.server.service.FeatureService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author qingyang
 */
@RestController
@RequestMapping("feature")
public class FeatureController {

    private final FeatureService featureService;

    public FeatureController(FeatureService featureService) {
        this.featureService = featureService;
    }

    @GetMapping("queryById/{id}")
    public Resp<Feature> queryById(@PathVariable String id) {
        return featureService.queryById(id);
    }

    @Page
    @PostMapping("queryList")
    public Resp<List<Feature>> queryList(@RequestBody Feature feature) {
        return featureService.queryList(feature);
    }

    @PostMapping("insert")
    public Resp<String> insert(@RequestBody Feature feature) {
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


}
