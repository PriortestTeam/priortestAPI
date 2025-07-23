
package com.hu.oneclick.server.service.impl.issue;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.hu.oneclick.common.exception.ServiceException;
import com.hu.oneclick.dao.IssueDao;
import com.hu.oneclick.model.domain.dto.IssueSaveDto;
import com.hu.oneclick.model.domain.vo.IssueVo;
import com.hu.oneclick.model.entity.Issue;
import com.hu.oneclick.server.service.ModifyRecordsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Issue保存相关服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IssueSaveService {

    private final IssueDao issueDao;
    private final ModifyRecordsService modifyRecordsService;
    private final IssueTimeConverter issueTimeConverter;

    /**
     * 保存Issue
     */
    public IssueVo saveIssue(IssueSaveDto dto) {
        Issue issue = new Issue();
        BeanUtil.copyProperties(dto, issue);

        // 处理时区转换
        String userTZ = IssueServiceImpl.getUserTimezone();
        if (StrUtil.isNotBlank(userTZ)) {
            issueTimeConverter.convertIssueTimeToUTC(issue, userTZ);
        }

        if (issue.getId() != null) {
            // 更新
            Issue existingIssue = issueDao.queryById(issue.getId());
            if (existingIssue == null) {
                throw new ServiceException("缺陷不存在");
            }
            issueDao.update(issue);
        } else {
            // 新增
            issue.setCreateTime(new Date());
            issue.setUpdateTime(new Date());
            issueDao.insert(issue);
        }

        // 查询并返回最新数据
        Issue savedIssue = issueDao.queryById(issue.getId());
        
        // 转换回用户时区
        if (StrUtil.isNotBlank(userTZ)) {
            issueTimeConverter.convertIssueTimeToUserTZ(savedIssue, userTZ);
        }

        IssueVo issueVo = new IssueVo();
        BeanUtil.copyProperties(savedIssue, issueVo);
        return issueVo;
    }

    /**
     * 克隆Issue
     */
    public Issue cloneIssue(Issue issue) {
        Issue clonedIssue = new Issue();
        BeanUtil.copyProperties(issue, clonedIssue);
        
        // 清除ID和时间字段
        clonedIssue.setId(null);
        clonedIssue.setCreateTime(new Date());
        clonedIssue.setUpdateTime(new Date());
        
        // 在标题前加上"复制 - "
        if (StrUtil.isNotBlank(clonedIssue.getTitle())) {
            clonedIssue.setTitle("复制 - " + clonedIssue.getTitle());
        }
        
        issueDao.insert(clonedIssue);
        return clonedIssue;
    }
}
