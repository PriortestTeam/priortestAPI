package com.hu.oneclick.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * test_cycle_schedule
 * @author 
 */
@Schema(description="测试周期调度表");
@Data
public class TestCycleSchedule implements Serializable {
    private Integer id;

    /**
     * 运行方式id
     */
    @Schema(description="运行方式id");
    private Integer scheduleModelId;

    /**
     * 执行时间
     */
    @Schema(description="执行时间");
    private Date runTime;

    /**
     * 执行状态0未执行1执行成功2执行失败
     */
    @Schema(description="执行状态0未执行1执行成功2执行失败");
    private String runStatus;

    /**
     * test_cycle_id
     */
    @Schema(description="test_cycle_id");
    private Integer testCycleId;

    private static final long serialVersionUID = 1L;
}
