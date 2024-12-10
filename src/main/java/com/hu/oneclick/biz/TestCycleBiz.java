package com.hu.oneclick.biz;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hu.oneclick.dao.TestCycleDao;
import com.hu.oneclick.dao.TestCycleJoinTestCaseDao;
import com.hu.oneclick.dao.TestCycleTcDao;
import com.hu.oneclick.model.domain.dto.TestCycleJoinTestCaseSaveDto;
import com.hu.oneclick.model.entity.TestCasesExecution;
import com.hu.oneclick.model.entity.TestCycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TestCycleBiz {
    @Autowired
    TestCycleDao testCycleDao;
    @Autowired
    private TestCycleTcDao testCycleTcDao;
    @Autowired
    private TestCycleJoinTestCaseDao testCycleJoinTestCaseDao;

    public void increaseInstanceCount(Long id, int num) {
        updateInstanceCount(id, num, "up");
    }

    public void decreaseInstanceCount(Long id, int num) {
        updateInstanceCount(id, num, "down");
    }

    public void updateInstanceCount(Long id, int num, String predicate) {
        UpdateWrapper<TestCycle> updateWrapper = Wrappers.update();
        updateWrapper.eq("id", id);
        String sql = "";
        if (predicate.equals("up")) {
            sql = "instance_count=instance_count+" + num;
        } else if (predicate.equals("down")) {
            sql = "instance_count=instance_count-" + num;
        } else if (predicate.equals("refresh")) {
            sql = "instance_count=" + num;
        }
        updateWrapper.setSql(sql);
        testCycleDao.update(new TestCycle(), updateWrapper);
    }

    public List<Long> deleteInstance(TestCycleJoinTestCaseSaveDto dto) {
        List<Long> testCasesIds = new ArrayList<>();

        for (Long testCaseId : dto.getTestCaseIds()) {
            // 删除关联的test_cycle_join_test_case表
            this.testCycleJoinTestCaseDao.deleteByParam(dto.getProjectId(), dto.getTestCycleId(),
                testCaseId);
            testCasesIds.add(testCaseId);
        }
        testCasesIds = Arrays.asList(dto.getTestCaseIds());
//        //删除关联的relation表
//        this.relationService.removeBatchByTestCaseIds(testCasesIds);

        // 删除test_cases_execution表
        testCycleTcDao.delete(
            new LambdaQueryWrapper<TestCasesExecution>().in(TestCasesExecution::getTestCaseId,
                    testCasesIds).eq(TestCasesExecution::getTestCycleId, dto.getTestCycleId())
                .eq(TestCasesExecution::getProjectId, dto.getProjectId()));

        return testCasesIds;
    }
}
