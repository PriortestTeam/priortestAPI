
package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.dao.IssueDao;
import com.hu.oneclick.dao.ProjectDao;
import com.hu.oneclick.dao.TestCaseDao;
import com.hu.oneclick.dao.TestCycleDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.BurndownChartDto;
import com.hu.oneclick.model.domain.dto.ChartDataDto;
import com.hu.oneclick.model.domain.dto.GanttChartDto;
import com.hu.oneclick.model.domain.dto.ProjectProgressDto;
import com.hu.oneclick.model.entity.Issue;
import com.hu.oneclick.model.entity.Project;
import com.hu.oneclick.model.entity.TestCase;
import com.hu.oneclick.model.entity.TestCycle;
import com.hu.oneclick.server.service.ChartService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChartServiceImpl implements ChartService {

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private TestCaseDao testCaseDao;

    @Autowired
    private TestCycleDao testCycleDao;

    @Autowired
    private IssueDao issueDao;

    @Override
    public Resp<ProjectProgressDto> getProjectDashboard(String projectId) {
        try {
            ProjectProgressDto progressDto = new ProjectProgressDto();
            
            // 获取项目基本信息
            Project project = projectDao.selectById(Long.valueOf(projectId));
            if (project == null) {
                return new Resp.Builder<ProjectProgressDto>()
                    .buildResult("404", "项目不存在");
            }
            
            progressDto.setProjectId(projectId);
            progressDto.setProjectName(project.getTitle());
            
            // 获取测试用例统计
            QueryWrapper<TestCase> testCaseWrapper = new QueryWrapper<>();
            testCaseWrapper.eq("project_id", Long.valueOf(projectId));
            List<TestCase> testCases = testCaseDao.selectList(testCaseWrapper);
            
            int totalTestCases = testCases.size();
            int passedTestCases = 0;
            
            for (TestCase tc : testCases) {
                // 假设状态字段存在，根据实际字段名调整
                String status = tc.getStatus();
                if ("PASSED".equals(status) || "通过".equals(status)) {
                    passedTestCases++;
                }
            }
            
            progressDto.setTotalTestCases(totalTestCases);
            progressDto.setPassedTestCases(passedTestCases);
            progressDto.setCompletionPercentage(
                totalTestCases > 0 ? (double) passedTestCases / totalTestCases * 100 : 0.0
            );
            
            // 获取缺陷统计
            QueryWrapper<Issue> issueWrapper = new QueryWrapper<>();
            issueWrapper.eq("project_id", Long.valueOf(projectId));
            List<Issue> issues = issueDao.selectList(issueWrapper);
            progressDto.setTotalIssues(issues.size());
            
            return new Resp.Builder<ProjectProgressDto>()
                .buildSuccessResult(progressDto);
                
        } catch (Exception e) {
            return new Resp.Builder<ProjectProgressDto>()
                .buildResult("500", "获取项目仪表板数据失败: " + e.getMessage());
        }
    }

    @Override
    public Resp<List<GanttChartDto>> getGanttChart(String projectId) {
        try {
            List<GanttChartDto> ganttData = new ArrayList<>();
            
            // 获取测试周期作为甘特图数据
            QueryWrapper<TestCycle> wrapper = new QueryWrapper<>();
            wrapper.eq("project_id", Long.valueOf(projectId));
            List<TestCycle> cycles = testCycleDao.selectList(wrapper);
            
            for (TestCycle cycle : cycles) {
                GanttChartDto dto = new GanttChartDto();
                dto.setId(cycle.getId().toString());
                dto.setName(cycle.getTitle() != null ? cycle.getTitle() : "测试周期");
                dto.setStartDate(cycle.getCreateTime());
                dto.setEndDate(cycle.getUpdateTime());
                dto.setStatus(cycle.getStatus() != null ? cycle.getStatus() : "进行中");
                
                // 计算进度
                dto.setProgress(calculateCycleProgress(cycle.getId().toString()));
                
                ganttData.add(dto);
            }
            
            return new Resp.Builder<List<GanttChartDto>>()
                .buildSuccessResult(ganttData);
                
        } catch (Exception e) {
            return new Resp.Builder<List<GanttChartDto>>()
                .buildResult("500", "获取甘特图数据失败: " + e.getMessage());
        }
    }

    @Override
    public Resp<List<BurndownChartDto>> getBurndownChart(String projectId, String startDate, String endDate) {
        try {
            List<BurndownChartDto> burndownData = new ArrayList<>();
            
            // 获取项目的测试用例
            QueryWrapper<TestCase> wrapper = new QueryWrapper<>();
            wrapper.eq("project_id", Long.valueOf(projectId));
            List<TestCase> testCases = testCaseDao.selectList(wrapper);
            
            int totalWork = testCases.size();
            
            // 生成日期范围
            LocalDate start = startDate != null ? 
                LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE) : 
                LocalDate.now().minusDays(30);
            LocalDate end = endDate != null ? 
                LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE) : 
                LocalDate.now();
            
            long daysBetween = ChronoUnit.DAYS.between(start, end);
            
            for (int i = 0; i <= daysBetween; i++) {
                LocalDate currentDate = start.plusDays(i);
                BurndownChartDto dto = new BurndownChartDto();
                dto.setDate(currentDate.toString());
                
                // 计算理想剩余工作量
                double idealRemaining = totalWork - (totalWork * i / (double) daysBetween);
                dto.setIdealRemaining((int) idealRemaining);
                
                // 计算实际剩余工作量（这里使用模拟数据）
                int actualCompleted = 0;
                for (TestCase tc : testCases) {
                    String status = tc.getStatus();
                    if ("PASSED".equals(status) || "通过".equals(status)) {
                        actualCompleted++;
                    }
                }
                dto.setActualRemaining(totalWork - actualCompleted);
                
                burndownData.add(dto);
            }
            
            return new Resp.Builder<List<BurndownChartDto>>()
                .buildSuccessResult(burndownData);
                
        } catch (Exception e) {
            return new Resp.Builder<List<BurndownChartDto>>()
                .buildResult("500", "获取燃尽图数据失败: " + e.getMessage());
        }
    }

    @Override
    public Resp<ChartDataDto> getTestExecutionTrend(String projectId, String dateRange) {
        try {
            ChartDataDto chartData = new ChartDataDto();
            
            // 获取测试用例
            QueryWrapper<TestCase> wrapper = new QueryWrapper<>();
            wrapper.eq("project_id", Long.valueOf(projectId));
            List<TestCase> testCases = testCaseDao.selectList(wrapper);
            
            // 生成趋势数据
            List<String> labels = new ArrayList<>();
            List<Integer> passedData = new ArrayList<>();
            List<Integer> failedData = new ArrayList<>();
            List<Integer> skippedData = new ArrayList<>();
            
            int days = Integer.parseInt(dateRange);
            for (int i = days; i >= 0; i--) {
                LocalDate date = LocalDate.now().minusDays(i);
                labels.add(date.format(DateTimeFormatter.ofPattern("MM-dd")));
                
                // 模拟数据 - 实际应该从数据库查询
                int passed = 0, failed = 0, skipped = 0;
                for (TestCase tc : testCases) {
                    String status = tc.getStatus();
                    if ("PASSED".equals(status) || "通过".equals(status)) {
                        passed++;
                    } else if ("FAILED".equals(status) || "失败".equals(status)) {
                        failed++;
                    } else {
                        skipped++;
                    }
                }
                
                passedData.add(passed);
                failedData.add(failed);
                skippedData.add(skipped);
            }
            
            chartData.setLabels(labels);
            
            Map<String, List<Integer>> datasets = new HashMap<>();
            datasets.put("passed", passedData);
            datasets.put("failed", failedData);
            datasets.put("skipped", skippedData);
            chartData.setDatasets(datasets);
            
            return new Resp.Builder<ChartDataDto>()
                .buildSuccessResult(chartData);
                
        } catch (Exception e) {
            return new Resp.Builder<ChartDataDto>()
                .buildResult("500", "获取测试执行趋势数据失败: " + e.getMessage());
        }
    }

    @Override
    public Resp<ChartDataDto> getTestResultDistribution(String projectId) {
        try {
            ChartDataDto chartData = new ChartDataDto();
            
            // 获取测试用例
            QueryWrapper<TestCase> wrapper = new QueryWrapper<>();
            wrapper.eq("project_id", Long.valueOf(projectId));
            List<TestCase> testCases = testCaseDao.selectList(wrapper);
            
            // 统计各状态的数量
            Map<String, Long> statusCount = testCases.stream()
                .collect(Collectors.groupingBy(
                    tc -> tc.getStatus() != null ? tc.getStatus() : "未执行",
                    Collectors.counting()
                ));
            
            List<String> labels = new ArrayList<>(statusCount.keySet());
            List<Integer> data = statusCount.values().stream()
                .map(Long::intValue)
                .collect(Collectors.toList());
            
            chartData.setLabels(labels);
            
            Map<String, List<Integer>> datasets = new HashMap<>();
            datasets.put("distribution", data);
            chartData.setDatasets(datasets);
            
            return new Resp.Builder<ChartDataDto>()
                .buildSuccessResult(chartData);
                
        } catch (Exception e) {
            return new Resp.Builder<ChartDataDto>()
                .buildResult("500", "获取测试结果分布数据失败: " + e.getMessage());
        }
    }

    @Override
    public Resp<ChartDataDto> getDefectStatistics(String projectId) {
        try {
            ChartDataDto chartData = new ChartDataDto();
            
            // 获取缺陷数据
            QueryWrapper<Issue> wrapper = new QueryWrapper<>();
            wrapper.eq("project_id", Long.valueOf(projectId));
            List<Issue> issues = issueDao.selectList(wrapper);
            
            // 按优先级统计
            Map<String, Long> priorityCount = issues.stream()
                .collect(Collectors.groupingBy(
                    issue -> issue.getPriority() != null ? issue.getPriority() : "普通",
                    Collectors.counting()
                ));
            
            List<String> labels = new ArrayList<>(priorityCount.keySet());
            List<Integer> data = priorityCount.values().stream()
                .map(Long::intValue)
                .collect(Collectors.toList());
            
            chartData.setLabels(labels);
            
            Map<String, List<Integer>> datasets = new HashMap<>();
            datasets.put("defects", data);
            chartData.setDatasets(datasets);
            
            return new Resp.Builder<ChartDataDto>()
                .buildSuccessResult(chartData);
                
        } catch (Exception e) {
            return new Resp.Builder<ChartDataDto>()
                .buildResult("500", "获取缺陷统计数据失败: " + e.getMessage());
        }
    }

    private int calculateCycleProgress(String cycleId) {
        try {
            // 根据测试周期ID计算进度
            QueryWrapper<TestCase> wrapper = new QueryWrapper<>();
            wrapper.eq("cycle_id", cycleId);
            List<TestCase> testCases = testCaseDao.selectList(wrapper);
            
            if (testCases.isEmpty()) {
                return 0;
            }
            
            long completedCount = testCases.stream()
                .filter(tc -> "PASSED".equals(tc.getStatus()) || "FAILED".equals(tc.getStatus()))
                .count();
                
            return (int) (completedCount * 100 / testCases.size());
        } catch (Exception e) {
            return 0;
        }
    }
}
