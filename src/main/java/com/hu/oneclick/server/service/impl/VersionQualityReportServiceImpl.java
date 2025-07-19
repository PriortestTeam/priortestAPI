
package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.server.service.VersionQualityReportService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VersionQualityReportServiceImpl implements VersionQualityReportService {

    @Override
    public Resp<Map<String, Object>> getQualityOverview(String projectId) {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // 模拟项目质量总览数据
            result.put("totalVersions", 5);
            result.put("averageDefectDensity", 8.5);
            result.put("averageTestCoverage", 85.2);
            result.put("totalDefects", 45);
            result.put("totalTestCases", 530);
            
            return new Resp.Builder<Map<String, Object>>().setData(result).ok();
        } catch (Exception e) {
            return new Resp.Builder<Map<String, Object>>().buildResult("获取质量总览失败");
        }
    }

    @Override
    public Resp<Map<String, Object>> getDefectDensity(String projectId, String releaseVersion) {
        try {
            Map<String, Object> result = new HashMap<>();

            // 缺陷密度计算: (版本总缺陷数 ÷ 版本实际执行测试用例数) × 100
            int totalDefects = 15;
            int executedTestCases = 120;
            int plannedTestCases = 135;
            double defectDensity = (double) totalDefects / executedTestCases * 100;

            result.put("defectDensity", Math.round(defectDensity * 100.0) / 100.0);
            result.put("totalDefects", totalDefects);
            result.put("executedTestCases", executedTestCases);
            result.put("plannedTestCases", plannedTestCases);
            result.put("level", getDefectDensityLevel(defectDensity));

            // 模块缺陷分布
            List<Map<String, Object>> moduleDefects = new ArrayList<>();
            moduleDefects.add(createModuleDefect("用户管理", 4));
            moduleDefects.add(createModuleDefect("项目管理", 3));
            moduleDefects.add(createModuleDefect("测试管理", 5));
            moduleDefects.add(createModuleDefect("报表系统", 3));
            result.put("moduleDefectDistribution", moduleDefects);

            return new Resp.Builder<Map<String, Object>>().setData(result).ok();
        } catch (Exception e) {
            return new Resp.Builder<Map<String, Object>>().buildResult("获取缺陷密度失败");
        }
    }

    @Override
    public Resp<Map<String, Object>> getTestCoverage(String projectId, String releaseVersion) {
        try {
            Map<String, Object> result = new HashMap<>();

            // 测试覆盖率计算: (已覆盖故事数 ÷ 版本总故事数) × 100%
            int totalStories = 25;
            int coveredStories = 22;
            double storyCoverage = (double) coveredStories / totalStories * 100;

            result.put("storyCoverage", storyCoverage);
            result.put("totalStories", totalStories);
            result.put("coveredStories", coveredStories);
            result.put("level", getTestCoverageLevel(storyCoverage));

            // 故事覆盖详情
            List<Map<String, Object>> storyCoverageDetails = new ArrayList<>();
            storyCoverageDetails.add(createStoryCoverage("用户登录功能", 8, true));
            storyCoverageDetails.add(createStoryCoverage("项目创建功能", 6, true));
            storyCoverageDetails.add(createStoryCoverage("测试用例管理", 12, true));
            storyCoverageDetails.add(createStoryCoverage("报表导出功能", 4, false));
            storyCoverageDetails.add(createStoryCoverage("权限管理", 7, false));
            storyCoverageDetails.add(createStoryCoverage("API接口", 9, false));
            result.put("storyCoverageDetails", storyCoverageDetails);

            return new Resp.Builder<Map<String, Object>>().setData(result).ok();
        } catch (Exception e) {
            return new Resp.Builder<Map<String, Object>>().buildResult("获取测试覆盖率失败");
        }
    }

    @Override
    public Resp<Map<String, Object>> getDefectDistribution(String projectId, String releaseVersion) {
        try {
            Map<String, Object> result = new HashMap<>();

            // 缺陷严重程度分布
            List<Map<String, Object>> severityDist = new ArrayList<>();
            severityDist.add(createSeverityDistribution("致命", 2, "#dc3545"));
            severityDist.add(createSeverityDistribution("严重", 5, "#fd7e14"));
            severityDist.add(createSeverityDistribution("一般", 6, "#ffc107"));
            severityDist.add(createSeverityDistribution("轻微", 2, "#28a745"));
            result.put("severityDistribution", severityDist);

            // 环境缺陷分布
            List<Map<String, Object>> envDist = new ArrayList<>();
            envDist.add(createEnvDistribution("开发环境", 8, "#007bff"));
            envDist.add(createEnvDistribution("测试环境", 5, "#6f42c1"));
            envDist.add(createEnvDistribution("预发布环境", 2, "#20c997"));
            result.put("envDefectDistribution", envDist);

            return new Resp.Builder<Map<String, Object>>().setData(result).ok();
        } catch (Exception e) {
            return new Resp.Builder<Map<String, Object>>().buildResult("获取缺陷分布失败");
        }
    }

    @Override
    public Resp<Map<String, Object>> getExecutionRate(String projectId, String releaseVersion) {
        try {
            Map<String, Object> result = new HashMap<>();

            // 测试执行率计算: (实际执行测试用例数 ÷ 计划执行测试用例数) × 100%
            int plannedCases = 135;
            int executedCases = 120;
            int passedCases = 108;
            int failedCases = 12;
            
            double executionRate = (double) executedCases / plannedCases * 100;
            double passRate = (double) passedCases / executedCases * 100;

            result.put("executionRate", executionRate);
            result.put("passRate", passRate);
            result.put("plannedCases", plannedCases);
            result.put("executedCases", executedCases);
            result.put("passedCases", passedCases);
            result.put("failedCases", failedCases);
            result.put("executionLevel", getExecutionRateLevel(executionRate));
            result.put("passLevel", getPassRateLevel(passRate));

            // 测试执行趋势
            List<Map<String, Object>> executionTrend = new ArrayList<>();
            executionTrend.add(createTrendData("2024-01-01", 85.0, 90.0));
            executionTrend.add(createTrendData("2024-01-02", 88.0, 92.0));
            executionTrend.add(createTrendData("2024-01-03", 90.0, 94.0));
            executionTrend.add(createTrendData("2024-01-04", 89.0, 90.0));
            executionTrend.add(createTrendData("2024-01-05", 91.0, 95.0));
            result.put("executionTrend", executionTrend);

            // 测试周期执行详情
            List<Map<String, Object>> cycleDetails = new ArrayList<>();
            cycleDetails.add(createCycleDetail("冒烟测试", 45, 45, 42, 3, 100.0, 93.3));
            cycleDetails.add(createCycleDetail("功能测试", 60, 55, 50, 5, 91.7, 90.9));
            cycleDetails.add(createCycleDetail("集成测试", 30, 20, 16, 4, 66.7, 80.0));
            result.put("cycleExecutionDetails", cycleDetails);

            return new Resp.Builder<Map<String, Object>>().setData(result).ok();
        } catch (Exception e) {
            return new Resp.Builder<Map<String, Object>>().buildResult("获取执行率失败");
        }
    }

    @Override
    public Resp<Map<String, Object>> getVersionComparison(String projectId, String startVersion, String endVersion) {
        try {
            Map<String, Object> result = new HashMap<>();

            List<Map<String, Object>> comparisonData = new ArrayList<>();
            comparisonData.add(createVersionComparison(startVersion, 12.5, 88.0, 89.0, 90.0, 15, 120, "良好"));
            comparisonData.add(createVersionComparison(endVersion, 10.8, 92.0, 91.0, 95.0, 13, 120, "优秀"));
            
            result.put("comparisonData", comparisonData);

            return new Resp.Builder<Map<String, Object>>().setData(result).ok();
        } catch (Exception e) {
            return new Resp.Builder<Map<String, Object>>().buildResult("获取版本对比失败");
        }
    }

    // 辅助方法
    private String getDefectDensityLevel(double density) {
        if (density <= 2) return "优秀";
        if (density <= 5) return "良好";
        if (density <= 10) return "一般";
        return "需改进";
    }

    private String getTestCoverageLevel(double coverage) {
        if (coverage >= 90) return "优秀";
        if (coverage >= 80) return "良好";
        if (coverage >= 70) return "一般";
        return "需改进";
    }

    private String getExecutionRateLevel(double rate) {
        if (rate >= 95) return "优秀";
        if (rate >= 85) return "良好";
        if (rate >= 75) return "一般";
        return "需改进";
    }

    private String getPassRateLevel(double rate) {
        if (rate >= 95) return "优秀";
        if (rate >= 90) return "良好";
        if (rate >= 85) return "一般";
        return "需改进";
    }

    private Map<String, Object> createModuleDefect(String module, int count) {
        Map<String, Object> item = new HashMap<>();
        item.put("module", module);
        item.put("count", count);
        return item;
    }

    private Map<String, Object> createStoryCoverage(String story, int testCaseCount, boolean covered) {
        Map<String, Object> item = new HashMap<>();
        item.put("story", story);
        item.put("testCaseCount", testCaseCount);
        item.put("covered", covered);
        return item;
    }

    private Map<String, Object> createSeverityDistribution(String severity, int count, String color) {
        Map<String, Object> item = new HashMap<>();
        item.put("severity", severity);
        item.put("count", count);
        item.put("color", color);
        return item;
    }

    private Map<String, Object> createEnvDistribution(String environment, int count, String color) {
        Map<String, Object> item = new HashMap<>();
        item.put("environment", environment);
        item.put("count", count);
        item.put("color", color);
        return item;
    }

    private Map<String, Object> createTrendData(String date, double executionRate, double passRate) {
        Map<String, Object> item = new HashMap<>();
        item.put("date", date);
        item.put("executionRate", executionRate);
        item.put("passRate", passRate);
        return item;
    }

    private Map<String, Object> createCycleDetail(String cycleName, int plannedCases, int executedCases, 
                                                 int passedCases, int failedCases, double executionRate, double passRate) {
        Map<String, Object> item = new HashMap<>();
        item.put("cycleName", cycleName);
        item.put("plannedCases", plannedCases);
        item.put("executedCases", executedCases);
        item.put("passedCases", passedCases);
        item.put("failedCases", failedCases);
        item.put("executionRate", executionRate);
        item.put("passRate", passRate);
        return item;
    }

    private Map<String, Object> createVersionComparison(String version, double defectDensity, double testCoverage,
                                                       double executionRate, double passRate, int totalDefects, 
                                                       int totalTestCases, String qualityLevel) {
        Map<String, Object> item = new HashMap<>();
        item.put("version", version);
        item.put("defectDensity", defectDensity);
        item.put("testCoverage", testCoverage);
        item.put("executionRate", executionRate);
        item.put("passRate", passRate);
        item.put("totalDefects", totalDefects);
        item.put("totalTestCases", totalTestCases);
        item.put("qualityLevel", qualityLevel);
        return item;
    }
}
