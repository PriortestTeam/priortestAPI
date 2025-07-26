package com.hu.oneclick.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.util.CloneFormatUtil;
import com.hu.oneclick.common.util.PageUtil;
import com.hu.oneclick.dao.*;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.OneFilter;
import com.hu.oneclick.model.entity.TestCycle;
import com.hu.oneclick.model.entity.View;
import com.hu.oneclick.model.domain.dto.LeftJoinDto;
import com.hu.oneclick.model.domain.dto.SignOffDto;
import com.hu.oneclick.model.domain.dto.TestCycleSaveDto;
import com.hu.oneclick.model.param.TestCycleParam;
import com.hu.oneclick.server.service.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TestCycleServiceImpl extends ServiceImpl<TestCycleDao, TestCycle> implements TestCycleService {

    private final static Logger logger = LoggerFactory.getLogger(TestCycleServiceImpl.class);


   
    @Resource
    private JwtUserServiceImpl jwtUserService;
    @Resource
    private TestCycleDao testCycleDao;
   
  
    @Resource
    private cn.zhxu.bs.MapSearcher mapSearcher;
    @Resource
    private ViewDao viewDao;
    @Resource
    private TestCycleCloneService testCycleCloneService;
    @Resource
    private TestCycleUpdateService testCycleUpdateService;
   

    @Override
    public Resp<List<LeftJoinDto>> queryTitles(String projectId, String title) {
        List<LeftJoinDto> select = testCycleDao.queryTitles(projectId, title, jwtUserService.getMasterId());
        return new Resp.Builder<List<LeftJoinDto>>().setData(select).total(select.size()).ok();
    }
//
//
    @Override
    public Resp<TestCycle> queryById(String id) {
        String masterId = jwtUserService.getMasterId();
        TestCycle testCycle = testCycleDao.queryById(id, masterId);

        return new Resp.Builder<TestCycle>().setData(testCycle).ok();
    }

    @Override
    public Resp<List<Map<String, String>>> getTestCycleVersion(String projectId, String env, String version) {
        List<Map<String, String>> testCycleVersion = testCycleDao.getTestCycleVersion(projectId, env, version);
        return new Resp.Builder<List<Map<String, String>>>().setData(testCycleVersion).ok();
    }
//
    @Override
    public List<Map<String, Object>> getAllTestCycle(SignOffDto signOffDto) {

        List<Map<String, Object>> allTestCycle = testCycleDao.getAllTestCycle(signOffDto.getProjectId(), signOffDto.getVersion(), signOffDto.getEnv(), signOffDto.getTestCycle());
        return allTestCycle;
    }

    @Override
    public List<String> getTestCycleByProjectIdAndEvn(String projectId, String env, String testCycle) {
        return testCycleDao.getTestCycleByProjectIdAndEvn(projectId, env, testCycle);
    }

    @Override
    public List<TestCycle> list(TestCycleParam param) {
        return this.lambdaQuery()
                .eq(TestCycle::getProjectId, param.getProjectId())
                .like(StrUtil.isNotBlank(param.getTitle()), TestCycle::getTitle, param.getTitle())
                .orderByDesc(TestCycle::getCreateTime)
                .list();
    }

    @Override
    public TestCycle save(TestCycleSaveDto dto) {
        return testCycleUpdateService.save(dto);
    }

    @Override
    public TestCycle update(TestCycleSaveDto dto) {
         return testCycleUpdateService.update(dto);
    }

    @Override
    public TestCycle info(Long id) {
        TestCycle testCycle = baseMapper.selectById(id);
        if (testCycle == null) {
            throw new BizException(StrUtil.format("测试周期查询不到。ID：{}", id));
        }
        return testCycle;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clone(List<Long> ids) {
        testCycleCloneService.clone(ids);
    }
     
   
    private List<TestCycle> listByTitle(String title, Long id, Long projectId){
       return  this.lambdaQuery()
               .eq(TestCycle::getTitle, title)
               .eq(Objects.nonNull(projectId), TestCycle::getProjectId, projectId)
               .ne(Objects.nonNull(id), TestCycle::getId, id)
               .list();
    }

    @Override
    public PageInfo<TestCycle> listWithViewFilter(TestCycleParam param, int pageNum, int pageSize) {
        // 使用 PageHelper 进行分页
        PageHelper.startPage(pageNum, pageSize);
        List<TestCycle> list = this.lambdaQuery()
                .eq(TestCycle::getProjectId, param.getProjectId())
                .like(StrUtil.isNotBlank(param.getTitle()), TestCycle::getTitle, param.getTitle())
                .orderByDesc(TestCycle::getCreateTime)
                .list();
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<TestCycle> listWithBeanSearcher(String viewId, String projectId, int pageNum, int pageSize) {
        try {
            // 使用与 BeanSearchController 相同的视图过滤逻辑
            List<List<OneFilter>> lst = new ArrayList<>();
            
            // 查询视图
            View view1 = viewDao.selectById(viewId);
            if (view1 == null) {
                logger.error("视图不存在，viewId: {}", viewId);
                return new PageInfo<>(new ArrayList<>());
            }
            
            this.processAllFilter(view1, lst);
            
            if (CollUtil.isEmpty(lst)) {
                // 如果没有过滤条件，直接查询所有
                PageHelper.startPage(pageNum, pageSize);
                List<TestCycle> list = this.lambdaQuery()
                        .eq(TestCycle::getProjectId, Long.valueOf(projectId))
                        .orderByDesc(TestCycle::getCreateTime)
                        .list();
                return new PageInfo<>(list);
            }
            
            // 构建查询参数
            Map<String, Object> params = this.processParam(lst, projectId);
            
            // 使用 MapSearcher 进行查询
            List<Map<String, Object>> mapList = mapSearcher.searchAll(TestCycle.class, params);
            
            // 手动分页
            int total = mapList.size();
            int startIndex = (pageNum - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, total);
            
            List<Map<String, Object>> pagedMapList = new ArrayList<>();
            if (startIndex < total) {
                pagedMapList = mapList.subList(startIndex, endIndex);
            }
            
            // 转换为 TestCycle 对象
            List<TestCycle> testCycleList = pagedMapList.stream()
                    .map(map -> BeanUtil.toBeanIgnoreError(map, TestCycle.class))
                    .collect(Collectors.toList());
            
            // 构造 PageInfo
            PageInfo<TestCycle> pageInfo = new PageInfo<>(testCycleList);
            pageInfo.setPageNum(pageNum);
            pageInfo.setPageSize(pageSize);
            pageInfo.setTotal(total);
            pageInfo.setPages((int) ((total + pageSize - 1) / pageSize));
            pageInfo.setIsFirstPage(pageNum == 1);
            pageInfo.setIsLastPage(pageNum >= pageInfo.getPages());
            pageInfo.setHasPreviousPage(pageNum > 1);
            pageInfo.setHasNextPage(pageNum < pageInfo.getPages());
            
            logger.info("listWithBeanSearcher - 查询结果数量: {}, 分页信息: pageNum={}, pageSize={}, total={}", 
                     testCycleList.size(), pageNum, pageSize, total);
            
            return pageInfo;
        } catch (Exception e) {
            logger.error("查询测试周期失败，viewId: {}, projectId: {}", viewId, projectId, e);
            return new PageInfo<>(new ArrayList<>());
        }
    }
    
    private Map<String, Object> processParam(List<List<OneFilter>> lst, String projectId){
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("P0.projectId", projectId);
        params.put("P0.projectId-op", "eq");

        // 参数增加逻辑关系
        StringBuilder gexpr = new StringBuilder();
        gexpr.append("P0");

        int j = 0;
        for(List<OneFilter> oneFilters : lst){
            gexpr.append("&(");
            for (int i = 0; i < oneFilters.size(); i++) {
                String fieldName = StrUtil.format("A_{}_{}", j, i);
                params.put(StrUtil.format("{}.{}", fieldName, oneFilters.get(i).getFieldNameEn()), oneFilters.get(i).getSourceVal());
                params.put(StrUtil.format("{}.{}-op", fieldName, oneFilters.get(i).getFieldNameEn()), oneFilters.get(i).getCondition());
                if(i == 0){
                    gexpr.append(fieldName);
                } else {
                    gexpr.append(oneFilters.get(i).getAndOr().equals("and") ? "&" : "|");
                    gexpr.append(fieldName);
                }
            }
            gexpr.append(")");
            j = j + 1;
        }
        params.put("gexpr", gexpr.toString());

        return params;
    }
    
    private void processAllFilter(View view, List<List<OneFilter>> lst){
        if(StringUtils.isNotEmpty(view.getParentId()) && view.getLevel() > 0){
            View tempView = viewDao.selectById(view.getParentId());
            this.processAllFilter(tempView, lst);

            lst.add(view.getOneFilters());
        } else {
            lst.add(view.getOneFilters());
        }
    }

    @Override
    public PageInfo<TestCycle> queryByFieldAndValue(String fieldNameEn, String value, String scopeName, String scopeId, int pageNum, int pageSize) {
        // 1. 确定表名
        String tableName = null;
        switch (scopeName) {
            case "故事": tableName = "feature"; break;
            case "测试用例": tableName = "test_case"; break;
            case "缺陷": tableName = "issue"; break;
            case "测试周期": tableName = "test_cycle"; break;
            default: tableName = "test_cycle";
        }
        // 2. 获取 projectId
        String projectId = jwtUserService.getUserLoginInfo().getSysUser().getUserUseOpenProject().getProjectId();
        
        // 3. 计算偏移量
        int offset = (pageNum - 1) * pageSize;
        
        // 添加调试日志
        logger.info("queryByFieldAndValue - 分页参数: pageNum={}, pageSize={}, offset={}", pageNum, pageSize, offset);
        logger.info("queryByFieldAndValue - 查询参数: tableName={}, fieldNameEn={}, value={}, projectId={}", tableName, fieldNameEn, value, projectId);
        
        // 4. 使用 DAO 方法查询数据
        List<Map<String, Object>> result = viewDao.queryRecordsByScope(
            tableName,
            fieldNameEn,
            value,
            projectId,
            null, // 不排除任何用户创建的记录
            offset,
            pageSize
        );
        
        logger.info("queryByFieldAndValue - 查询结果数量: {}", result.size());
        if (!result.isEmpty()) {
            logger.info("queryByFieldAndValue - 第一条记录: {}", result.get(0));
        }
        
        // 5. 查询总数
        long total = viewDao.countRecordsByScope(
            tableName,
            fieldNameEn,
            value,
            projectId,
            null
        );
        
        logger.info("queryByFieldAndValue - 总记录数: {}", total);
        
        // 6. 转 bean
        List<TestCycle> testCycleList = result.stream().map(map -> BeanUtil.toBeanIgnoreError(map, TestCycle.class)).collect(Collectors.toList());
        
        // 7. 构造 PageInfo
        PageInfo<TestCycle> pageInfo = new PageInfo<>(testCycleList);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotal(total);
        pageInfo.setPages((int) ((total + pageSize - 1) / pageSize));
        pageInfo.setIsFirstPage(pageNum == 1);
        pageInfo.setIsLastPage(pageNum >= pageInfo.getPages());
        pageInfo.setHasPreviousPage(pageNum > 1);
        pageInfo.setHasNextPage(pageNum < pageInfo.getPages());
        
        logger.info("queryByFieldAndValue - 分页信息: pageNum={}, pageSize={}, total={}, pages={}, hasNextPage={}", 
                 pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(), pageInfo.getPages(), pageInfo.isHasNextPage());
        
        return pageInfo;
    }
}