package com.hu.oneclick.controller;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.server.service.FeatureService;
import com.hu.oneclick.server.service.SprintService;
import com.hu.oneclick.server.service.TestCaseService;
import com.hu.oneclick.server.service.TestCycleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("leftJoin")
public class LeftJoinController {

    private final SprintService sprintService;

    private final FeatureService featureService;

    private final TestCaseService testCaseService;

    private final TestCycleService testCycleService;


    public LeftJoinController(SprintService sprintService, FeatureService featureService, TestCaseService testCaseService, TestCycleService testCycleService) {
        this.sprintService = sprintService;
        this.featureService = featureService;
        this.testCaseService = testCaseService;
        this.testCycleService = testCycleService;
    }

    @GetMapping("querySprintTitles")
    public Resp<List<Map<String,String>>> querySprintTitles(@RequestParam String projectId,
                                                            @RequestParam String title){
        return sprintService.queryTitles(projectId,title);
    }

    @GetMapping("queryFeatureTitles")
    public Resp<List<Map<String,String>>> queryFeatureTitles(@RequestParam String projectId,
                                                            @RequestParam String title){
        return featureService.queryTitles(projectId,title);
    }

    @GetMapping("queryFestCaseTitles")
    public Resp<List<Map<String,String>>> queryFestCaseTitles(@RequestParam String projectId,
                                                            @RequestParam String title){
        return testCaseService.queryTitles(projectId,title);
    }

    @GetMapping("queryFestCycleTitles")
    public Resp<List<Map<String,String>>> queryFestCycleTitles(@RequestParam String projectId,
                                                            @RequestParam String title){
        return testCycleService.queryTitles(projectId,title);
    }



}
