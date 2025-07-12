
package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.*;
import java.util.List;

public interface ChartService {
    
    /**
     * 获取项目进度仪表板数据
     */
    Resp<ProjectProgressDto> getProjectDashboard(String projectId);
    
    /**
     * 获取甘特图数据
     */
    Resp<List<GanttChartDto>> getGanttChart(String projectId);
    
    /**
     * 获取燃尽图数据
     */
    Resp<List<BurndownChartDto>> getBurndownChart(String projectId, String startDate, String endDate);
    
    /**
     * 获取测试执行趋势图
     */
    Resp<ChartDataDto> getTestExecutionTrend(String projectId, String dateRange);
    
    /**
     * 获取测试结果分布图
     */
    Resp<ChartDataDto> getTestResultDistribution(String projectId);
    
    /**
     * 获取缺陷统计图表
     */
    Resp<ChartDataDto> getDefectStatistics(String projectId);
}
