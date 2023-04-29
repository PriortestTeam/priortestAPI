package com.hu.oneclick.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.security.service.SysPermissionService;
import com.hu.oneclick.dao.IssueDao;
import com.hu.oneclick.model.domain.Issue;
import com.hu.oneclick.model.domain.dto.IssueSaveDto;
import com.hu.oneclick.model.domain.param.IssueParam;
import com.hu.oneclick.server.service.CustomFieldDataService;
import com.hu.oneclick.server.service.IssueService;
import com.hu.oneclick.server.service.ModifyRecordsService;
import com.hu.oneclick.server.service.QueryFilterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class IssueServiceImpl extends ServiceImpl<IssueDao, Issue> implements IssueService {

    private final static Logger logger = LoggerFactory.getLogger(IssueServiceImpl.class);

    @Resource
    private IssueDao issueDao;

    private final JwtUserServiceImpl jwtUserService;

    private final ModifyRecordsService modifyRecordsService;

    private final SysPermissionService sysPermissionService;

    private final QueryFilterService queryFilterService;

    private final CustomFieldDataService customFieldDataService;


    public IssueServiceImpl(JwtUserServiceImpl jwtUserService, ModifyRecordsService modifyRecordsService, SysPermissionService sysPermissionService, QueryFilterService queryFilterService, CustomFieldDataService customFieldDataService) {
        this.jwtUserService = jwtUserService;
        this.modifyRecordsService = modifyRecordsService;
        this.sysPermissionService = sysPermissionService;
        this.queryFilterService = queryFilterService;
        this.customFieldDataService = customFieldDataService;
    }



    @Override
    public List<Issue> list(IssueParam param) {
        return this.list(param.getQueryCondition());
    }

    @Override
    public Issue add(IssueSaveDto dto) {
        Issue issue = new Issue();
        BeanUtil.copyProperties(dto, issue);
        // 保存自定义字段
        if (!JSONUtil.isNull(dto.getCustomFieldDatas())) {
            issue.setIssueExpand(JSONUtil.toJsonStr(dto.getCustomFieldDatas()));
        }
        this.baseMapper.insert(issue);
        return issue;
    }

    @Override
    public Issue edit(IssueSaveDto dto) {
        Issue entity = this.getByIdAndProjectId(dto.getId(), dto.getProjectId());
        if (null == entity) {
            throw new BaseException(StrUtil.format("缺陷查询不到。ID：{} projectId：{}", dto.getId(), dto.getProjectId()));
        }
        Issue issue = new Issue();
        BeanUtil.copyProperties(dto, issue);
        // 保存自定义字段
        if (!JSONUtil.isNull(dto.getCustomFieldDatas())) {
            issue.setIssueExpand(JSONUtil.toJsonStr(dto.getCustomFieldDatas()));
        }
        this.baseMapper.updateById(issue);
        return issue;
    }

    private Issue getByIdAndProjectId(Long id, Long projectId) {
        QueryWrapper<Issue> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(Issue::getId, id)
                .eq(Issue::getProjectId, projectId);
        Issue issue = this.baseMapper.selectOne(queryWrapper);
        return issue;
    }

    @Override
    public Issue info(Long id) {
        Issue issue = baseMapper.selectById(id);
        if (issue == null) {
            throw new BaseException(StrUtil.format("缺陷查询不到。ID：{}", id));
        }
        return issue;
    }

    @Override
    public void clone(List<Long> ids) {
        List<Issue> issueList = new ArrayList<>();
        for (Long id : ids) {
            Issue issue = baseMapper.selectById(id);
            if (issue == null) {
                throw new BaseException(StrUtil.format("缺陷查询不到。ID：{}", id));
            }
            Issue issueClone = new Issue();
            BeanUtil.copyProperties(issue, issueClone);
            issueClone.setId(null);
            issueList.add(issueClone);
        }
        // 批量克隆
        this.saveBatch(issueList);
    }
}
