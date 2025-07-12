
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
            progressDto.setEndDate(project.getPlanReleaseDate());

            // 查询测试用例统计
            TestCase testCaseQuery = new TestCase();
            testCaseQuery.setProjectId(projectId);
            List<TestCase> allTestCases = testCaseDao.queryAll(testCaseQuery);
            int totalTestCases = allTestCases.size();
            int completedTestCases = (int) allTestCases.stream()
                .filter(tc -> tc.getRunStatus() != null && tc.getRunStatus() == 1)
                .count();

            progressDto.setTotalTestCases(totalTestCases);
            progressDto.setCompletedTestCases(completedTestCases);

            // 查询测试周期统计
            TestCycle testCycleQuery = new TestCycle();
            testCycleQuery.setProjectId(projectId);
            List<TestCycle> allTestCycles = testCycleDao.queryAll(testCycleQuery);
            int totalTestCycles = allTestCycles.size();
            int completedTestCycles = (int) allTestCycles.stream()
                .filter(tc -> tc.getRunStatus() != null && tc.getRunStatus() == 1)
                .count();

            progressDto.setTotalTestCycles(totalTestCycles);
            progressDto.setCompletedTestCycles(completedTestCycles);

            // 计算完成度
            double completionRate = totalTestCases > 0 ? 
                (double) completedTestCases / totalTestCases * 100 : 0;
            progressDto.setCompletionRate(Math.round(completionRate * 100.0) / 100.0);

            return new Resp.Builder<ProjectProgressDto>().setData(progressDto).ok();
            
        } catch (Exception e) {
            return new Resp.Builder<ProjectProgressDto>().buildResult("500", "获取项目仪表板数据失败");
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
                gantt.setName(cycle.getTitle());
                gantt.setStartDate(cycle.getCreateTime());
                gantt.setEndDate(cycle.getLastRunDate() != null ? cycle.getLastRunDate() : new Date());
                
                // 计算进度
                double progress = 0;
                if (cycle.getRunStatus() != null) {
                    progress = cycle.getRunStatus() == 1 ? 100 : 50; // 完成=100%, 进行中=50%
                }
                gantt.setProgress(progress);
                
                String status = "未开始";
                if (cycle.getRunStatus() != null) {
                    switch (cycle.getRunStatus()) {
                        case 1: status = "已完成"; break;
                        case 2: status = "进行中"; break;
                        default: status = "未开始"; break;
                    }
                }
                gantt.setStatus(status);
                
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
            List<TestCase> allTestCases = testCaseDao.queryAll(testCaseQuery);
            int totalWork = allTestCases.size();

            List<BurndownChartDto> burndownData = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            
            // 模拟燃尽图数据（实际应该根据历史执行记录计算）
            Calendar cal = Calendar.getInstance();
            if (!StringUtils.isEmpty(startDate)) {
                try {
                    cal.setTime(sdf.parse(startDate));
                } catch (Exception e) {
                    cal.add(Calendar.DAY_OF_MONTH, -30); // 默认30天前
                }
            } else {
                cal.add(Calendar.DAY_OF_MONTH, -30);
            }
            
            Date end = StringUtils.isEmpty(endDate) ? new Date() : sdf.parse(endDate);
            
            int daysDiff = (int) ((end.getTime() - cal.getTimeInMillis()) / (1000 * 60 * 60 * 24));
            
            for (int i = 0; i <= daysDiff; i++) {
                BurndownChartDto burndown = new BurndownChartDto();
                burndown.setDate(cal.getTime());
                
                // 理想工作量线性递减
                int idealWork = totalWork - (totalWork * i / daysDiff);
                burndown.setIdealWork(idealWork);
                
                // 实际剩余工作量（基于当前完成情况）
                int completedWork = (int) allTestCases.stream()
                    .filter(tc -> tc.getRunStatus() != null && tc.getRunStatus() == 1)
                    .count();
                int remainingWork = totalWork - completedWork;
                burndown.setRemainingWork(remainingWork);
                burndown.setCompletedWork(completedWork);
                
                burndownData.add(burndown);
                cal.add(Calendar.DAY_OF_MONTH, 1);
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

            // 按日期统计执行情况（示例数据）
            Map<String, Long> trendData = testCases.stream()
                .filter(tc -> tc.getLastModify() != null)
                .collect(Collectors.groupingBy(
                    tc -> new SimpleDateFormat("yyyy-MM-dd").format(tc.getLastModify()),
                    Collectors.counting()
                ));

            List<String> labels = new ArrayList<>(trendData.keySet());
            Collections.sort(labels);
            List<Object> data = labels.stream()
                .map(trendData::get)
                .collect(Collectors.toList());

            chartData.setLabels(labels);
            chartData.setData(data);

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

            // 统计测试结果
            long passedCount = testCases.stream().filter(tc -> tc.getRunStatus() != null && tc.getRunStatus() == 1).count();
            long failedCount = testCases.stream().filter(tc -> tc.getRunStatus() != null && tc.getRunStatus() == 2).count();
            long notExecutedCount = testCases.stream().filter(tc -> tc.getRunStatus() == null || tc.getRunStatus() == 0).count();

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
}
