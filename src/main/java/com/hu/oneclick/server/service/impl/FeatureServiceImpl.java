package com.hu.oneclick.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.security.service.SysPermissionService;
import com.hu.oneclick.common.util.CloneFormatUtil;
import com.hu.oneclick.dao.FeatureDao;
import com.hu.oneclick.dao.FeatureJoinSprintDao;
import com.hu.oneclick.dao.SprintDao;
import com.hu.oneclick.model.entity.Feature;
import com.hu.oneclick.model.domain.dto.FeatureSaveDto;
import com.hu.oneclick.model.param.FeatureParam;
import com.hu.oneclick.server.service.CustomFieldDataService;
import com.hu.oneclick.server.service.FeatureService;
import com.hu.oneclick.server.service.QueryFilterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.alibaba.fastjson2.JSON;

/**
 * @author qingyang
 */
@Service
public class FeatureServiceImpl extends ServiceImpl<FeatureDao, Feature> implements FeatureService {

    private final static Logger logger = LoggerFactory.getLogger(FeatureServiceImpl.class);

    @Resource
    private FeatureDao featureDao;
    @Resource
    private FeatureJoinSprintDao featureJoinSprintDao;
    @Resource
    private SprintDao sprintDao;
    @Resource
    private JwtUserServiceImpl jwtUserService;
    @Resource
    private SysPermissionService sysPermissionService;
    @Resource
    private QueryFilterService queryFilterService;
    @Resource
    private CustomFieldDataService customFieldDataService;

    @Override
    public List<Feature> list(FeatureParam param) {
        return this.list(param.getQueryCondition());
    }

    @Override
    public Feature add(FeatureSaveDto dto) {
        Feature feature = new Feature();
        BeanUtil.copyProperties(dto, feature);
        // 保存自定义字段
        if (!JSONUtil.isNull(dto.getCustomFieldDatas())) {
            feature.setFeatureExpand(JSONUtil.toJsonStr(dto.getCustomFieldDatas()));
        }
        this.baseMapper.insert(feature);
        return feature;
    }

    @Override
    public Feature edit(FeatureSaveDto dto) {
        Feature feature = new Feature();
        BeanUtil.copyProperties(dto, feature);
        // 保存自定义字段
        if (!JSONUtil.isNull(dto.getCustomFieldDatas())) {
            feature.setFeatureExpand(JSONUtil.toJsonStr(dto.getCustomFieldDatas()));
        }
        this.baseMapper.updateById(feature);
        return feature;
    }

    @Override
    public List<Map<String, String>> queryTitles(String projectId, String title) {
        QueryWrapper<Feature> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", projectId);
        if (StrUtil.isNotBlank(title)) {
            queryWrapper.like("title", title);
        }
        queryWrapper.select("id", "title");

        List<Feature> features = this.baseMapper.selectList(queryWrapper);

        return features.stream()
                .map(feature -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("id", feature.getId().toString());
                    map.put("title", feature.getTitle());
                    return map;
                })
                .collect(Collectors.toList());
    }
}