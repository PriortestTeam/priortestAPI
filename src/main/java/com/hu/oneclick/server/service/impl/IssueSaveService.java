
package com.hu.oneclick.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.hu.oneclick.dao.IssueDao;
import com.hu.oneclick.model.entity.Issue;
import com.hu.oneclick.model.domain.dto.IssueSaveDto;
import com.hu.oneclick.common.util.TimezoneContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class IssueSaveService {

    @Resource
    private IssueDao issueDao;

    @Resource
    private IssueTimeConverter issueTimeConverter;

    /**
     * 处理新增缺陷的所有逻辑
     */
    @Transactional
    public Issue saveNewIssue(IssueSaveDto dto) {
        Issue issue = new Issue();
        BeanUtil.copyProperties(dto, issue);

        // 获取用户时区
        String userTimezone = TimezoneContext.getUserTimezone();

        // 转换日期到UTC
        issueTimeConverter.convertDatesToUTC(issue, userTimezone);

        // 处理自定义字段数据
        processCustomFields(issue, dto);

        // 处理版本相关字段
        processVersionFields(issue, dto);

        issueDao.insert(issue);
        return issue;
    }

    /**
     * 处理编辑缺陷的所有逻辑
     */
    @Transactional
    public Issue updateExistingIssue(IssueSaveDto dto) {
        Issue issue = new Issue();
        BeanUtil.copyProperties(dto, issue);

        // 获取用户时区
        String userTimezone = TimezoneContext.getUserTimezone();

        // 转换日期到UTC
        issueTimeConverter.convertDatesToUTC(issue, userTimezone);

        // 处理自定义字段数据
        processCustomFields(issue, dto);

        // 处理版本相关字段
        processVersionFields(issue, dto);

        issueDao.updateById(issue);

        return issue;
    }

    /**
     * 处理自定义字段数据
     */
    private void processCustomFields(Issue issue, IssueSaveDto dto) {
        Map<String, Object> customFieldMap = new HashMap<>();
        if (!JSONUtil.isNull(dto.getCustomFieldDatas())) {
            customFieldMap = JSONUtil.toBean(JSONUtil.toJsonStr(dto.getCustomFieldDatas()), Map.class);
        }

        // 检查并添加新字段到自定义字段数据中
        if (dto.getIntroducedVersion() != null) {
            customFieldMap.put("introduced_version", dto.getIntroducedVersion());
        }
        if (dto.getIsLegacy() != null) {
            customFieldMap.put("is_legacy", dto.getIsLegacy());
        }
        if (dto.getFoundAfterRelease() != null) {
            customFieldMap.put("found_after_release", dto.getFoundAfterRelease());
        }

        // 保存自定义字段
        if (!customFieldMap.isEmpty()) {
            issue.setIssueExpand(JSONUtil.toJsonStr(customFieldMap));
        }
    }

    /**
     * 处理版本相关字段
     */
    private void processVersionFields(Issue issue, IssueSaveDto dto) {
        // 直接从 DTO 中获取三个版本相关字段
        if (dto.getIntroducedVersion() != null) {
            issue.setIntroducedVersion(dto.getIntroducedVersion());
        }
        if (dto.getIsLegacy() != null) {
            issue.setIsLegacy(dto.getIsLegacyAsInt());
        }
        if (dto.getFoundAfterRelease() != null) {
            issue.setFoundAfterRelease(dto.getFoundAfterReleaseAsInt());
        }
    }
}
