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

            // 获取基础数据
            int uniqueTestCases = 45;          // 独立测试用例数（设计时的用例数）
            int totalExecutions = 120;         // 总执行次数（包含多环境重复执行）
            int totalCycles = 3;               // 测试周期数
            int plannedTestCases = 135;        // 计划执行的用例数

            // 缺陷数据
            int totalDefectInstances = 18;     // 总缺陷实例数（所有环境发现的缺陷）
            int uniqueDefects = 12;            // 去重后的独立缺陷数
            int environmentSpecificDefects = 6; // 环境特定缺陷数

            // 方式1: 基于执行次数的缺陷密度 (传统方式)
            double executionBasedDensity = (double) totalDefectInstances / totalExecutions * 100;

            // 方式2: 基于独立用例的缺陷密度 (推荐方式)
            double caseBasedDensity = (double) uniqueDefects / uniqueTestCases * 100;

            // 方式3: 加权缺陷密度 (考虑环境因素)
            double weightedDensity = ((double) uniqueDefects + environmentSpecificDefects * 0.5) / uniqueTestCases * 100;

            result.put("defectDensity", Math.round(caseBasedDensity * 100.0) / 100.0);
            result.put("executionBasedDensity", Math.round(executionBasedDensity * 100.0) / 100.0);
            result.put("caseBasedDensity", Math.round(caseBasedDensity * 100.0) / 100.0);
            result.put("weightedDensity", Math.round(weightedDensity * 100.0) / 100.0);

            result.put("totalDefectInstances", totalDefectInstances);
            result.put("uniqueDefects", uniqueDefects);
            result.put("environmentSpecificDefects", environmentSpecificDefects);
            result.put("uniqueTestCases", uniqueTestCases);
            result.put("totalExecutions", totalExecutions);
            result.put("totalCycles", totalCycles);
            result.put("plannedTestCases", plannedTestCases);

            // 质量等级评估
            String level;
            double finalDefectDensity = caseBasedDensity; // 使用推荐的计算方式
            if (finalDefectDensity < 5) {
                level = "优秀";
            } else if (finalDefectDensity < 10) {
                level = "良好";
            } else if (finalDefectDensity < 15) {
                level = "一般";
            } else {
                level = "需改进";
            }
            result.put("level", level);

            // 模块缺陷分布
            List<Map<String, Object>> moduleDefectDistribution = new ArrayList<>();
            moduleDefectDistribution.add(createModuleDefect("用户管理", 5));
            moduleDefectDistribution.add(createModuleDefect("订单处理", 8));
            moduleDefectDistribution.add(createModuleDefect("支付系统", 2));
            moduleDefectDistribution.add(createModuleDefect("数据分析", 3));
            result.put("moduleDefectDistribution", moduleDefectDistribution);

            return new Resp.Builder<Map<String, Object>>().setData(result).ok();
        } catch (Exception e) {
            return new Resp.Builder<Map<String, Object>>().buildResult("获取缺陷密度失败");
        }
    }

    public Resp<Map<String, Object>> getReleasePhaseDefects(String projectId, String releaseVersion) {
        try {
            Map<String, Object> result = new HashMap<>();

            // 发布前后对比分析
            Map<String, Object> phaseStats = new HashMap<>();

            // 发布前缺陷
            int preReleaseDefects = 8;
            // 发布后缺陷  
            int postReleaseDefects = 4;
            // 遗留缺陷（历史版本引入，当前版本发现）
            int legacyDefects = 2;
            int totalDefects = preReleaseDefects + postReleaseDefects;

            phaseStats.put("preReleaseDefects", preReleaseDefects);
            phaseStats.put("postReleaseDefects", postReleaseDefects);
            phaseStats.put("totalDefects", totalDefects);

            // 核心指标计算
            double escapeRate = totalDefects > 0 ? (double) postReleaseDefects / totalDefects * 100 : 0.0;
            double testEffectiveness = totalDefects > 0 ? (double) preReleaseDefects / totalDefects * 100 : 0.0;

            phaseStats.put("escapeRate", Math.round(escapeRate * 100.0) / 100.0);
            phaseStats.put("testEffectiveness", Math.round(testEffectiveness * 100.0) / 100.0);

            // 发布质量等级评定
            String qualityLevel;
            if (escapeRate < 10) {
                qualityLevel = "优秀";
            } else if (escapeRate < 20) {
                qualityLevel = "良好";
            } else if (escapeRate < 30) {
                qualityLevel = "一般";
            } else {
                qualityLevel = "需改进";
            }
            phaseStats.put("qualityLevel", qualityLevel);

            // 发布前后缺陷严重程度对比
            Map<String, Object> severityComparison = new HashMap<>();

            // 发布前缺陷严重程度分布
            List<Map<String, Object>> preReleaseSeverity = new ArrayList<>();
            preReleaseSeverity.add(createSeverityData("致命", 1, "#dc3545"));
            preReleaseSeverity.add(createSeverityData("严重", 2, "#fd7e14"));
            preReleaseSeverity.add(createSeverityData("一般", 3, "#ffc107"));
            preReleaseSeverity.add(createSeverityData("轻微", 2, "#28a745"));

            // 发布后缺陷严重程度分布
            List<Map<String, Object>> postReleaseSeverity = new ArrayList<>();
            postReleaseSeverity.add(createSeverityData("致命", 2, "#dc3545"));
            postReleaseSeverity.add(createSeverityData("严重", 1, "#fd7e14"));
            postReleaseSeverity.add(createSeverityData("一般", 1, "#ffc107"));
            postReleaseSeverity.add(createSeverityData("轻微", 0, "#28a745"));

            severityComparison.put("preRelease", preReleaseSeverity);
            severityComparison.put("postRelease", postReleaseSeverity);

            result.put("phaseStats", phaseStats);
            result.put("severityComparison", severityComparison);

            // 发布后缺陷列表
            List<Map<String, Object>> postReleaseDefectsList = new ArrayList<>();
            // 当前版本新缺陷
            postReleaseDefectsList.add(createDetailedPostReleaseDefectWithTracking(
                "登录页面响应缓慢", "性能", "严重", "2024-01-15", "生产环境用户反馈", 
                "3.0.0.0", "3.0.0.0", 0, 1));
            postReleaseDefectsList.add(createDetailedPostReleaseDefectWithTracking(
                "订单状态更新异常", "功能", "致命", "2024-01-18", "客服部门报告", 
                "3.0.0.0", "3.0.0.0", 0, 1));
            
            // 历史版本遗留缺陷
            postReleaseDefectsList.add(createDetailedPostReleaseDefectWithTracking(
                "移动端适配问题", "兼容性", "一般", "2024-01-20", "用户投诉", 
                "3.0.0.0", "2.0.0", 1, 1));
            postReleaseDefectsList.add(createDetailedPostReleaseDefectWithTracking(
                "数据导出格式错误", "功能", "轻微", "2024-01-22", "业务部门发现", 
                "3.0.0.0", "2.5.0", 1, 1));
            
            // 历史遗留缺陷（测试阶段发现）
            postReleaseDefectsList.add(createDetailedPostReleaseDefectWithTracking(
                "权限验证逻辑缺陷", "安全", "严重", "2024-01-10", "测试团队发现", 
                "3.0.0.0", "2.3.0", 1, 0));

            result.put("postReleaseDefects", postReleaseDefectsList);

            // 缺陷引入版本分析
            Map<String, Object> defectIntroductionAnalysis = new HashMap<>();
            
            // 按引入版本分组统计
            List<Map<String, Object>> introductionVersionStats = new ArrayList<>();
            introductionVersionStats.add(createIntroductionVersionStat("3.0.0.0", 2, "当前版本引入"));
            introductionVersionStats.add(createIntroductionVersionStat("2.5.0", 1, "历史版本遗留"));
            introductionVersionStats.add(createIntroductionVersionStat("2.3.0", 1, "历史版本遗留"));
            introductionVersionStats.add(createIntroductionVersionStat("2.0.0", 1, "早期版本遗留"));
            
            defectIntroductionAnalysis.put("versionStats", introductionVersionStats);
            
            // 遗留缺陷详细分析
            Map<String, Object> legacyDefectAnalysis = new HashMap<>();
            legacyDefectAnalysis.put("count", 3); // 遗留缺陷总数
            legacyDefectAnalysis.put("foundAfterReleaseCount", 2); // 发布后发现的遗留缺陷
            legacyDefectAnalysis.put("foundBeforeReleaseCount", 1); // 发布前发现的遗留缺陷
            legacyDefectAnalysis.put("percentage", 5 > 0 ? Math.round((double) 3 / 5 * 100 * 100.0) / 100.0 : 0);
            legacyDefectAnalysis.put("description", "历史版本遗留缺陷分析");
            
            defectIntroductionAnalysis.put("legacyDefects", legacyDefectAnalysis);
            
            // 发现时机分析
            Map<String, Object> discoveryTimingAnalysis = new HashMap<>();
            discoveryTimingAnalysis.put("postReleaseCount", 4); // 发布后缺陷总数
            discoveryTimingAnalysis.put("preReleaseCount", 1);  // 发布前缺陷总数
            discoveryTimingAnalysis.put("postReleaseNewDefects", 2); // 发布后新缺陷
            discoveryTimingAnalysis.put("postReleaseLegacyDefects", 2); // 发布后遗留缺陷
            discoveryTimingAnalysis.put("description", "缺陷发现时机分析");
            
            defectIntroductionAnalysis.put("discoveryTiming", discoveryTimingAnalysis);
            
            result.put("defectIntroductionAnalysis", defectIntroductionAnalysis);

            // 缺陷来源分析
            List<Map<String, Object>> defectSources = new ArrayList<>();
            defectSources.add(createDefectSource("用户反馈", 6, "#ff6b6b"));
            defectSources.add(createDefectSource("监控告警", 3, "#4ecdc4"));
            defectSources.add(createDefectSource("客服报告", 2, "#45b7d1"));
            defectSources.add(createDefectSource("业务部门", 1, "#96ceb4"));

            result.put("defectSources", defectSources);

            // 发布质量趋势（最近几个版本）
            List<Map<String, Object>> qualityTrend = new ArrayList<>();
            qualityTrend.add(createQualityTrendData("v1.8", 12, 2, 14.3));
            qualityTrend.add(createQualityTrendData("v1.9", 10, 3, 23.1));
            qualityTrend.add(createQualityTrendData("v2.0", 8, 4, 33.3));
            qualityTrend.add(createQualityTrendData("v2.1", 8, 4, 33.3));

            result.put("qualityTrend", qualityTrend);

            // 发布后缺陷时间分布分析
            List<Map<String, Object>> timeDistribution = new ArrayList<>();
            timeDistribution.add(createTimeDistribution("发布后1天", 2, "紧急问题"));
            timeDistribution.add(createTimeDistribution("发布后1周", 1, "用户反馈"));
            timeDistribution.add(createTimeDistribution("发布后1月", 1, "深度使用发现"));

            result.put("timeDistribution", timeDistribution);

            // 测试改进建议
            List<String> improvements = new ArrayList<>();
            if (escapeRate > 30) {
                improvements.add("建议增加端到端测试覆盖");
                improvements.add("加强用户场景测试");
                improvements.add("提升测试环境与生产环境一致性");
            } else if (escapeRate > 20) {
                improvements.add("优化测试用例设计");
                improvements.add("增加边界条件测试");
            } else if (escapeRate > 10) {
                improvements.add("加强回归测试");
            } else {
                improvements.add("测试质量良好，继续保持");
            }
            result.put("improvements", improvements);

            return new Resp.Builder<Map<String, Object>>().setData(result).ok();
        } catch (Exception e) {
            return new Resp.Builder<Map<String, Object>>().buildResult("获取发布阶段缺陷分析失败");
        }
    }

    @Override
    public Resp<Map<String, Object>> getTestCoverage(String projectId, String releaseVersion) {
        try {
            Map<String, Object> result = new HashMap<>();

            // 故事覆盖率数据说明:
            // 故事(User Story) = 功能需求的最小单位，如"用户登录"、"商品搜索"等
            // 总故事数 = 本版本需要实现的所有功能故事
            // 已覆盖故事数 = 有测试用例验证的故事数量

            int totalStories = 25;        // 本版本计划的总故事数
            int coveredStories = 22;      // 已有测试用例覆盖的故事数
            double storyCoverage = (double) coveredStories / totalStories * 100;

            result.put("totalStories", totalStories);
            result.put("coveredStories", coveredStories);
            result.put("uncoveredStories", totalStories - coveredStories);
            result.put("storyCoverage", Math.round(storyCoverage * 100.0) / 100.0);

            // 质量等级
            String level;
            if (storyCoverage >= 95) {
                level = "优秀";
            } else if (storyCoverage >= 85) {
                level = "良好";
            } else if (storyCoverage >= 75) {
                level = "一般";
            } else {
                level = "需改进";
            }
            result.put("level", level);

            // 故事覆盖详情示例
            List<Map<String, Object>> storyCoverageDetails = new ArrayList<>();
            // 已覆盖的故事
            storyCoverageDetails.add(createStoryCoverage("用户注册功能", true, 8));
            storyCoverageDetails.add(createStoryCoverage("用户登录功能", true, 6));
            storyCoverageDetails.add(createStoryCoverage("商品搜索功能", true, 12));
            storyCoverageDetails.add(createStoryCoverage("购物车管理", true, 10));
            storyCoverageDetails.add(createStoryCoverage("订单提交流程", true, 15));

            // 未覆盖的故事
            storyCoverageDetails.add(createStoryCoverage("支付优化功能", false, 0));
            storyCoverageDetails.add(createStoryCoverage("会员积分系统", false, 0));
            storyCoverageDetails.add(createStoryCoverage("数据导出功能", false, 0));
            storyCoverageDetails.add(createStoryCoverage("报表生成功能", true, 8));
            storyCoverageDetails.add(createStoryCoverage("权限管理功能", true, 15));
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
            List<Map<String, Object>> severityDistribution = new ArrayList<>();
            severityDistribution.add(createSeverityData("致命", 2, "#dc3545"));
            severityDistribution.add(createSeverityData("严重", 4, "#fd7e14"));
            severityDistribution.add(createSeverityData("一般", 7, "#ffc107"));
            severityDistribution.add(createSeverityData("轻微", 2, "#28a745"));
            result.put("severityDistribution", severityDistribution);

            // 环境缺陷分布
            List<Map<String, Object>> envDefectDistribution = new ArrayList<>();
            envDefectDistribution.add(createEnvDefect("开发环境", 8, "#007bff"));
            envDefectDistribution.add(createEnvDefect("测试环境", 5, "#6f42c1"));
            envDefectDistribution.add(createEnvDefect("预发布环境", 2, "#20c997"));
            result.put("envDefectDistribution", envDefectDistribution);

            return new Resp.Builder<Map<String, Object>>().setData(result).ok();
        } catch (Exception e) {
            return new Resp.Builder<Map<String, Object>>().buildResult("获取缺陷分布失败");
        }
    }

    @Override
    public Resp<Map<String, Object>> getExecutionRate(String projectId, String releaseVersion) {
        try {
            Map<String, Object> result = new HashMap<>();

            // 测试执行率数据
            int plannedCases = 135;
            int executedCases = 120;
            int passedCases = 105;
            int failedCases = 15;

            double executionRate = (double) executedCases / plannedCases * 100;
            double passRate = (double) passedCases / executedCases * 100;

            result.put("plannedCases", plannedCases);
            result.put("executedCases", executedCases);
            result.put("passedCases", passedCases);
            result.put("failedCases", failedCases);
            result.put("executionRate", Math.round(executionRate * 100.0) / 100.0);
            result.put("passRate", Math.round(passRate * 100.0) / 100.0);

            // 执行率等级
            String executionLevel;
            if (executionRate >= 95) {
                executionLevel = "优秀";
            } else if (executionRate >= 85) {
                executionLevel = "良好";
            } else if (executionRate >= 75) {
                executionLevel = "一般";
            } else {
                executionLevel = "需改进";
            }
            result.put("executionLevel", executionLevel);

            // 通过率等级
            String passLevel;
            if (passRate >= 95) {
                passLevel = "优秀";
            } else if (passRate >= 90) {
                passLevel = "良好";
            } else if (passRate >= 85) {
                passLevel = "一般";
            } else {
                passLevel = "需改进";
            }
            result.put("passLevel", passLevel);

            // 测试执行趋势
            List<Map<String, Object>> executionTrend = new ArrayList<>();
            executionTrend.add(createTrendData("2024-01-15", 75.5, 85.2));
            executionTrend.add(createTrendData("2024-01-16", 82.3, 87.5));
            executionTrend.add(createTrendData("2024-01-17", 88.9, 89.1));
            executionTrend.add(createTrendData("2024-01-18", 91.2, 88.8));
            executionTrend.add(createTrendData("2024-01-19", 88.9, 87.5));
            result.put("executionTrend", executionTrend);

            // 测试周期执行详情
            List<Map<String, Object>> cycleExecutionDetails = new ArrayList<>();
            cycleExecutionDetails.add(createCycleExecution("系统测试周期1", 45, 42, 38, 4, 93.3, 90.5));
            cycleExecutionDetails.add(createCycleExecution("集成测试周期", 30, 28, 25, 3, 93.3, 89.3));
            cycleExecutionDetails.add(createCycleExecution("回归测试周期", 60, 50, 42, 8, 83.3, 84.0));
            result.put("cycleExecutionDetails", cycleExecutionDetails);

            return new Resp.Builder<Map<String, Object>>().setData(result).ok();
        } catch (Exception e) {
            return new Resp.Builder<Map<String, Object>>().buildResult("获取执行率失败");
        }
    }

    @Override
    public Resp<Map<String, Object>> getVersionComparison(String projectId, String startVersion, String endVersion) {
        try {
            Map<String, Object> result = new HashMap<>();

            // 版本对比数据
            List<Map<String, Object>> comparisonData = new ArrayList<>();
            comparisonData.add(createVersionComparison("v2.1.0", 12.5, 82.4, 88.9, 85.2, 18, 144, "良好"));
            comparisonData.add(createVersionComparison("v2.2.0", 8.3, 88.5, 91.2, 92.1, 15, 120, "优秀"));
            result.put("comparisonData", comparisonData);

            return new Resp.Builder<Map<String, Object>>().setData(result).ok();
        } catch (Exception e) {
            return new Resp.Builder<Map<String, Object>>().buildResult("获取版本对比失败");
        }
    }

    @Override
    public Resp<Map<String, Object>> getPostReleaseDefects(String projectId, String releaseVersion) {
        return getReleasePhaseDefects(projectId, releaseVersion);
    }

    // 辅助方法
    private Map<String, Object> createModuleDefect(String module, int count) {
        Map<String, Object> data = new HashMap<>();
        data.put("module", module);
        data.put("count", count);
        return data;
    }

    private Map<String, Object> createStoryCoverage(String story, boolean covered, int testCaseCount) {
        Map<String, Object> data = new HashMap<>();
        data.put("story", story);
        data.put("covered", covered);
        data.put("testCaseCount", testCaseCount);
        return data;
    }

    private Map<String, Object> createSeverityData(String severity, int count, String color) {
        Map<String, Object> data = new HashMap<>();
        data.put("severity", severity);
        data.put("count", count);
        data.put("color", color);
        return data;
    }

    private Map<String, Object> createPostReleaseDefect(String title, String category, String severity, String foundDate, String source) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("category", category);
        data.put("severity", severity);
        data.put("foundDate", foundDate);
        data.put("source", source);
        return data;
    }

    private Map<String, Object> createDetailedPostReleaseDefect(String title, String category, String severity, 
            String foundDate, String source, String foundVersion, String introducedVersion, boolean isLegacy) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("category", category);
        data.put("severity", severity);
        data.put("foundDate", foundDate);
        data.put("source", source);
        data.put("foundVersion", foundVersion);
        data.put("introducedVersion", introducedVersion);
        data.put("isLegacy", isLegacy);
        data.put("legacyDescription", isLegacy ? "遗留缺陷" : "新引入缺陷");
        return data;
    }

    private Map<String, Object> createDetailedPostReleaseDefectWithTracking(String title, String category, String severity, 
            String foundDate, String source, String foundVersion, String introducedVersion, 
            int isLegacy, int foundAfterRelease) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("category", category);
        data.put("severity", severity);
        data.put("foundDate", foundDate);
        data.put("source", source);
        data.put("foundVersion", foundVersion);
        data.put("introducedVersion", introducedVersion);
        data.put("isLegacy", isLegacy);
        data.put("foundAfterRelease", foundAfterRelease);
        
        // 用于前端显示的描述
        String legacyDescription;
        String discoveryDescription;
        
        if (isLegacy == 1) {
            legacyDescription = "遗留缺陷";
        } else {
            legacyDescription = "新引入缺陷";
        }
        
        if (foundAfterRelease == 1) {
            discoveryDescription = "发布后";
        } else {
            discoveryDescription = "发布前";
        }
        
        data.put("legacyDescription", legacyDescription);
        data.put("discoveryDescription", discoveryDescription);
        data.put("detailedDescription", legacyDescription + " - " + discoveryDescription);
        
        return data;
    }

    private Map<String, Object> createIntroductionVersionStat(String version, int count, String description) {
        Map<String, Object> data = new HashMap<>();
        data.put("version", version);
        data.put("count", count);
        data.put("description", description);
        return data;
    }

    private Map<String, Object> createDefectSource(String source, int count, String color) {
        Map<String, Object> data = new HashMap<>();
        data.put("source", source);
        data.put("count", count);
        data.put("color", color);
        return data;
    }

    private Map<String, Object> createQualityTrendData(String version, int preRelease, int postRelease, double escapeRate) {
        Map<String, Object> data = new HashMap<>();
        data.put("version", version);
        data.put("preRelease", preRelease);
        data.put("postRelease", postRelease);
        data.put("escapeRate", escapeRate);
        data.put("totalDefects", preRelease + postRelease);
        return data;
    }

    private Map<String, Object> createTimeDistribution(String timeRange, int count, String description) {
        Map<String, Object> data = new HashMap<>();
        data.put("timeRange", timeRange);
        data.put("count", count);
        data.put("description", description);
        return data;
    }

    private Map<String, Object> createEnvDefect(String environment, int count, String color) {
        Map<String, Object> data = new HashMap<>();
        data.put("environment", environment);
        data.put("count", count);
        data.put("color", color);
        return data;
    }

    private Map<String, Object> createTrendData(String date, double executionRate, double passRate) {
        Map<String, Object> data = new HashMap<>();
        data.put("date", date);
        data.put("executionRate", executionRate);
        data.put("passRate", passRate);
        return data;
    }

    private Map<String, Object> createCycleExecution(String cycleName, int plannedCases, int executedCases,
            int passedCases, int failedCases, double executionRate, double passRate) {
        Map<String, Object> data = new HashMap<>();
        data.put("cycleName", cycleName);
        data.put("plannedCases", plannedCases);
        data.put("executedCases", executedCases);
        data.put("passedCases", passedCases);
        data.put("failedCases", failedCases);
        data.put("executionRate", executionRate);
        data.put("passRate", passRate);
        return data;
    }

    private Map<String, Object> createVersionComparison(String version, double defectDensity,
            double testCoverage, double executionRate, double passRate,
            int totalDefects, int totalTestCases, String qualityLevel) {
        Map<String, Object> data = new HashMap<>();
        data.put("version", version);
        data.put("defectDensity", defectDensity);
        data.put("testCoverage", testCoverage);
        data.put("executionRate", executionRate);
        data.put("passRate", passRate);
        data.put("totalDefects", totalDefects);
        data.put("totalTestCases", totalTestCases);
        data.put("qualityLevel", qualityLevel);
        return data;
    }
}