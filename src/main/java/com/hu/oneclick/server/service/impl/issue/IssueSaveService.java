package com.hu.oneclick.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.dao.IssueDao;
import com.hu.oneclick.dto.IssueSaveDto;
import com.hu.oneclick.model.Issue;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IssueSaveService {

    @Autowired
    private IssueDao issueDao;

    @Autowired
    private IssueTimeConverter issueTimeConverter;

    @Transactional
    public Issue cloneIssue(Issue originalIssue) {
        Issue clonedIssue = BeanUtil.cloneBean(originalIssue, Issue.class);
        clonedIssue.setId(null); // 设置ID为空，表示新Issue
        clonedIssue.setCreateTime(new Date());
        clonedIssue.setUpdateTime(new Date());
        issueDao.insert(clonedIssue);
        return clonedIssue;
    }

    /**
     * 保存新Issue
     */
    public Issue saveNewIssue(IssueSaveDto dto) {
        Issue issue = new Issue();
        BeanUtil.copyProperties(dto, issue);

        // 处理时区转换
        String userTZ = getUserTimezone();
        if (StrUtil.isNotBlank(userTZ)) {
            issueTimeConverter.convertIssueTimeToUTC(issue, userTZ);
        }

        // 新增
        issue.setCreateTime(new Date());
        issue.setUpdateTime(new Date());
        issueDao.insert(issue);

        return issue;
    }

    /**
     * 更新现有Issue
     */
    public Issue updateExistingIssue(IssueSaveDto dto) {
        Issue issue = issueDao.queryById(dto.getId());
        if (issue == null) {
            throw new BaseException("缺陷不存在");
        }

        BeanUtil.copyProperties(dto, issue);

        // 处理时区转换
        String userTZ = getUserTimezone();
        if (StrUtil.isNotBlank(userTZ)) {
            issueTimeConverter.convertIssueTimeToUTC(issue, userTZ);
        }

        issue.setUpdateTime(new Date());
        issueDao.update(issue);

        return issue;
    }

    /**
     * 批量克隆Issues
     */
    public void cloneIssues(List<Long> ids) {
        for (Long id : ids) {
            Issue originalIssue = issueDao.queryById(id);
            if (originalIssue != null) {
                cloneIssue(originalIssue);
            }
        }
    }

    /**
     * 获取用户时区
     */
    private String getUserTimezone() {
        // 这里应该从ThreadLocal或其他地方获取用户时区
        // 暂时返回默认值，具体实现需要根据实际情况调整
        return "UTC";
    }
}