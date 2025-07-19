package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.server.service.*;
import com.hu.oneclick.server.service.VersionQualityReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 版本质量分析报表服务实现
 */
@Service
public class VersionQualityReportServiceImpl implements VersionQualityReportService {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestCycleService testCycleService;

    @Autowired
    private IssueService issueService;

    @Autowired
    private FeatureService featureService;

    @Override
    public Resp<Map<String, Object>> getQualityOverview(String projectId) {
        Map<String, Object> result = new HashMap<>();

        // 这里可以实现项目质量总览逻辑
        result.put("totalVersions", 5);
        result.put("averageDefectDensity", 3.2);
        result.put("averageTestCoverage", 85.5);

        return new Resp.Builder<Map<String, Object>>().setData(result).ok();
    }

    @Override
    public Resp<Map<String, Object>> getDefectDensity(String projectId, String releaseVersion) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 获取版本总缺陷数
            int totalDefects = getTotalDefects(projectId, releaseVersion);

            // 获取版本实际执行的测试用例数
            int executedTestCases = getExecutedTestCases(projectId, releaseVersion);

            // 计算缺陷密度
            double defectDensity = 0.0;
            if (executedTestCases > 0) {
                defectDensity = (double) totalDefects / executedTestCases * 100;
                defectDensity = Math.round(defectDensity * 100.0) / 100.0; // 保留两位小数
            }

            // 获取质量等级
            String qualityLevel = getQualityLevel(defectDensity);

            result.put("defectDensity", defectDensity);
            result.put("totalDefects", totalDefects);
            result.put("executedTestCases", executedTestCases);
            result.put("qualityLevel", qualityLevel);

