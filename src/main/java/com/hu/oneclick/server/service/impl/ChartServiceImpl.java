
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
            
            // 获取测试用例统计 - 使用正确的查询方法
            QueryWrapper<TestCase> testCaseWrapper = new QueryWrapper<>();
            testCaseWrapper.eq("project_id", Long.valueOf(projectId));
            List<TestCase> testCases = testCaseDao.selectList(testCaseWrapper);
            
            int totalTestCases = testCases.size();
            int passedTestCases = 0;
            
            // 统计已通过的测试用例 - 使用 lastRunStatus 字段
            for (TestCase tc : testCases) {
                Integer status = tc.getLastRunStatus();
                if (status != null && status == 1) { // 假设 1 表示通过
                    passedTestCases++;
                }
            }
            
            progressDto.setTotalTestCases(totalTestCases);
            progressDto.setPassedTestCases(passedTestCases);
            progressDto.setCompletionPercentage(
                totalTestCases > 0 ? (double) passedTestCases / totalTestCases * 100 : 0.0
            );
            
            // 获取缺陷统计 - 使用正确的查询方法
            QueryWrapper<Issue> issueWrapper = new QueryWrapper<>();
            issueWrapper.eq("project_id", Long.valueOf(projectId));
            List<Issue> issues = issueDao.selectList(issueWrapper);
            
            int totalIssues = issues.size();
            int openIssues = 0;
            int resolvedIssues = 0;
            
            // 统计缺陷状态
            for (Issue issue : issues) {
                Integer status = issue.getIssueStatus();
                if (status != null) {
                    if (status == 1) { // 假设 1 表示打开状态
                        openIssues++;
                    } else if (status == 2) { // 假设 2 表示已解决
                        resolvedIssues++;
                    }
                }
            }
            
            progressDto.setTotalIssues(totalIssues);
            progressDto.setOpenIssues(openIssues);
            progressDto.setResolvedIssues(resolvedIssues);
            
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
                dto.setStatus("进行中"); // 默认状态
                
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
            
            // 获取指定时间范围内的测试用例数据
            QueryWrapper<TestCase> wrapper = new QueryWrapper<>();
            wrapper.eq("project_id", Long.valueOf(projectId));
            List<TestCase> testCases = testCaseDao.selectList(wrapper);
            
            // 模拟燃尽图数据生成
            LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusDays(30);
            LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
            
            int totalWork = testCases.size();
            long totalDays = ChronoUnit.DAYS.between(start, end);
            
            for (int i = 0; i <= totalDays; i++) {
                BurndownChartDto dto = new BurndownChartDto();
                LocalDate currentDate = start.plusDays(i);
                dto.setDate(currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
                
                // 理想燃尽线
                dto.setIdealRemaining(totalWork - (int)((double)totalWork * i / totalDays));
                
                // 实际燃尽线 - 基于测试用例完成情况
                int actualCompleted = 0;
                for (TestCase tc : testCases) {
                    if (tc.getUpdateTime() != null && 
                        tc.getUpdateTime().toLocalDate().isBefore(currentDate.plusDays(1)) &&
                        tc.getLastRunStatus() != null && tc.getLastRunStatus() == 1) {
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
            
            // 生成日期标签
            int days = Integer.parseInt(dateRange);
            List<String> labels = new ArrayList<>();
            List<Integer> passedData = new ArrayList<>();
            List<Integer> failedData = new ArrayList<>();
            
            for (int i = days - 1; i >= 0; i--) {
                LocalDate date = LocalDate.now().minusDays(i);
                labels.add(date.format(DateTimeFormatter.of("MM-dd")));
                
                // 统计当日通过和失败的测试用例
                int passed = 0;
                int failed = 0;
                for (TestCase tc : testCases) {
                    if (tc.getUpdateTime() != null && 
                        tc.getUpdateTime().toLocalDate().equals(date)) {
                        if (tc.getLastRunStatus() != null && tc.getLastRunStatus() == 1) {
                            passed++;
                        } else if (tc.getLastRunStatus() != null && tc.getLastRunStatus() == 2) {
                            failed++;
                        }
                    }
                }
                passedData.add(passed);
                failedData.add(failed);
            }
            
            chartData.setLabels(labels);
            
            Map<String, List<Integer>> datasets = new HashMap<>();
            datasets.put("通过", passedData);
            datasets.put("失败", failedData);
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
            Map<String, Long> statusCount = new HashMap<>();
            statusCount.put("通过", 0L);
            statusCount.put("失败", 0L);
            statusCount.put("未执行", 0L);
            
            for (TestCase tc : testCases) {
                Integer status = tc.getLastRunStatus();
                if (status == null) {
                    statusCount.put("未执行", statusCount.get("未执行") + 1);
                } else if (status == 1) {
                    statusCount.put("通过", statusCount.get("通过") + 1);
                } else if (status == 2) {
                    statusCount.put("失败", statusCount.get("失败") + 1);
                }
            }
            
            List<String> labels = new ArrayList<>(statusCount.keySet());
            List<Integer> data = statusCount.values().stream()
                .map(Long::intValue)
                .collect(Collectors.toList());
            
            chartData.setLabels(labels);
            
            Map<String, List<Integer>> datasets = new HashMap<>();
            datasets.put("测试结果", data);
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
            
            // 按优先级统计缺陷
            Map<String, Long> priorityCount = issues.stream()
                .collect(Collectors.groupingBy(
                    issue -> issue.getPriority() != null ? issue.getPriority() : "未设置",
                    Collectors.counting()
                ));
            
            List<String> labels = new ArrayList<>(priorityCount.keySet());
            List<Integer> data = priorityCount.values().stream()
                .map(Long::intValue)
                .collect(Collectors.toList());
            
            chartData.setLabels(labels);
            
            Map<String, List<Integer>> datasets = new HashMap<>();
            datasets.put("缺陷数量", data);
            chartData.setDatasets(datasets);
            
            return new Resp.Builder<ChartDataDto>()
                .buildSuccessResult(chartData);
                
        } catch (Exception e) {
            return new Resp.Builder<ChartDataDto>()
                .buildResult("500", "获取缺陷统计数据失败: " + e.getMessage());
        }
    }

    private double calculateCycleProgress(String cycleId) {
        try {
            // 根据测试周期ID计算进度
            // 这里可以根据实际业务逻辑实现
            return 50.0; // 默认返回50%
        } catch (Exception e) {
            return 0.0;
        }
    }
}
