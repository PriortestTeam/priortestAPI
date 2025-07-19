
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
            double defectDensity = (double) totalDefects / executedTestCases * 100;

            result.put("defectDensity", Math.round(defectDensity * 100.0) / 100.0);
            result.put("totalDefects", totalDefects);
            result.put("executedTestCases", executedTestCases);
            result.put("level", getDefectDensityLevel(defectDensity));

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
            double testCoverage = (double) coveredStories / totalStories * 100;

            result.put("testCoverage", Math.round(testCoverage * 100.0) / 100.0);
            result.put("totalStories", totalStories);
            result.put("coveredStories", coveredStories);
            result.put("level", getTestCoverageLevel(testCoverage));

            return new Resp.Builder<Map<String, Object>>().setData(result).ok();
        } catch (Exception e) {
            return new Resp.Builder<Map<String, Object>>().buildResult("获取测试覆盖率失败");
        }
    }

    @Override
    public Resp<Map<String, Object>> getDefectDistribution(String projectId, String releaseVersion) {
        try {
            Map<String, Object> result = new HashMap<>();

            // 模拟缺陷严重程度分布数据
            List<String> labels = List.of("致命", "严重", "一般", "轻微");
            List<Integer> data = List.of(2, 5, 6, 2);
            List<String> colors = List.of("#dc3545", "#fd7e14", "#ffc107", "#28a745");

            result.put("labels", labels);
            result.put("data", data);
            result.put("colors", colors);

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
            int executedTestCases = 120;
            int plannedTestCases = 135;
            double executionRate = (double) executedTestCases / plannedTestCases * 100;

            // 测试通过率计算: (通过测试用例数 ÷ 实际执行测试用例数) × 100%
            int passedTestCases = 108;
            double passRate = (double) passedTestCases / executedTestCases * 100;

            result.put("executionRate", Math.round(executionRate * 100.0) / 100.0);
            result.put("passRate", Math.round(passRate * 100.0) / 100.0);
            result.put("executedTestCases", executedTestCases);
            result.put("plannedTestCases", plannedTestCases);
            result.put("passedTestCases", passedTestCases);
            result.put("executionLevel", getExecutionRateLevel(executionRate));
            result.put("passLevel", getPassRateLevel(passRate));

            return new Resp.Builder<Map<String, Object>>().setData(result).ok();
        } catch (Exception e) {
            return new Resp.Builder<Map<String, Object>>().buildResult("获取测试执行率失败");
        }
    }

    @Override
    public Resp<Map<String, Object>> getVersionComparison(String projectId, String startVersion, String endVersion) {
        try {
            Map<String, Object> result = new HashMap<>();

            // 模拟版本对比数据
            List<Map<String, Object>> versions = new ArrayList<>();
            
            Map<String, Object> version1 = new HashMap<>();
            version1.put("version", startVersion != null ? startVersion : "v2.0.0");
            version1.put("defectDensity", 12.5);
            version1.put("testCoverage", 88.0);
            version1.put("executionRate", 89.0);
            version1.put("passRate", 90.0);
            version1.put("totalDefects", 15);
            version1.put("totalTestCases", 120);
            version1.put("qualityLevel", "良好");
            versions.add(version1);

            Map<String, Object> version2 = new HashMap<>();
            version2.put("version", endVersion != null ? endVersion : "v2.1.0");
            version2.put("defectDensity", 8.3);
            version2.put("testCoverage", 92.0);
            version2.put("executionRate", 95.0);
            version2.put("passRate", 94.0);
            version2.put("totalDefects", 10);
            version2.put("totalTestCases", 135);
            version2.put("qualityLevel", "优秀");
            versions.add(version2);

            result.put("versions", versions);

            return new Resp.Builder<Map<String, Object>>().setData(result).ok();
        } catch (Exception e) {
            return new Resp.Builder<Map<String, Object>>().buildResult("获取版本对比失败");
        }
    }

    // 辅助方法 - 获取缺陷密度等级
    private String getDefectDensityLevel(double defectDensity) {
        if (defectDensity <= 2) return "优秀";
        else if (defectDensity <= 5) return "良好";
        else if (defectDensity <= 10) return "一般";
        else return "需改进";
    }

    // 辅助方法 - 获取测试覆盖率等级
    private String getTestCoverageLevel(double testCoverage) {
        if (testCoverage >= 90) return "优秀";
        else if (testCoverage >= 80) return "良好";
        else if (testCoverage >= 70) return "一般";
        else return "需改进";
    }

    // 辅助方法 - 获取执行率等级
    private String getExecutionRateLevel(double executionRate) {
        if (executionRate >= 95) return "优秀";
        else if (executionRate >= 85) return "良好";
        else if (executionRate >= 75) return "一般";
        else return "需改进";
    }

    // 辅助方法 - 获取通过率等级
    private String getPassRateLevel(double passRate) {
        if (passRate >= 95) return "优秀";
        else if (passRate >= 90) return "良好";
        else if (passRate >= 85) return "一般";
        else return "需改进";
    }
}
