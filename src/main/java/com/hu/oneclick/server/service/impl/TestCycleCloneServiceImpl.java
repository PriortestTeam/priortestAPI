
package com.hu.oneclick.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.common.util.CloneFormatUtil;
import com.hu.oneclick.dao.TestCycleDao;
import com.hu.oneclick.model.entity.TestCycle;
import com.hu.oneclick.server.service.TestCycleCloneService;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 测试周期克隆服务实现类
 *
 * @author oneclick
 */
@Service
public class TestCycleCloneServiceImpl extends ServiceImpl<TestCycleDao, TestCycle> implements TestCycleCloneService {
    
    @Resource
    private TestCycleDao testCycleDao;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clone(List<Long> ids) {
        List<TestCycle> testCycleList = new ArrayList<>();
        for (Long id : ids) {
            TestCycle testCycle = baseMapper.selectById(id);
            if (testCycle == null) {
                throw new BaseException(StrUtil.format("测试周期查询不到。ID：{}", id));
            }
            TestCycle testCaseClone = new TestCycle();
            BeanUtil.copyProperties(testCycle, testCaseClone);
            testCaseClone.setId(null);
            
            // 生成唯一的克隆标题
            // 重置相关的字段
            String uniqueCloneTitle = generateUniqueCloneTitle(testCycle.getTitle(), testCycle.getProjectId());
            testCaseClone.setTitle(uniqueCloneTitle);
            testCaseClone.setTestCycleStatus("草稿");
            testCaseClone.setInstanceCount(0);
            testCaseClone.setRunStatus(0);
            testCaseClone.setReleased(0);
            testCaseClone.setCurrentRelease(0);
            testCaseClone.setExeucteProgress(0);
            testCaseClone.setLastRunDate(null);
            testCaseClone.setAutoJobStart(null);
            testCaseClone.setAutoJobEnd(null);
            testCaseClone.setAutoJobRunTime(null);
            testCaseClone.setAllureReportUrl(null);
            testCaseClone.setNotRunCount(0);
            testCaseClone.setPlanExecuteDate(null);
            
            testCycleList.add(testCaseClone);
        }
        // 批量克隆
        this.saveBatch(testCycleList);
    }
    
    /**
     * 生成唯一的克隆标题
     */
    private String generateUniqueCloneTitle(String originalTitle, Long projectId) {
        String baseTitle = CloneFormatUtil.getCloneTitle(originalTitle);
        String uniqueTitle = baseTitle;
        int counter = 1;

        // 检查标题是否已存在，如果存在则添加数字后缀
        while (!listByTitle(uniqueTitle, null, projectId).isEmpty()) {
            uniqueTitle = baseTitle + "(" + counter + ")";
            counter++;
        }

        return uniqueTitle;
    }

    private List<TestCycle> listByTitle(String title, Long id, Long projectId){
       return  this.lambdaQuery()
               .eq(TestCycle::getTitle, title)
               .eq(Objects.nonNull(projectId), TestCycle::getProjectId, projectId)
               .ne(Objects.nonNull(id), TestCycle::getId, id)
               .list();
    }
}
