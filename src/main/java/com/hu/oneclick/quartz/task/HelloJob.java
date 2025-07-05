package com.hu.oneclick.quartz.task;
import com.hu.oneclick.quartz.JenkinsManager;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import jakarta.annotation.Resource;
import java.util.Date;
@Slf4j


public class HelloJob extends QuartzJobBean {
    @Resource
    private JenkinsManager jenkinsManager;
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        // get parameters
        context.getJobDetail().getJobDataMap().forEach(
                (k, v) -> log.info("param, key:{}, value:{}", k, v)
        );
        // your logics
        log.info("Hello Job执行时间: " + new Date();
//        jenkinsManager.buildJob("priortestapi");
        log.info("成功");
    }
}
}
}
