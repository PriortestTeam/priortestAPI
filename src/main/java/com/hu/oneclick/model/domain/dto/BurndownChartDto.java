
package com.hu.oneclick.model.domain.dto;

import lombok.Data;
import java.util.Date;

@Data
public class BurndownChartDto {
    private Date date; // 日期
    private Integer remainingWork; // 剩余工作量
    private Integer idealWork; // 理想工作量
    private Integer completedWork; // 已完成工作量
}
