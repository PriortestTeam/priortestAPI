
package com.hu.oneclick.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.dao.TestCycleDao;
import com.hu.oneclick.model.entity.TestCycle;
import com.hu.oneclick.model.domain.dto.TestCycleSaveDto;
import com.hu.oneclick.server.service.TestCycleUpdateService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 测试周期更新服务实现类
 *
 * @author oneclick
 */
@Service
public class TestCycleUpdateServiceImpl extends ServiceImpl<TestCycleDao, TestCycle> implements TestCycleUpdateService {
    
    @Resource
    private TestCycleDao testCycleDao;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TestCycle update(TestCycleSaveDto dto) {
        TestCycle testCycle = baseMapper.getByIdAndProjectId(dto.getId(), dto.getProjectId());
        if (testCycle == null) {
            throw new BaseException(StrUtil.format("测试周期查询不到。ID：{} projectId：{}", dto.getId(), dto.getProjectId()));
        }
        BeanUtil.copyProperties(dto, testCycle);
        // 修改自定义字段
        if (!JSONUtil.isNull(dto.getCustomFieldDatas())) {
            testCycle.setTestcycleExpand(JSONUtil.toJsonStr(dto.getCustomFieldDatas()));
        }
        if(StringUtils.isNotBlank(testCycle.getTitle())){
            List<TestCycle> testCycles = listByTitle(testCycle.getTitle(), dto.getId(), dto.getProjectId());
            if(Objects.nonNull(testCycles) && !testCycles.isEmpty()){
                return null;
            }
        }
        baseMapper.updateById(testCycle);
        return testCycle;
    }  
    
    /**
     * 根据标题查询测试周期列表
     *
     * @param title 标题
     * @param id ID（排除的ID）
     * @param projectId 项目ID
     * @return 测试周期列表
     */
    private List<TestCycle> listByTitle(String title, Long id, Long projectId){
        return this.lambdaQuery()
                .eq(TestCycle::getTitle, title)
                .eq(Objects.nonNull(projectId), TestCycle::getProjectId, projectId)
                .ne(Objects.nonNull(id), TestCycle::getId, id)
                .list();
    }
}
