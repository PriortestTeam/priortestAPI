package com.hu.oneclick.biz;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hu.oneclick.dao.TestCycleDao;
import com.hu.oneclick.model.entity.TestCycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestCycleBiz {
    @Autowired
    TestCycleDao testCycleDao;

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
        }
        updateWrapper.setSql(sql);
        testCycleDao.update(new TestCycle(), updateWrapper);
    }
}
