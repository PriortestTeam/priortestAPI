
package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;

import java.util.Map;

/**
 * 版本质量分析报表服务接口
 */
public interface VersionQualityReportService {

    /**
     * 获取项目版本质量总览
     */
    Resp<Map<String, Object>> getQualityOverview(String projectId);

    /**
     * 获取版本缺陷密度分析
     */
    Resp<Map<String, Object>> getDefectDensity(String projectId, String releaseVersion);

    /**
     * 获取版本测试覆盖率分析
     */
    Resp<Map<String, Object>> getTestCoverage(String projectId, String releaseVersion);

    /**
     * 获取版本缺陷分布分析
     */
    Resp<Map<String, Object>> getDefectDistribution(String projectId, String releaseVersion);

    /**
     * 获取版本测试执行率分析
     */
    Resp<Map<String, Object>> getExecutionRate(String projectId, String releaseVersion);

    /**
     * 获取版本质量对比分析
     */
    Resp<Map<String, Object>> getVersionComparison(String projectId, String startVersion, String endVersion);
}
