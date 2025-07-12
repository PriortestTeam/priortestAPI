
package com.hu.oneclick.model.domain.dto;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class GanttChartDto {
    private String id;
    private String name; // 任务名称（测试周期名称）
    private Date startDate; // 开始日期
    private Date endDate; // 结束日期
    private Double progress; // 进度百分比
    private String status; // 状态
    private List<GanttChartDto> children; // 子任务
}
