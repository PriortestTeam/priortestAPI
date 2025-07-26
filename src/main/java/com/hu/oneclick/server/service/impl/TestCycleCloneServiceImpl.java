
package com.hu.oneclick.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.common.util.CloneFormatUtil;
import com.hu.oneclick.dao.TestCycleDao;
import com.hu.oneclick.model.entity.TestCycle;
import com.hu.oneclick.server.service.TestCycleCloneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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
            String uniqueCloneTitle = generateUniqueCloneTitle(testCycle.getTitle(), testCycle.getProjectId());
            testCaseClone.setTitle(uniqueCloneTitle);
            
            testCycleList.add(testCaseClone);
        }
        // 批量克隆
        this.saveBatch(testCycleList);
    }
    
    /**
     * 生成唯一的克隆标题
     *
     * @param originalTitle 原始标题
     * @param projectId     项目ID
     * @return 唯一的克隆标题
     */
    private String generateUniqueCloneTitle(String originalTitle, Long projectId) {
        return CloneFormatUtil.generateUniqueCloneTitle(originalTitle, projectId, testCycleDao);
    }
}