            // 添加缺陷分布数据
            result.put("severityDistribution", getDefectSeverityDistribution(projectId, releaseVersion));
            result.put("moduleDistribution", getModuleDefectDistribution(projectId, releaseVersion));
            result.put("envDistribution", getEnvDefectDistribution(projectId, releaseVersion));

        } catch (Exception e) {
            e.printStackTrace();
            return new Resp.Builder<Map<String, Object>>().setMsg("获取缺陷密度数据失败：" + e.getMessage()).error();
        }

        return new Resp.Builder<Map<String, Object>>().setData(result).ok();
    }

    @Override
    public Resp<Map<String, Object>> getTestCoverage(String projectId, String releaseVersion) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 获取版本总故事数
            int totalStories = getTotalStories(projectId, releaseVersion);

            // 获取已覆盖故事数
            int coveredStories = getCoveredStories(projectId, releaseVersion);

            // 计算测试覆盖率
            double coverageRate = 0.0;
            if (totalStories > 0) {
                coverageRate = (double) coveredStories / totalStories * 100;
                coverageRate = Math.round(coverageRate * 100.0) / 100.0;
            }

            // 获取质量等级
            String qualityLevel = getCoverageQualityLevel(coverageRate);

            result.put("coverageRate", coverageRate);
            result.put("totalStories", totalStories);
            result.put("coveredStories", coveredStories);
            result.put("qualityLevel", qualityLevel);

            // 添加故事覆盖详情
            result.put("storyCoverageDetails", getStoryCoverageDetails(projectId, releaseVersion));

        } catch (Exception e) {
            e.printStackTrace();
            return new Resp.Builder<Map<String, Object>>().setMsg("获取测试覆盖率数据失败：" + e.getMessage()).error();
        }

        return new Resp.Builder<Map<String, Object>>().setData(result).ok();
    }

    @Override
    public Resp<Map<String, Object>> getDefectDistribution(String projectId, String releaseVersion) {
        Map<String, Object> result = new HashMap<>();

        try {
            result.put("severityDistribution", getDefectSeverityDistribution(projectId, releaseVersion));
            result.put("moduleDistribution", getModuleDefectDistribution(projectId, releaseVersion));
            result.put("envDistribution", getEnvDefectDistribution(projectId, releaseVersion));
            result.put("executionTrend", getTestExecutionTrend(projectId, releaseVersion));
        } catch (Exception e) {
            e.printStackTrace();
            return new Resp.Builder<Map<String, Object>>().setMsg("获取缺陷分布数据失败：" + e.getMessage()).error();
        }

        return new Resp.Builder<Map<String, Object>>().setData(result).ok();
    }

    @Override
    public Resp<Map<String, Object>> getExecutionRate(String projectId, String releaseVersion) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 获取计划执行测试用例数
            int plannedTestCases = getPlannedTestCases(projectId, releaseVersion);

            // 获取实际执行测试用例数
            int executedTestCases = getExecutedTestCases(projectId, releaseVersion);

            // 获取通过测试用例数
            int passedTestCases = getPassedTestCases(projectId, releaseVersion);

            // 计算执行率
            double executionRate = 0.0;
            if (plannedTestCases > 0) {
                executionRate = (double) executedTestCases / plannedTestCases * 100;
                executionRate = Math.round(executionRate * 100.0) / 100.0;
            }

            // 计算通过率
            double passRate = 0.0;
            if (executedTestCases > 0) {
                passRate = (double) passedTestCases / executedTestCases * 100;
                passRate = Math.round(passRate * 100.0) / 100.0;
            }

            result.put("executionRate", executionRate);
            result.put("passRate", passRate);
            result.put("plannedTestCases", plannedTestCases);
            result.put("executedTestCases", executedTestCases);
            result.put("passedTestCases", passedTestCases);
            result.put("executionRateLevel", getExecutionRateQualityLevel(executionRate));
            result.put("passRateLevel", getPassRateQualityLevel(passRate));

            // 添加测试周期执行详情
            result.put("cycleExecutionDetails", getCycleExecutionDetails(projectId, releaseVersion));

        } catch (Exception e) {
            e.printStackTrace();
            return new Resp.Builder<Map<String, Object>>().setMsg("获取测试执行率数据失败：" + e.getMessage()).error();
        }

        return new Resp.Builder<Map<String, Object>>().setData(result).ok();
    }

    @Override
    public Resp<Map<String, Object>> getVersionComparison(String projectId, String startVersion, String endVersion) {
        Map<String, Object> result = new HashMap<>();

        try {
            List<Map<String, Object>> comparisonData = new ArrayList<>();

            // 获取起始版本数据
            if (startVersion != null && !startVersion.isEmpty()) {
                Map<String, Object> startData = generateVersionQualityData(projectId, startVersion);
                comparisonData.add(startData);
            }

            // 获取结束版本数据
            if (endVersion != null && !endVersion.isEmpty()) {
                Map<String, Object> endData = generateVersionQualityData(projectId, endVersion);
                comparisonData.add(endData);
            }

            result.put("comparisonData", comparisonData);

        } catch (Exception e) {
            e.printStackTrace();
            return new Resp.Builder<Map<String, Object>>().setMsg("获取版本对比数据失败：" + e.getMessage()).error();
        }

        return new Resp.Builder<Map<String, Object>>().setData(result).ok();
    }

    // 私有辅助方法
    private int getTotalDefects(String projectId, String releaseVersion) {
        // 模拟数据，实际应从数据库查询
        return 15;
    }

    private int getExecutedTestCases(String projectId, String releaseVersion) {
        // 模拟数据，实际应从数据库查询
        return 250;
    }

    private int getPlannedTestCases(String projectId, String releaseVersion) {
        // 模拟数据，实际应从数据库查询
        return 280;
    }

    private int getPassedTestCases(String projectId, String releaseVersion) {
        // 模拟数据，实际应从数据库查询
        return 235;
    }

    private int getTotalStories(String projectId, String releaseVersion) {
        // 模拟数据，实际应从数据库查询
        return 45;
    }

    private int getCoveredStories(String projectId, String releaseVersion) {
        // 模拟数据，实际应从数据库查询
        return 38;
    }

    private String getQualityLevel(double defectDensity) {
        if (defectDensity <= 2) return "优秀";
        if (defectDensity <= 5) return "良好";
        if (defectDensity <= 10) return "一般";
        return "需改进";
    }

    private String getCoverageQualityLevel(double coverageRate) {
        if (coverageRate >= 90) return "优秀";
        if (coverageRate >= 80) return "良好";
        if (coverageRate >= 70) return "一般";
        return "需改进";
    }

    private String getExecutionRateQualityLevel(double executionRate) {
        if (executionRate >= 95) return "优秀";
        if (executionRate >= 85) return "良好";
        if (executionRate >= 75) return "一般";
        return "需改进";
    }

    private String getPassRateQualityLevel(double passRate) {
        if (passRate >= 95) return "优秀";
        if (passRate >= 90) return "良好";
        if (passRate >= 85) return "一般";
        return "需改进";
    }

    private List<Map<String, Object>> getDefectSeverityDistribution(String projectId, String releaseVersion) {
        List<Map<String, Object>> distribution = new ArrayList<>();
        try {
            // 模拟缺陷严重程度分布数据
            String[] severities = {"致命", "严重", "一般", "轻微"};
            String[] colors = {"#dc3545", "#fd7e14", "#ffc107", "#28a745"};
            int[] counts = {2, 5, 6, 2};

            for (int i = 0; i < severities.length; i++) {
                Map<String, Object> item = new HashMap<>();
                item.put("severity", severities[i]);
                item.put("count", counts[i]);
                item.put("color", colors[i]);
                distribution.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return distribution;
    }

    private List<Map<String, Object>> getModuleDefectDistribution(String projectId, String releaseVersion) {
        List<Map<String, Object>> distribution = new ArrayList<>();
        try {
            // 模拟模块缺陷分布数据
            String[] modules = {"用户管理", "项目管理", "测试管理", "报表模块", "系统设置"};
            int[] counts = {4, 3, 5, 2, 1};

            for (int i = 0; i < modules.length; i++) {
                Map<String, Object> item = new HashMap<>();
                item.put("module", modules[i]);
                item.put("count", counts[i]);
                distribution.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return distribution;
    }

    private List<Map<String, Object>> getEnvDefectDistribution(String projectId, String releaseVersion) {
        List<Map<String, Object>> distribution = new ArrayList<>();
        try {
            // 模拟环境缺陷分布数据
            String[] envs = {"开发环境", "测试环境", "预发布环境"};
            int[] counts = {8, 5, 2};

            for (int i = 0; i<code>This code fixes the compilation errors by using the Resp.Builder to construct the response objects.</code>
<replit_final_file>
package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.server.service.*;
import com.hu.oneclick.server.service.VersionQualityReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 版本质量分析报表服务实现
 */
@Service
public class VersionQualityReportServiceImpl implements VersionQualityReportService {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestCycleService testCycleService;

    @Autowired
    private IssueService issueService;

    @Autowired
    private FeatureService featureService;

    @Override
    public Resp<Map<String, Object>> getQualityOverview(String projectId) {
        Map<String, Object> result = new HashMap<>();

        // 这里可以实现项目质量总览逻辑
        result.put("totalVersions", 5);
        result.put("averageDefectDensity", 3.2);
        result.put("averageTestCoverage", 85.5);

        return new Resp.Builder<Map<String, Object>>().setData(result).ok();
    }

    @Override
    public Resp<Map<String, Object>> getDefectDensity(String projectId, String releaseVersion) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 获取版本总缺陷数
            int totalDefects = getTotalDefects(projectId, releaseVersion);

            // 获取版本实际执行的测试用例数
            int executedTestCases = getExecutedTestCases(projectId, releaseVersion);

            // 计算缺陷密度
            double defectDensity = 0.0;
            if (executedTestCases > 0) {
                defectDensity = (double) totalDefects / executedTestCases * 100;
                defectDensity = Math.round(defectDensity * 100.0) / 100.0; // 保留两位小数
            }

            // 获取质量等级
            String qualityLevel = getQualityLevel(defectDensity);

            result.put("defectDensity", defectDensity);
            result.put("totalDefects", totalDefects);
            result.put("executedTestCases", executedTestCases);
            result.put("qualityLevel", qualityLevel);

            // 添加缺陷分布数据
            result.put("severityDistribution", getDefectSeverityDistribution(projectId, releaseVersion));
            result.put("moduleDistribution", getModuleDefectDistribution(projectId, releaseVersion));
            result.put("envDistribution", getEnvDefectDistribution(projectId, releaseVersion));

        } catch (Exception e) {
            e.printStackTrace();
            return new Resp.Builder<Map<String, Object>>().setMsg("获取缺陷密度数据失败：" + e.getMessage()).error();
        }

        return new Resp.Builder<Map<String, Object>>().setData(result).ok();
    }

    @Override
    public Resp<Map<String, Object>> getTestCoverage(String projectId, String releaseVersion) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 获取版本总故事数
            int totalStories = getTotalStories(projectId, releaseVersion);

            // 获取已覆盖故事数
            int coveredStories = getCoveredStories(projectId, releaseVersion);

            // 计算测试覆盖率
            double coverageRate = 0.0;
            if (totalStories > 0) {
                coverageRate = (double) coveredStories / totalStories * 100;
                coverageRate = Math.round(coverageRate * 100.0) / 100.0;
            }

            // 获取质量等级
            String qualityLevel = getCoverageQualityLevel(coverageRate);

            result.put("coverageRate", coverageRate);
            result.put("totalStories", totalStories);
            result.put("coveredStories", coveredStories);
            result.put("qualityLevel", qualityLevel);

            // 添加故事覆盖详情
            result.put("storyCoverageDetails", getStoryCoverageDetails(projectId, releaseVersion));

        } catch (Exception e) {
            e.printStackTrace();
            return new Resp.Builder<Map<String, Object>>().setMsg("获取测试覆盖率数据失败：" + e.getMessage()).error();
        }

        return new Resp.Builder<Map<String, Object>>().setData(result).ok();
    }

    @Override
    public Resp<Map<String, Object>> getDefectDistribution(String projectId, String releaseVersion) {
        Map<String, Object> result = new HashMap<>();

        try {
            result.put("severityDistribution", getDefectSeverityDistribution(projectId, releaseVersion));
            result.put("moduleDistribution", getModuleDefectDistribution(projectId, releaseVersion));
            result.put("envDistribution", getEnvDefectDistribution(projectId, releaseVersion));
            result.put("executionTrend", getTestExecutionTrend(projectId, releaseVersion));
        } catch (Exception e) {
            e.printStackTrace();
            return new Resp.Builder<Map<String, Object>>().setMsg("获取缺陷分布数据失败：" + e.getMessage()).error();
        }

        return new Resp.Builder<Map<String, Object>>().setData(result).ok();
    }

    @Override
    public Resp<Map<String, Object>> getExecutionRate(String projectId, String releaseVersion) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 获取计划执行测试用例数
            int plannedTestCases = getPlannedTestCases(projectId, releaseVersion);

            // 获取实际执行测试用例数
            int executedTestCases = getExecutedTestCases(projectId, releaseVersion);

            // 获取通过测试用例数
            int passedTestCases = getPassedTestCases(projectId, releaseVersion);

            // 计算执行率
            double executionRate = 0.0;
            if (plannedTestCases > 0) {
                executionRate = (double) executedTestCases / plannedTestCases * 100;
                executionRate = Math.round(executionRate * 100.0) / 100.0;
            }

            // 计算通过率
            double passRate = 0.0;
            if (executedTestCases > 0) {
                passRate = (double) passedTestCases / executedTestCases * 100;
                passRate = Math.round(passRate * 100.0) / 100.0;
            }

            result.put("executionRate", executionRate);
            result.put("passRate", passRate);
            result.put("plannedTestCases", plannedTestCases);
            result.put("executedTestCases", executedTestCases);
            result.put("passedTestCases", passedTestCases);
            result.put("executionRateLevel", getExecutionRateQualityLevel(executionRate));
            result.put("passRateLevel", getPassRateQualityLevel(passRate));

            // 添加测试周期执行详情
            result.put("cycleExecutionDetails", getCycleExecutionDetails(projectId, releaseVersion));

        } catch (Exception e) {
            e.printStackTrace();
            return new Resp.Builder<Map<String, Object>>().setMsg("获取测试执行率数据失败：" + e.getMessage()).error();
        }

        return new Resp.Builder<Map<String, Object>>().setData(result).ok();
    }

    @Override
    public Resp<Map<String, Object>> getVersionComparison(String projectId, String startVersion, String endVersion) {
        Map<String, Object> result = new HashMap<>();

        try {
            List<Map<String, Object>> comparisonData = new ArrayList<>();

            // 获取起始版本数据
            if (startVersion != null && !startVersion.isEmpty()) {
                Map<String, Object> startData = generateVersionQualityData(projectId, startVersion);
                comparisonData.add(startData);
            }

            // 获取结束版本数据
            if (endVersion != null && !endVersion.isEmpty()) {
                Map<String, Object> endData = generateVersionQualityData(projectId, endVersion);
                comparisonData.add(endData);
            }

            result.put("comparisonData", comparisonData);

        } catch (Exception e) {
            e.printStackTrace();
            return new Resp.Builder<Map<String, Object>>().setMsg("获取版本对比数据失败：" + e.getMessage()).error();
        }

        return new Resp.Builder<Map<String, Object>>().setData(result).ok();
    }

    // 私有辅助方法
    private int getTotalDefects(String projectId, String releaseVersion) {
        // 模拟数据，实际应从数据库查询
        return 15;
    }

    private int getExecutedTestCases(String projectId, String releaseVersion) {
        // 模拟数据，实际应从数据库查询
        return 250;
    }

    private int getPlannedTestCases(String projectId, String releaseVersion) {
        // 模拟数据，实际应从数据库查询
        return 280;
    }

    private int getPassedTestCases(String projectId, String releaseVersion) {
        // 模拟数据，实际应从数据库查询
        return 235;
    }

    private int getTotalStories(String projectId, String releaseVersion) {
        // 模拟数据，实际应从数据库查询
        return 45;
    }

    private int getCoveredStories(String projectId, String releaseVersion) {
        // 模拟数据，实际应从数据库查询
        return 38;
    }

    private String getQualityLevel(double defectDensity) {
        if (defectDensity <= 2) return "优秀";
        if (defectDensity <= 5) return "良好";
        if (defectDensity <= 10) return "一般";
        return "需改进";
    }

    private String getCoverageQualityLevel(double coverageRate) {
        if (coverageRate >= 90) return "优秀";
        if (coverageRate >= 80) return "良好";
        if (coverageRate >= 70) return "一般";
        return "需改进";
    }

    private String getExecutionRateQualityLevel(double executionRate) {
        if (executionRate >= 95) return "优秀";
        if (executionRate >= 85) return "良好";
        if (executionRate >= 75) return "一般";
        return "需改进";
    }

    private String getPassRateQualityLevel(double passRate) {
        if (passRate >= 95) return "优秀";
        if (passRate >= 90) return "良好";
        if (passRate >= 85) return "一般";
        return "需改进";
    }

    private List<Map<String, Object>> getDefectSeverityDistribution(String projectId, String releaseVersion) {
        List<Map<String, Object>> distribution = new ArrayList<>();
        try {
            // 模拟缺陷严重程度分布数据
            String[] severities = {"致命", "严重", "一般", "轻微"};
            String[] colors = {"#dc3545", "#fd7e14", "#ffc107", "#28a745"};
            int[] counts = {2, 5, 6, 2};

            for (int i = 0; i < severities.length; i++) {
                Map<String, Object> item = new HashMap<>();
                item.put("severity", severities[i]);
                item.put("count", counts[i]);
                item.put("color", colors[i]);
                distribution.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return distribution;
    }

    private List<Map<String, Object>> getModuleDefectDistribution(String projectId, String releaseVersion) {
        List<Map<String, Object>> distribution = new ArrayList<>();
        try {
            // 模拟模块缺陷分布数据
            String[] modules = {"用户管理", "项目管理", "测试管理", "报表模块", "系统设置"};
            int[] counts = {4, 3, 5, 2, 1};

            for (int i = 0; i < modules.length; i++) {
                Map<String, Object> item = new HashMap<>();
                item.put("module", modules[i]);
                item.put("count", counts[i]);
                distribution.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return distribution;
    }

    private List<Map<String, Object>> getEnvDefectDistribution(String projectId, String releaseVersion) {
        List<Map<String, Object>> distribution = new ArrayList<>();
        try {
            // 模拟环境缺陷分布数据
            String[] envs = {"开发环境", "测试环境", "预发布环境"};
            int[] counts = {8, 5, 2};

            for (int i = 0; i < envs.length; i++) {
                Map<String, Object> item = new HashMap<>();
                item.put("env", envs[i]);
                item.put("count", counts[i]);
                distribution.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return distribution;
    }

    private List<Map<String, Object>> getStoryCoverageDetails(String projectId, String releaseVersion) {
        List<Map<String, Object>> details = new ArrayList<>();
        try {
            // 模拟故事覆盖详情数据
            String[] stories = {"用户登录功能", "项目创建功能", "测试用例管理", "缺陷管理", "报表生成", "权限管理", "数据导入导出"};
            boolean[] covered = {true, true, true, true, true, false, false};
            int[] testCases = {15, 12, 25, 18, 8, 0, 0};

            for (int i = 0; i < stories.length; i++) {
                Map<String, Object> item = new HashMap<>();
                item.put("story", stories[i]);
                item.put("covered", covered[i]);
                item.put("testCaseCount", testCases[i]);
                details.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return details;
    }

    private List<Map<String, Object>> getTestExecutionTrend(String projectId, String releaseVersion) {
        List<Map<String, Object>> trend = new ArrayList<>();
        try {
            // 模拟测试执行趋势数据
            String[] dates = {"2024-01-15", "2024-01-16", "2024-01-17", "2024-01-18", "2024-01-19", "2024-01-20", "2024-01-21"};
            double[] executionRates = {75.0, 80.5, 85.2, 88.7, 92.3, 95.1, 97.8};
            double[] passRates = {92.5, 93.2, 94.1, 93.8, 95.5, 96.2, 97.1};

            for (int i = 0; i < dates.length; i++) {
                Map<String, Object> item = new HashMap<>();
                item.put("date", dates[i]);
                item.put("executionRate", executionRates[i]);
                item.put("passRate", passRates[i]);
                trend.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trend;
    }

    private List<Map<String, Object>> getCycleExecutionDetails(String projectId, String releaseVersion) {
        List<Map<String, Object>> details = new ArrayList<>();
        try {
            // 模拟测试周期执行详情数据
            String[] cycles = {"冒烟测试", "功能测试", "集成测试", "性能测试", "回归测试"};
            int[] planned = {50, 120, 80, 30, 60};
            int[] executed = {48, 115, 75, 28, 58};
            int[] passed = {46, 108, 70, 26, 55};

            for (int i = 0; i < cycles.length; i++) {
                Map<String, Object> item = new HashMap<>();
                item.put("cycleName", cycles[i]);
                item.put("plannedCases", planned[i]);
                item.put("executedCases", executed[i]);
                item.put("passedCases", passed[i]);
                item.put("failedCases", executed[i] - passed[i]);

                double executionRate = (double) executed[i] / planned[i] * 100;
                double passRate = (double) passed[i] / executed[i] * 100;

                item.put("executionRate", Math.round(executionRate * 100.0) / 100.0);
                item.put("passRate", Math.round(passRate * 100.0) / 100.0);

                details.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return details;
    }

    private Map<String, Object> generateVersionQualityData(String projectId, String version) {
        Map<String, Object> data = new HashMap<>();

        // 获取基础数据
        int totalDefects = getTotalDefects(projectId, version);
        int executedTestCases = getExecutedTestCases(projectId, version);
        int totalStories = getTotalStories(projectId, version);
        int coveredStories = getCoveredStories(projectId, version);
        int plannedTestCases = getPlannedTestCases(projectId, version);
        int passedTestCases = getPassedTestCases(projectId, version);

        // 计算指标
        double defectDensity = executedTestCases > 0 ? (double) totalDefects / executedTestCases * 100 : 0;
        double testCoverage = totalStories > 0 ? (double) coveredStories / totalStories * 100 : 0;
        double executionRate = plannedTestCases > 0 ? (double) executedTestCases / plannedTestCases * 100 : 0;
        double passRate = executedTestCases > 0 ? (double) passedTestCases / executedTestCases * 100 : 0;

        // 获取质量等级
        String qualityLevel = getOverallQualityLevel(defectDensity, testCoverage, executionRate, passRate);

        data.put("version", version);
        data.put("defectDensity", Math.round(defectDensity * 100.0) / 100.0);
        data.put("testCoverage", Math.round(testCoverage * 100.0) / 100.0);
        data.put("executionRate", Math.round(executionRate * 100.0) / 100.0);
        data.put("passRate", Math.round(passRate * 100.0) / 100.0);
        data.put("totalDefects", totalDefects);
        data.put("totalTestCases", executedTestCases);
        data.put("qualityLevel", qualityLevel);

        return data;
    }

    private String getOverallQualityLevel(double defectDensity, double testCoverage, double executionRate, double passRate) {
        // 综合评估质量等级
        int score = 0;

        // 缺陷密度评分（权重25%）
        if (defectDensity <= 2) score += 25;
        else if (defectDensity <= 5) score += 20;
        else if (defectDensity <= 10) score += 15;
        else score += 10;

        // 测试覆盖率评分（权重25%）
        if (testCoverage >= 90) score += 25;
        else if (testCoverage >= 80) score += 20;
        else if (testCoverage >= 70) score += 15;
        else score += 10;

        // 执行率评分（权重25%）
        if (executionRate >= 95) score += 25;
        else if (executionRate >= 85) score += 20;
        else if (executionRate >= 75) score += 15;
        else score += 10;

        // 通过率评分（权重25%）
        if (passRate >= 95) score += 25;
        else if (passRate >= 90) score += 20;
        else if (passRate >= 85) score += 15;
        else score += 10;

        // 根据总分确定等级
        if (score >= 90) return "优秀";
        if (score >= 75) return "良好";
        if (score >= 60) return "一般";
        return "需改进";
    }
}