package com.hu.oneclick.quartz.domain;

import lombok.Data;
import java.util.Date;
import java.util.Map;

@Data


public class JobDetails {
    private String cronExpression;
    private String jobClassName;
    private String triggerGroupName;
    private String triggerName;
    private String jobGroupName;
    private String jobName;
    private Date nextFireTime;
    private Date previousFireTime;
    private Date startTime;
    private String timeZone;
    private String status;
    private Map&lt;String, Object> jobDataMap;
}
}
