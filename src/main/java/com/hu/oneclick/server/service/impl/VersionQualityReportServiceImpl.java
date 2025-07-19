package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.server.service.*;
import com.hu.oneclick.server.service.VersionQualityReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

        try {
            // 获取项目基本信息
            Map<String, Object> projectInfo = getProjectBasicInfo(projectId);
            result.put("projectInfo", projectInfo);

            // 获取所有版本的质量概览
            List<Map<String, Object>> versionOverviews = getAllVersionsOverview(projectId);
            result.put("versionOverviews", versionOverviews);

            // 计算质量趋势
            Map<String, Object> qualityTrend = calculateQualityTrend(versionOverviews);
            result.put("qualityTrend", qualityTrend);

            return new Resp.Builder<Map<String, Object>>().setData(result).ok();
        } catch (Exception e) {
            e.printStackTrace();
            return new Resp.Builder<Map<String, Object>>().fail();
        }
    }

    @Override
    public Resp<Map<String, Object>> getDefectDensity(String projectId, String releaseVersion) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 获取版本相关的总缺陷数
            int totalDefects = getTotalDefectsByVersion(projectId, releaseVersion);

            // 获取版本实际执行的测试用例数
            int executedTestCases = getExecutedTestCasesByVersion(projectId, releaseVersion);

            // 计算缺陷密度：(总缺陷数 / 实际执行测试用例数) × 100
            double defectDensity = 0.0;
            if (executedTestCases > 0) {
                defectDensity = (double) totalDefects / executedTestCases * 100;
                defectDensity = new BigDecimal(defectDensity).setScale(2, RoundingMode.HALF_UP).doubleValue();
            }

            // 评估缺陷密度等级
            String level = evaluateDefectDensityLevel(defectDensity);

            result.put("totalDefects", totalDefects);
            result.put("executedTestCases", executedTestCases);
            result.put("defectDensity", defectDensity);
            result.put("level", level);
            result.put("unit", "缺陷/100用例");

            // 获取缺陷严重程度分布
            List<Map<String, Object>> severityDistribution = getDefectSeverityDistribution(projectId, releaseVersion);
            result.put("severityDistribution", severityDistribution);

            // 获取模块缺陷分布
            List<Map<String, Object>> moduleDefectDistribution = getModuleDefectDistribution(projectId, releaseVersion);
            result.put("moduleDefectDistribution", moduleDefectDistribution);

            return new Resp.Builder<Map<String, Object>>().setData(result).ok();
        } catch (Exception e) {
            e.printStackTrace();
            return new Resp.Builder<Map<String, Object>>().fail();
        }
    }

    @Override
    public Resp<Map<String, Object>> getTestCoverage(String projectId, String releaseVersion) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 获取版本总故事数
            int totalStories = getTotalStoriesByVersion(projectId, releaseVersion);

            // 获取已覆盖故事数
            int coveredStories = getCoveredStoriesByVersion(projectId, releaseVersion);

            // 计算测试覆盖率：(已覆盖故事数 / 版本总故事数) × 100%
            double storyCoverage = 0.0;
            if (totalStories > 0) {
                storyCoverage = (double) coveredStories / totalStories * 100;
                storyCoverage = new BigDecimal(storyCoverage).setScale(2, RoundingMode.HALF_UP).doubleValue();
            }

            // 评估覆盖率等级
            String level = evaluateCoverageLevel(storyCoverage);

            result.put("totalStories", totalStories);
            result.put("coveredStories", coveredStories);
            result.put("storyCoverage", storyCoverage);
            result.put("level", level);
            result.put("unit", "%");

            // 获取故事覆盖详情
            List<Map<String, Object>> storyCoverageDetails = getStoryCoverageDetails(projectId, releaseVersion);
            result.put("storyCoverageDetails", storyCoverageDetails);

            return new Resp.Builder<Map<String, Object>>().setData(result).ok();
        } catch (Exception e) {
            e.printStackTrace();
            return new Resp.Builder<Map<String, Object>>().fail();
        }
    }

    @Override
    public Resp<Map<String, Object>> getDefectDistribution(String projectId, String releaseVersion) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 获取缺陷严重程度分布
            List<Map<String, Object>> severityDistribution = getDefectSeverityDistribution(projectId, releaseVersion);
            result.put("severityDistribution", severityDistribution);

            // 获取环境缺陷分布
            List<Map<String, Object>> envDefectDistribution = getEnvironmentDefectDistribution(projectId, releaseVersion);
            result.put("envDefectDistribution", envDefectDistribution);

            // 获取缺陷状态分布
            List<Map<String, Object>> statusDistribution = getDefectStatusDistribution(projectId, releaseVersion);
            result.put("statusDistribution", statusDistribution);

            return new Resp.Builder<Map<String, Object>>().setData(result).ok();
        } catch (Exception e) {
            e.printStackTrace();
            return new Resp.Builder<Map<String, Object>>().fail();
        }
    }

    @Override
    public Resp<Map<String, Object>> getExecutionRate(String projectId, String releaseVersion) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 获取计划执行测试用例数
            int plannedTestCases = getPlannedTestCasesByVersion(projectId, releaseVersion);

            // 获取实际执行测试用例数
            int executedTestCases = getExecutedTestCasesByVersion(projectId, releaseVersion);

            // 获取通过测试用例数
            int passedTestCases = getPassedTestCasesByVersion(projectId, releaseVersion);

            // 计算测试执行率：(实际执行测试用例数 / 计划执行测试用例数) × 100%
            double executionRate = 0.0;
            if (plannedTestCases > 0) {
                executionRate = (double) executedTestCases / plannedTestCases * 100;
                executionRate = new BigDecimal(executionRate).setScale(2, RoundingMode.HALF_UP).doubleValue();
            }

            // 计算测试通过率：(通过测试用例数 / 实际执行测试用例数) × 100%
            double passRate = 0.0;
            if (executedTestCases > 0) {
                passRate = (double) passedTestCases / executedTestCases * 100;
                passRate = new BigDecimal(passRate).setScale(2, RoundingMode.HALF_UP).doubleValue();
            }

            // 评估等级
            String executionLevel = evaluateExecutionRateLevel(executionRate);
            String passLevel = evaluatePassRateLevel(passRate);

            result.put("plannedTestCases", plannedTestCases);
            result.put("executedTestCases", executedTestCases);
            result.put("passedTestCases", passedTestCases);
            result.put("executionRate", executionRate);
            result.put("passRate", passRate);
            result.put("executionLevel", executionLevel);
            result.put("passLevel", passLevel);

            // 获取测试执行趋势
            List<Map<String, Object>> executionTrend = getTestExecutionTrend(projectId, releaseVersion);
            result.put("executionTrend", executionTrend);

            // 获取测试周期执行详情
            List<Map<String, Object>> cycleExecutionDetails = getCycleExecutionDetails(projectId, releaseVersion);
            result.put("cycleExecutionDetails", cycleExecutionDetails);

            return new Resp.Builder<Map<String, Object>>().setData(result).ok();
        } catch (Exception e) {
            e.printStackTrace();
            return new Resp.Builder<Map<String, Object>>().fail();
        }
    }

    @Override
    public Resp<Map<String, Object>> getVersionComparison(String projectId, String startVersion, String endVersion) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 获取版本列表（如果没指定范围，则获取所有版本）
            List<String> versions = getVersionsForComparison(projectId, startVersion, endVersion);

            List<Map<String, Object>> comparisonData = new ArrayList<>();

            for (String version : versions) {
                Map<String, Object> versionData = new HashMap<>();
                versionData.put("version", version);

                // 获取各个质量指标
                Map<String, Object> defectDensityResp = getDefectDensity(projectId, version).getData();
                Map<String, Object> testCoverageResp = getTestCoverage(projectId, version).getData();
                Map<String, Object> executionRateResp = getExecutionRate(projectId, version).getData();

                versionData.put("defectDensity", defectDensityResp.get("defectDensity"));
                versionData.put("testCoverage", testCoverageResp.get("storyCoverage"));
                versionData.put("executionRate", executionRateResp.get("executionRate"));
                versionData.put("passRate", executionRateResp.get("passRate"));
                versionData.put("totalDefects", defectDensityResp.get("totalDefects"));
                versionData.put("totalTestCases", defectDensityResp.get("executedTestCases"));

                // 计算综合质量等级
                String qualityLevel = calculateOverallQualityLevel(versionData);
                versionData.put("qualityLevel", qualityLevel);

                comparisonData.add(versionData);
            }

            result.put("comparisonData", comparisonData);

            // 计算质量改进趋势
            Map<String, Object> improvementTrend = calculateImprovementTrend(comparisonData);
            result.put("improvementTrend", improvementTrend);

            return new Resp.Builder<Map<String, Object>>().setData(result).ok();
        } catch (Exception e) {
            e.printStackTrace();
            return new Resp.Builder<Map<String, Object>>().fail();
        }
    }

    // 私有辅助方法
    private Map<String, Object> getProjectBasicInfo(String projectId) {
        Map<String, Object> projectInfo = new HashMap<>();
        try {
            // 这里应该调用 ProjectService 获取项目基本信息
            projectInfo.put("projectId", projectId);
            projectInfo.put("projectName", "示例项目");
            projectInfo.put("totalVersions", 5);
            projectInfo.put("latestVersion", "v2.0.0");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return projectInfo;
    }

    private List<Map<String, Object>> getAllVersionsOverview(String projectId) {
        List<Map<String, Object>> versions = new ArrayList<>();
        try {
            // 模拟数据，实际应该从数据库获取
            String[] versionNames = {"v1.0.0", "v1.1.0", "v1.2.0", "v2.0.0"};
            for (String version : versionNames) {
                Map<String, Object> versionInfo = new HashMap<>();
                versionInfo.put("version", version);
                versionInfo.put("defectDensity", Math.random() * 10);
                versionInfo.put("testCoverage", 80 + Math.random() * 20);
                versionInfo.put("executionRate", 85 + Math.random() * 15);
                versions.add(versionInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versions;
    }

    private Map<String, Object> calculateQualityTrend(List<Map<String, Object>> versionOverviews) {
        Map<String, Object> trend = new HashMap<>();
        try {
            // 计算质量趋势，这里简化处理
            trend.put("improving", true);
            trend.put("trendDescription", "整体质量呈上升趋势");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trend;
    }

    private int getTotalDefectsByVersion(String projectId, String releaseVersion) {
        try {
            // 实际应该调用 IssueService 查询该版本的缺陷数量
            // 这里返回模拟数据
            return (int) (Math.random() * 50) + 10;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private int getExecutedTestCasesByVersion(String projectId, String releaseVersion) {
        try {
            // 实际应该查询测试周期中实际执行的测试用例数量
            // 这里返回模拟数据
            return (int) (Math.random() * 500) + 200;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private int getPlannedTestCasesByVersion(String projectId, String releaseVersion) {
        try {
            // 实际应该查询计划执行的测试用例数量
            // 这里返回模拟数据
            return (int) (Math.random() * 100) + 300;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private int getPassedTestCasesByVersion(String projectId, String releaseVersion) {
        try {
            // 实际应该查询通过的测试用例数量
            // 这里返回模拟数据
            int executed = getExecutedTestCasesByVersion(projectId, releaseVersion);
            return (int) (executed * (0.85 + Math.random() * 0.1)); // 85%-95%通过率
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private int getTotalStoriesByVersion(String projectId, String releaseVersion) {
        try {
            // 实际应该调用 FeatureService 查询该版本的故事数量
            // 这里返回模拟数据
            return (int) (Math.random() * 50) + 20;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private int getCoveredStoriesByVersion(String projectId, String releaseVersion) {
        try {
            // 实际应该查询被测试用例覆盖的故事数量
            // 这里返回模拟数据
            int total = getTotalStoriesByVersion(projectId, releaseVersion);
            return (int) (total * (0.7 + Math.random() * 0.25)); // 70%-95%覆盖率
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private List<Map<String, Object>> getDefectSeverityDistribution(String projectId, String releaseVersion) {
        List<Map<String, Object>> distribution = new ArrayList<>();
        try {
            // 模拟缺陷严重程度分布数据
            String[] severities = {"严重", "重要", "一般", "轻微"};
            int[] counts = {5, 15, 25, 10};
            String[] colors = {"#dc3545", "#fd7e14", "#ffc107", "#6c757d"};

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
            String[] modules = {"用户管理", "权限管理", "数据分析", "系统配置"};
            int[] counts = {12, 8, 15, 10};

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

    private List<Map<String, Object>> getEnvironmentDefectDistribution(String projectId, String releaseVersion) {
        List<Map<String, Object>> distribution = new ArrayList<>();
        try {
            // 模拟环境缺陷分布数据
            String[] environments = {"开发环境", "测试环境", "预发布环境"};
            int[] counts = {20, 25, 10};
            String[] colors = {"#28a745", "#ffc107", "#17a2b8"};

            for (int i = 0; i < environments.length; i++) {
                Map<String, Object> item = new HashMap<>();
                item.put("environment", environments[i]);
                item.put("count", counts[i]);
                item.put("color", colors[i]);
                distribution.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return distribution;
    }

    private List<Map<String, Object>> getDefectStatusDistribution(String projectId, String releaseVersion) {
        List<Map<String, Object>> distribution = new ArrayList<>();
        try {
            // 模拟缺陷状态分布数据
            String[] statuses = {"已修复", "待修复", "已关闭", "重新打开"};
            int[] counts = {35, 12, 5, 3};

            for (int i = 0; i < statuses.length; i++) {
                Map<String, Object> item = new HashMap<>();
                item.put("status", statuses[i]);
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
            String[] stories = {"用户注册功能", "用户登录功能", "密码重置功能", "用户资料编辑", "权限分配功能"};
            boolean[] covered = {true, true, true, false, true};
            int[] testCases = {5, 3, 4, 0, 6};

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
            String[] cycles = {"功能测试周期", "集成测试周期", "回归测试周期", "验收测试周期"};
            int[] planned = {150, 200, 180, 100};
            int[] executed = {145, 195, 175, 95};
            int[] passed = {138, 185, 170, 92};

            for (int i = 0; i < cycles.length; i++) {
                Map<String, Object> item = new HashMap<>();
                item.put("cycleName", cycles[i]);
                item.put("plannedCases", planned[i]);
                item.put("executedCases", executed[i]);
                item.put("passedCases", passed[i]);
                item.put("failedCases", executed[i] - passed[i]);

                double executionRate = (double) executed[i] / planned[i] * 100;
                double passRate = (double) passed[i] / executed[i] * 100;

                item.put("executionRate", new BigDecimal(executionRate).setScale(1, RoundingMode.HALF_UP).doubleValue());
                item.put("passRate", new BigDecimal(passRate).setScale(1, RoundingMode.HALF_UP).doubleValue());

                details.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return details;
    }

    private List<String> getVersionsForComparison(String projectId, String startVersion, String endVersion) {
        List<String> versions = new ArrayList<>();
        try {
            // 这里应该从数据库获取版本列表
            // 模拟数据
            versions.add("v1.0.0");
            versions.add("v1.1.0");
            versions.add("v1.2.0");
            versions.add("v2.0.0");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versions;
    }

    private String calculateOverallQualityLevel(Map<String, Object> versionData) {
        try {
            double defectDensity = (Double) versionData.get("defectDensity");
            double testCoverage = (Double) versionData.get("testCoverage");
            double executionRate = (Double) versionData.get("executionRate");
            double passRate = (Double) versionData.get("passRate");

            // 简单的综合评级算法
            int score = 0;
            if (defectDensity <= 2) score += 25;
            else if (defectDensity <= 5) score += 20;
            else if (defectDensity <= 10) score += 15;
            else score += 10;

            if (testCoverage >= 90) score += 25;
            else if (testCoverage >= 80) score += 20;
            else if (testCoverage >= 70) score += 15;
            else score += 10;

            if (executionRate >= 95) score += 25;
            else if (executionRate >= 85) score += 20;
            else if (executionRate >= 75) score += 15;
            else score += 10;

            if (passRate >= 95) score += 25;
            else if (passRate >= 90) score += 20;
            else if (passRate >= 85) score += 15;
            else score += 10;

            if (score >= 90) return "优秀";
            else if (score >= 75) return "良好";
            else if (score >= 60) return "一般";
            else return "需改进";

        } catch (Exception e) {
            e.printStackTrace();
            return "未知";
        }
    }

    private Map<String, Object> calculateImprovementTrend(List<Map<String, Object>> comparisonData) {
        Map<String, Object> trend = new HashMap<>();
        try {
            // 简化的趋势计算
            trend.put("improving", true);
            trend.put("trendDescription", "版本质量持续改进");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trend;
    }

    // 评级方法
    private String evaluateDefectDensityLevel(double density) {
        if (density <= 2) return "优秀";
        if (density <= 5) return "良好";
        if (density <= 10) return "一般";
        return "需改进";
    }

    private String evaluateCoverageLevel(double coverage) {
        if (coverage >= 90) return "优秀";
        if (coverage >= 80) return "良好";
        if (coverage >= 70) return "一般";
        return "需改进";
    }

    private String evaluateExecutionRateLevel(double rate) {
        if (rate >= 95) return "优秀";
        if (rate >= 85) return "良好";
        if (rate >= 75) return "一般";
        return "需改进";
    }

    private String evaluatePassRateLevel(double rate) {
        if (rate >= 95) return "优秀";
        if (rate >= 90) return "良好";
        if (rate >= 85) return "一般";
        return "需改进";
    }
}