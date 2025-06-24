package com.hu.oneclick.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * test_cycle_schedule
 * @author 
 */
@Schemavalue="com.hu.oneclick.model.entity.TestCycleSchedule"
@Data
public class TestCycleSchedule implements Serializable {
    private Integer id;

    /**
     * 运行方式id
     */
    @Schemavalue="运行方式id"
    private Integer scheduleModelId;

    /**
     * 执行时间
     */
    @Schemavalue="执行时间"
    private Date runTime;

    /**
     * 执行状态0未执行1执行成功2执行失败
     */
    @Schemavalue="执行状态0未执行1执行成功2执行失败"
    private String runStatus;

    /**
     * test_cycle_id
     */
    @Schemavalue="test_cycle_id"
    private Integer testCycleId;

    private static final long serialVersionUID = 1L;
}
