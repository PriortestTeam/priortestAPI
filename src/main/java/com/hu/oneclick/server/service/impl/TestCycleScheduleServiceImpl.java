package com.hu.oneclick.server.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.hu.oneclick.common.constant.JenkinsRunConstant;
import com.hu.oneclick.dao.TestCycleScheduleDao;
import com.hu.oneclick.dao.TestCycleScheduleModelDao;
import com.hu.oneclick.model.domain.TestCycleSchedule;
import com.hu.oneclick.model.domain.TestCycleScheduleModel;
import com.hu.oneclick.server.service.TestCycleScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.List;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/12/8
 * @since JDK 1.8.0
 */
@Service
public class TestCycleScheduleServiceImpl implements TestCycleScheduleService {

    @Autowired
    private TestCycleScheduleDao testCycleScheduleDao;
    @Autowired
    private TestCycleScheduleModelDao testCycleScheduleModelDao;

//    @Scheduled(fixedDelay = 1000 * 60)
    public void jenkinsSchedule() {
        List<TestCycleSchedule> testCycleSchedules = testCycleScheduleDao.selectAllByRuntime(new Date());
        for (TestCycleSchedule testCycleSchedule : testCycleSchedules) {
            //当前时间
            Date date = new Date();
            Date runTime = testCycleSchedule.getRunTime();
            long between = DateUtil.between(date, runTime, DateUnit.MS);

            if (between <= 1000 * 60L) {
                Integer scheduleModelId = testCycleSchedule.getScheduleModelId();
                TestCycleScheduleModel testCycleScheduleModel = testCycleScheduleModelDao.selectByPrimaryKey(scheduleModelId);
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(testCycleScheduleModel.getJenkinsUrl())).build();
                HttpResponse<String> response = null;
                try {
                    response = client.send(request, HttpResponse.BodyHandlers.ofString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.statusCode() == 200) {
                    testCycleSchedule.setRunStatus(JenkinsRunConstant.RUNSUCCESS);
                    testCycleScheduleDao.updateByPrimaryKeySelective(testCycleSchedule);
                }

            }
        }
    }
}
