
package com.hu.oneclick.model.domain.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ChartDataDto {
    private String chartType; // 图表类型：dashboard, gantt, burndown
    private String title; // 图表标题
    private List<String> labels; // X轴标签
    private List<Object> data; // 图表数据
    private Map<String, Object> options; // 图表配置选项
    private String projectId; // 项目ID
    private String dateRange; // 日期范围
}
