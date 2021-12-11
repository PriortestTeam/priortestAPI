package com.hu.oneclick.model.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * test_cycle_schedule_model
 * @author 
 */
@ApiModel(value="com.hu.oneclick.model.domain.TestCycleScheduleModel")
@Data
public class TestCycleScheduleModel implements Serializable {
    private Integer id;

    /**
     * 测试周期id
     */
    @ApiModelProperty(value="测试周期id")
    private Integer testCycleId;

    /**
     * 开始时间天
     */
    @ApiModelProperty(value="开始时间天")
    private Date startTimeDate;

    /**
     * 开始时间时分秒
     */
    @ApiModelProperty(value="开始时间时分秒")
    private Date runTime;

    /**
     * 执行路径
     */
    @ApiModelProperty(value="执行路径")
    private String jenkinsUrl;

    /**
     * 结束时间
     */
    @ApiModelProperty(value="结束时间")
    private Date endTime;

    /**
     * 重复方式
     */
    @ApiModelProperty(value="重复方式")
    private String frequency;

    private static final long serialVersionUID = 1L;
}