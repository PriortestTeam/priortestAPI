package com.hu.oneclick.model.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * test_cycle_schedule
 * @author 
 */
@ApiModel(value="com.hu.oneclick.model.entity.TestCycleSchedule")
@Data
public class TestCycleSchedule implements Serializable {
    private Integer id;

    /**
     * 运行方式id
     */
    @ApiModelProperty(value="运行方式id")
    private Integer scheduleModelId;

    /**
     * 执行时间
     */
    @ApiModelProperty(value="执行时间")
    private Date runTime;

    /**
     * 执行状态0未执行1执行成功2执行失败
     */
    @ApiModelProperty(value="执行状态0未执行1执行成功2执行失败")
    private String runStatus;

    /**
     * test_cycle_id
     */
    @ApiModelProperty(value="test_cycle_id")
    private Integer testCycleId;

    private static final long serialVersionUID = 1L;
}
