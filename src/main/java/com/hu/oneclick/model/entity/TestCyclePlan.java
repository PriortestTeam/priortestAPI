package com.hu.oneclick.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hu.oneclick.model.base.AssignBaseEntity;
import com.hu.oneclick.quartz.domain.JobDetails;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "测试周期计划关联表")
@TableName("test_cycle_plan")
public class TestCyclePlan extends AssignBaseEntity implements Serializable {

    private static final long serialVersionUID = -6797066177766223569L;

    /**
     * 测试周期id
     */
    @Schema(description = "测试周期id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long testCycleId;

    /**
     * 测试周期名称
     */
    @Schema(description = "测试周期名称")
    private String testCycleTitle;

    /**
     * qrtz_job_details表job_name的外键
     */
    @Schema(description = "qrtz_job_details表job_name的外键")
    private String jobName;

    /**
     * qrtz_job_details表job_group的外键
     */
    @Schema(description = "qrtz_job_details表job_group的外键")
    private String jobGroup;

    @Schema(description = "计划任务详细")
    @TableField(exist = false)
    private JobDetails jobDetails;

    @Schema(description = "计划任务最近十次运行时间")
    @TableField(exist = false)
    private List<String> runTimeList;
}
