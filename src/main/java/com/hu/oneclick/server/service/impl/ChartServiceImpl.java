
package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.*;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.*;
import com.hu.oneclick.model.entity.*;
import com.hu.oneclick.server.service.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChartServiceImpl implements ChartService {

    @Autowired
    private ProjectDao projectDao;
    
    @Autowired
    private TestCycleDao testCycleDao;
    
    @Autowired
    private TestCaseDao testCaseDao;
    
    @Autowired
    private IssueDao issueDao;
    
    @Autowired
    private JwtUserServiceImpl jwtUserService;

    @Override
    public Resp<ProjectProgressDto> getProjectDashboard(String projectId) {
        try {
            if (StringUtils.isEmpty(projectId)) {
                return new Resp.Builder<ProjectProgressDto>().buildResult(
                    SysConstantEnum.PARAM_EMPTY.getCode(), "项目ID不能为空");
            }

            Project project = projectDao.queryById(projectId);
            if (project == null) {
                return new Resp.Builder<ProjectProgressDto>().buildResult(
                    SysConstantEnum.NOUSER_ERROR.getCode(), "项目不存在");
            }

            ProjectProgressDto progressDto = new ProjectProgressDto();
            progressDto.setProjectId(projectId);
            progressDto.setProjectTitle(project.getTitle());
            progressDto.setStatus(project.getStatus());
            progressDto.setStartDate(project.getCreateTime());
            progressDto.setEndDate(project.getUpdateTime());

            // 查询测试用例统计
            TestCase testCaseQuery = new TestCase();
            testCaseQuery.setProjectId(projectId);
            List<TestCase> testCases = testCaseDao.queryAll(testCaseQuery);
            
            long totalTestCases = testCases.size();
            long passedTestCases = testCases.stream().mapToLong(tc -> 
                tc.getExecutionStatus() != null && tc.getExecutionStatus() == 1 ? 1 : 0).sum();
            
            double completionPercentage = totalTestCases > 0 ? 
                (double) passedTestCases / totalTestCases * 100 : 0;

            progressDto.setTotalTestCases((int) totalTestCases);
            progressDto.setPassedTestCases((int) passedTestCases);
            progressDto.setCompletionPercentage(completionPercentage);

            // 查询缺陷统计
            Issue issueQuery = new Issue();
            issueQuery.setProjectId(projectId);
            List<Issue> issues = issueDao.queryAll(issueQuery);
            progressDto.setTotalIssues(issues.size());

            return new Resp.Builder<ProjectProgressDto>().setData(progressDto).ok();
            
        } catch (Exception e) {
            return new Resp.Builder<ProjectProgressDto>().buildResult("500", "获取项目进度失败");
        }
    }

    @Override
    public Resp<List<GanttChartDto>> getGanttChart(String projectId) {
        try {
            if (StringUtils.isEmpty(projectId)) {
                return new Resp.Builder<List<GanttChartDto>>().buildResult(
                    SysConstantEnum.PARAM_EMPTY.getCode(), "项目ID不能为空");
            }

            TestCycle testCycleQuery = new TestCycle();
            testCycleQuery.setProjectId(projectId);
            List<TestCycle> testCycles = testCycleDao.queryAll(testCycleQuery);

            List<GanttChartDto> ganttData = testCycles.stream().map(cycle -> {
                GanttChartDto gantt = new GanttChartDto();
                gantt.setId(cycle.getId());
                gantt.setName(cycle.getName());
                gantt.setStartDate(cycle.getStartDate());
                gantt.setEndDate(cycle.getEndDate());
                gantt.setStatus(cycle.getStatus());
                
                // 计算进度百分比
                double progress = calculateCycleProgress(cycle.getId());
                gantt.setProgress(progress);
                
                return gantt;
            }).collect(Collectors.toList());

            return new Resp.Builder<List<GanttChartDto>>().setData(ganttData).ok();
            
        } catch (Exception e) {
            return new Resp.Builder<List<GanttChartDto>>().buildResult("500", "获取甘特图数据失败");
        }
    }

    @Override
    public Resp<List<BurndownChartDto>> getBurndownChart(String projectId, String startDate, String endDate) {
        try {
            if (StringUtils.isEmpty(projectId)) {
                return new Resp.Builder<List<BurndownChartDto>>().buildResult(
                    SysConstantEnum.PARAM_EMPTY.getCode(), "项目ID不能为空");
            }

            // 查询项目的测试用例总数
            TestCase testCaseQuery = new TestCase();
            testCaseQuery.setProjectId(projectId);
            List<TestCase> testCases = testCaseDao.queryAll(testCaseQuery);
            int totalWork = testCases.size();

            List<BurndownChartDto> burndownData = new ArrayList<>();
            
            // 生成30天的燃尽图数据（示例）
            Calendar cal = Calendar.getInstance();
            for (int i = 30; i >= 0; i--) {
                cal.add(Calendar.DAY_OF_MONTH, -1);
                Date date = cal.getTime();
                
                BurndownChartDto burndown = new BurndownChartDto();
                burndown.setDate(date);
                burndown.setIdealWork(totalWork * i / 30);
                
                // 计算实际剩余工作量
                long completedWork = testCases.stream()
                    .filter(tc -> tc.getUpdateTime() != null && tc.getUpdateTime().before(date))
                    .filter(tc -> tc.getExecutionStatus() != null && tc.getExecutionStatus() == 1)
                    .count();
                burndown.setRemainingWork((int) (totalWork - completedWork));
                burndown.setCompletedWork((int) completedWork);
                
                burndownData.add(burndown);
            }

            return new Resp.Builder<List<BurndownChartDto>>().setData(burndownData).ok();
            
        } catch (Exception e) {
            return new Resp.Builder<List<BurndownChartDto>>().buildResult("500", "获取燃尽图数据失败");
        }
    }

    @Override
    public Resp<ChartDataDto> getTestExecutionTrend(String projectId, String dateRange) {
        try {
            TestCase testCaseQuery = new TestCase();
            testCaseQuery.setProjectId(projectId);
            List<TestCase> testCases = testCaseDao.queryAll(testCaseQuery);

            ChartDataDto chartData = new ChartDataDto();
            chartData.setChartType("line");
            chartData.setTitle("测试执行趋势");
            chartData.setProjectId(projectId);

            // 按日期分组统计测试执行情况
            Map<String, Long> dailyExecution = testCases.stream()
                .filter(tc -> tc.getUpdateTime() != null)
                .collect(Collectors.groupingBy(
                    tc -> new SimpleDateFormat("yyyy-MM-dd").format(tc.getUpdateTime()),
                    Collectors.counting()
                ));

            List<String> dates = new ArrayList<>(dailyExecution.keySet());
            Collections.sort(dates);
            
            chartData.setLabels(dates);
            chartData.setData(dates.stream()
                .map(dailyExecution::get)
                .collect(Collectors.toList()));

            return new Resp.Builder<ChartDataDto>().setData(chartData).ok();
            
        } catch (Exception e) {
            return new Resp.Builder<ChartDataDto>().buildResult("500", "获取测试执行趋势失败");
        }
    }

    @Override
    public Resp<ChartDataDto> getTestResultDistribution(String projectId) {
        try {
            TestCase testCaseQuery = new TestCase();
            testCaseQuery.setProjectId(projectId);
            List<TestCase> testCases = testCaseDao.queryAll(testCaseQuery);

            ChartDataDto chartData = new ChartDataDto();
            chartData.setChartType("pie");
            chartData.setTitle("测试结果分布");
            chartData.setProjectId(projectId);

            long passedCount = testCases.stream()
                .filter(tc -> tc.getExecutionStatus() != null && tc.getExecutionStatus() == 1)
                .count();
            long failedCount = testCases.stream()
                .filter(tc -> tc.getExecutionStatus() != null && tc.getExecutionStatus() == 2)
                .count();
            long notExecutedCount = testCases.stream()
                .filter(tc -> tc.getExecutionStatus() == null || tc.getExecutionStatus() == 0)
                .count();

            chartData.setLabels(Arrays.asList("通过", "失败", "未执行"));
            chartData.setData(Arrays.asList(passedCount, failedCount, notExecutedCount));

            return new Resp.Builder<ChartDataDto>().setData(chartData).ok();
            
        } catch (Exception e) {
            return new Resp.Builder<ChartDataDto>().buildResult("500", "获取测试结果分布失败");
        }
    }

    @Override
    public Resp<ChartDataDto> getDefectStatistics(String projectId) {
        try {
            Issue issueQuery = new Issue();
            issueQuery.setProjectId(projectId);
            List<Issue> issues = issueDao.queryAll(issueQuery);

            ChartDataDto chartData = new ChartDataDto();
            chartData.setChartType("bar");
            chartData.setTitle("缺陷统计");
            chartData.setProjectId(projectId);

            // 按优先级统计缺陷
            Map<String, Long> defectStats = issues.stream()
                .collect(Collectors.groupingBy(
                    issue -> issue.getPriority() != null ? issue.getPriority() : "未设置",
                    Collectors.counting()
                ));

            chartData.setLabels(new ArrayList<>(defectStats.keySet()));
            chartData.setData(new ArrayList<>(defectStats.values()));

            return new Resp.Builder<ChartDataDto>().setData(chartData).ok();
            
        } catch (Exception e) {
            return new Resp.Builder<ChartDataDto>().buildResult("500", "获取缺陷统计失败");
        }
    }

    private double calculateCycleProgress(String cycleId) {
        // 计算测试周期进度的辅助方法
        try {
            // 这里应该根据实际的测试用例执行情况计算进度
            // 暂时返回随机进度用于演示
            return Math.random() * 100;
        } catch (Exception e) {
            return 0.0;
        }
    }
}
