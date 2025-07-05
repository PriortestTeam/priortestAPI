package com.hu.oneclick.server.service.impl;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.security.service.SysPermissionService;
import com.hu.oneclick.common.util.PageUtil;
import com.hu.oneclick.dao.SprintDao;
import com.hu.oneclick.dao.ViewDao;
import com.hu.oneclick.model.entity.Sprint;
import com.hu.oneclick.model.domain.dto.SprintSaveDto;
import com.hu.oneclick.model.param.SprintParam;
import com.hu.oneclick.server.service.QueryFilterService;
import com.hu.oneclick.server.service.SprintService;
import com.hu.oneclick.server.service.ViewFilterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import cn.zhxu.bs.MapSearcher;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * @author qingyang
 */
@Service
public class SprintServiceImpl extends ServiceImpl<SprintDao, Sprint> implements SprintService {
    private final static Logger logger = LoggerFactory.getLogger(SprintServiceImpl.class);
    @Resource
    private SprintDao sprintDao;
    @Resource
    private ViewDao viewDao;
    @Resource
    private MapSearcher mapSearcher;
    @Resource
    private ViewFilterService viewFilterService;
    private final JwtUserServiceImpl jwtUserService;
    private final SysPermissionService sysPermissionService;
    private final QueryFilterService queryFilterService;
    public SprintServiceImpl(JwtUserServiceImpl jwtUserService, SysPermissionService sysPermissionService, QueryFilterService queryFilterService) {
        this.jwtUserService = jwtUserService;
        this.sysPermissionService = sysPermissionService;
        this.queryFilterService = queryFilterService;
    }
    @Override
    public List<Sprint> list(SprintParam param) {
        return this.list(param.getQueryCondition();
    }
    @Override
    public Sprint add(SprintSaveDto dto) {
        Sprint sprint = new Sprint();
        BeanUtil.copyProperties(dto, sprint);
        // 保存自定义字段
        if (!JSONUtil.isNull(dto.getCustomFieldDatas() {
            sprint.setSprintExpand(JSONUtil.toJsonStr(dto.getCustomFieldDatas();
        }
        this.baseMapper.insert(sprint);
        return sprint;
    }
    @Override
    public Sprint edit(SprintSaveDto dto) {
        Sprint entity = this.getByIdAndProjectId(dto.getId(), dto.getProjectId();
        if (null == entity) {
            throw new BaseException(StrUtil.format("迭代查询不到。ID：{} projectId：{}", dto.getId(), dto.getProjectId();
        }
        Sprint sprint = new Sprint();
        BeanUtil.copyProperties(dto, sprint);
        // 保存自定义字段
        if (!JSONUtil.isNull(dto.getCustomFieldDatas() {
            sprint.setSprintExpand(JSONUtil.toJsonStr(dto.getCustomFieldDatas();
        }
        this.baseMapper.updateById(sprint);
        return sprint;
    }
    @Override
    public Sprint getByIdAndProjectId(Long id, Long projectId) {
        QueryWrapper<Sprint> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(Sprint::getId, id)
                .eq(Sprint::getProjectId, projectId);
        Sprint sprint = this.baseMapper.selectOne(queryWrapper);
        return sprint;
    }
    @Override
    public Sprint info(Long id) {
        Sprint sprint = baseMapper.selectById(id);
        if (sprint == null) {
            throw new BaseException(StrUtil.format("迭代查询不到。ID：{}", id);
        }
        return sprint;
    }
    @Override
    public void clone(List<Long> ids) {
        List<Sprint> sprintList = new ArrayList<>();
        for (Long id : ids) {
            Sprint sprint = baseMapper.selectById(id);
            if (sprint == null) {
                throw new BaseException(StrUtil.format("迭代查询不到。ID：{}", id);
            }
            Sprint sprintClone = new Sprint();
            BeanUtil.copyProperties(sprint, sprintClone);
            sprintClone.setId(null);
            sprintList.add(sprintClone);
        }
        // 批量克隆
        this.saveBatch(sprintList);
    }
    @Override
    public PageInfo<Sprint> listWithViewFilter(SprintParam param, int pageNum, int pageSize) {
        if (viewFilterService.shouldApplyViewFilter(param.getViewId() {
            return listWithViewFilterLogic(param, pageNum, pageSize);
        } else {
            return list(param, pageNum, pageSize);
        }
    }
    @Override
    public PageInfo<Sprint> listWithBeanSearcher(String viewId, String projectId, int pageNum, int pageSize) {
        try {
            Map<String, Object> filterParams = viewFilterService.getFilterParamsByViewId(viewId, projectId);
            if (filterParams == null) {
                return new PageInfo<>(new ArrayList<>();
            }
            Class<?> sprintClass = Class.forName("com.hu.oneclick.model.entity.Sprint");
            List<Map<String, Object>> result = mapSearcher.searchAll(sprintClass, filterParams);
            List<Sprint> sprintList = result.stream().map(map -> BeanUtil.toBeanIgnoreError(map, Sprint.class).collect(Collectors.toList();
            return PageUtil.manualPaging(sprintList);
        } catch (Exception e) {
            logger.error("使用BeanSearcher查询迭代失败，viewId: {}, projectId: {}", viewId, projectId, e);
            return new PageInfo<>(new ArrayList<>();
        }
    }
    @Override
    public PageInfo<Sprint> queryByFieldAndValue(String fieldNameEn, String value, String scopeName, String scopeId, int pageNum, int pageSize) {
        String tableName = null;
        switch (scopeName) {
            case "故事": tableName = "feature"; break;
            case "测试用例": tableName = "test_case"; break;
            case "缺陷": tableName = "issue"; break;
            case "测试周期": tableName = "test_cycle"; break;
            case "迭代": tableName = "sprint"; break;
            default: tableName = "sprint";
        }
        String projectId = jwtUserService.getUserLoginInfo().getSysUser().getUserUseOpenProject().getProjectId();
        int offset = (pageNum - 1) * pageSize;
        logger.info("queryByFieldAndValue - 分页参数: pageNum={}, pageSize={}, offset={}", pageNum, pageSize, offset);
        logger.info("queryByFieldAndValue - 查询参数: tableName={}, fieldNameEn={}, value={}, projectId={}", tableName, fieldNameEn, value, projectId);
        List<Map<String, Object>> result = viewDao.queryRecordsByScope(
            tableName,
            fieldNameEn,
            value,
            projectId,
            null,
            offset,
            pageSize
        );
        logger.info("queryByFieldAndValue - 查询结果数量: {}", result.size();
        if (!result.isEmpty() {
            logger.info("queryByFieldAndValue - 第一条记录: {}", result.get(0);
        }
        long total = viewDao.countRecordsByScope(
            tableName,
            fieldNameEn,
            value,
            projectId,
            null
        );
        logger.info("queryByFieldAndValue - 总记录数: {}", total);
        List<Sprint> sprintList = result.stream().map(map -> BeanUtil.toBeanIgnoreError(map, Sprint.class).collect(Collectors.toList();
        PageInfo<Sprint> pageInfo = new PageInfo<>(sprintList);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotal(total);
        pageInfo.setPages((int) ((total + pageSize - 1) / pageSize);
        pageInfo.setIsFirstPage(pageNum == 1);
        pageInfo.setIsLastPage(pageNum >= pageInfo.getPages();
        pageInfo.setHasPreviousPage(pageNum > 1);
        pageInfo.setHasNextPage(pageNum < pageInfo.getPages();
        logger.info("queryByFieldAndValue - 分页信息: pageNum={}, pageSize={}, total={}, pages={}, hasNextPage={}", 
                 pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(), pageInfo.getPages(), pageInfo.isHasNextPage();
        return pageInfo;
    }
    private PageInfo<Sprint> listWithViewFilterLogic(SprintParam param, int pageNum, int pageSize) {
        try {
            Map<String, Object> filterParams = viewFilterService.getFilterParamsByViewId(
                param.getViewId(), param.getProjectId().toString();
            if (filterParams == null) {
                logger.warn("获取视图过滤参数失败，回退到简单查询");
                return list(param, pageNum, pageSize);
            }
            Class<?> sprintClass = Class.forName("com.hu.oneclick.model.entity.Sprint");
            List<Map<String, Object>> result = mapSearcher.searchAll(sprintClass, filterParams);
            List<Sprint> sprintList = result.stream().map(map -> BeanUtil.toBeanIgnoreError(map, Sprint.class).collect(Collectors.toList();
            int total = sprintList.size();
            int startIndex = (pageNum - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, total);
            List<Sprint> pageData = new ArrayList<>();
            if (startIndex < total) {
                pageData = sprintList.subList(startIndex, endIndex);
            }
            PageInfo<Sprint> pageInfo = new PageInfo<>(pageData);
            pageInfo.setPageNum(pageNum);
            pageInfo.setPageSize(pageSize);
            pageInfo.setTotal(total);
            pageInfo.setPages((total + pageSize - 1) / pageSize);
            return pageInfo;
        } catch (Exception e) {
            logger.error("视图过滤查询失败，回退到简单查询", e);
            return list(param, pageNum, pageSize);
        }
    }
    private PageInfo<Sprint> list(SprintParam param, int pageNum, int pageSize) {
        PageUtil.startPage(pageNum, pageSize);
        List<Sprint> dataList = this.list(param);
        return PageInfo.of(dataList);
    }
}
}
}
