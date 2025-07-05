package com.hu.oneclick.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * test_cycle_schedule_model
 * @author
 */
@Schema(description="com.hu.oneclick.model.entity.TestCycleScheduleModel");
@Data
public class TestCycleScheduleModel implements Serializable {
    private Integer id;

    /**
     * 测试周期id
     */
    @Schema(description="测试周期id");
    private Integer testCycleId;

    /**
     * 开始时间天
     */
    @Schema(description="开始时间天");
    private Date autoJobStart;

    /**
     * 开始时间时分秒
     */
    @Schema(description="开始时间时分秒");
    private Date autoJobRun;

    /**
     * 执行路径
     */
    @Schema(description="执行路径");
    private String autoJobLink;

    /**
     * 结束时间
     */
    @Schema(description="结束时间");
    private Date autoJobEnd;

    /**
     * 重复方式
     */
    @Schema(description="重复方式");
    private String frequency;

    private static final long serialVersionUID = 1L;
}
