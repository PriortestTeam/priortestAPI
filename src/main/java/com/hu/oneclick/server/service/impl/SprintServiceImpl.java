package com.hu.oneclick.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.security.service.SysPermissionService;
import com.hu.oneclick.dao.SprintDao;
import com.hu.oneclick.model.domain.Sprint;
import com.hu.oneclick.model.domain.dto.SprintSaveDto;
import com.hu.oneclick.model.domain.param.SprintParam;
import com.hu.oneclick.server.service.QueryFilterService;
import com.hu.oneclick.server.service.SprintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author qingyang
 */
@Service
public class SprintServiceImpl extends ServiceImpl<SprintDao, Sprint> implements SprintService {

    private final static Logger logger = LoggerFactory.getLogger(SprintServiceImpl.class);

    @Resource
    private SprintDao sprintDao;

    private final JwtUserServiceImpl jwtUserService;

    private final SysPermissionService sysPermissionService;

    private final QueryFilterService queryFilterService;

    public SprintServiceImpl( JwtUserServiceImpl jwtUserService, SysPermissionService sysPermissionService, QueryFilterService queryFilterService) {
        this.jwtUserService = jwtUserService;
        this.sysPermissionService = sysPermissionService;
        this.queryFilterService = queryFilterService;
    }

    @Override
    public List<Sprint> list(SprintParam param) {
        return this.list(param.getQueryCondition());
    }

    @Override
    public Sprint add(SprintSaveDto dto) {
        Sprint sprint = new Sprint();
        BeanUtil.copyProperties(dto, sprint);
        // 保存自定义字段
        if (!JSONUtil.isNull(dto.getCustomFieldDatas())) {
            sprint.setSprintExpand(JSONUtil.toJsonStr(dto.getCustomFieldDatas()));
        }
        this.baseMapper.insert(sprint);
        return sprint;
    }

    @Override
    public Sprint edit(SprintSaveDto dto) {
        Sprint entity = this.getByIdAndProjectId(dto.getId(), dto.getProjectId());
        if (null == entity) {
            throw new BaseException(StrUtil.format("迭代查询不到。ID：{} projectId：{}", dto.getId(), dto.getProjectId()));
        }
        Sprint sprint = new Sprint();
        BeanUtil.copyProperties(dto, sprint);
        // 保存自定义字段
        if (!JSONUtil.isNull(dto.getCustomFieldDatas())) {
            sprint.setSprintExpand(JSONUtil.toJsonStr(dto.getCustomFieldDatas()));
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
            throw new BaseException(StrUtil.format("迭代查询不到。ID：{}", id));
        }
        return sprint;
    }

    @Override
    public void clone(List<Long> ids) {
        List<Sprint> sprintList = new ArrayList<>();
        for (Long id : ids) {
            Sprint sprint = baseMapper.selectById(id);
            if (sprint == null) {
                throw new BaseException(StrUtil.format("迭代查询不到。ID：{}", id));
            }
            Sprint sprintClone = new Sprint();
            BeanUtil.copyProperties(sprint, sprintClone);
            sprintClone.setId(null);
            sprintList.add(sprintClone);
        }
        // 批量克隆
        this.saveBatch(sprintList);
    }
}
