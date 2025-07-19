package com.hu.oneclick.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hu.oneclick.dao.VersionMappingDao;
import com.hu.oneclick.model.entity.VersionMapping;
import com.hu.oneclick.model.domain.dto.VersionMappingDto;
import com.hu.oneclick.server.service.VersionMappingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class VersionMappingServiceImpl implements VersionMappingService {

    @Autowired
    VersionMappingDao versionMappingDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchCreateMapping(VersionMappingDto mappingDto) {
        // 先删除该发布版本的现有映射
        deleteMappingByRelease(mappingDto.getReleaseId());

        List<VersionMapping> mappings = new ArrayList<>();

        // 动态添加各环境版本映射
        if (mappingDto.getEnvVersions() != null && !mappingDto.getEnvVersions().isEmpty()) {
            for (Map.Entry<String, List<String>> envEntry : mappingDto.getEnvVersions().entrySet()) {
                String envName = envEntry.getKey();
                List<String> versions = envEntry.getValue();

                if (CollectionUtil.isNotEmpty(versions)) {
                    for (String version : versions) {
                        VersionMapping mapping = new VersionMapping();
                        mapping.setProjectId(mappingDto.getProjectId());
                        mapping.setReleaseId(mappingDto.getReleaseId());
                        mapping.setReleaseVersion(mappingDto.getReleaseVersion());
                        mapping.setEnv(envName);
                        mapping.setEnvVersion(version);
                        mapping.setRemark(mappingDto.getRemark());
                        mappings.add(mapping);
                    }
                }
            }
        }

        // 批量插入
        for (VersionMapping mapping : mappings) {
            versionMappingDao.insert(mapping);
        }
    }

    @Override
    public void updateMapping(VersionMappingDto mappingDto) {
        VersionMapping mapping = new VersionMapping();
        BeanUtil.copyProperties(mappingDto, mapping);
        versionMappingDao.updateById(mapping);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMapping(VersionMappingDto mappingDto) {
        // 单个版本映射追加
        if (mappingDto.getEnvVersion() != null) {
            VersionMapping mapping = new VersionMapping();
            BeanUtil.copyProperties(mappingDto, mapping);
            versionMappingDao.insert(mapping);
        }

        // 单环境多版本追加
        if (CollectionUtil.isNotEmpty(mappingDto.getVersions()) && mappingDto.getEnv() != null) {
            for (String version : mappingDto.getVersions()) {
                VersionMapping mapping = new VersionMapping();
                mapping.setProjectId(mappingDto.getProjectId());
                mapping.setReleaseId(mappingDto.getReleaseId());
                mapping.setReleaseVersion(mappingDto.getReleaseVersion());
                mapping.setEnv(mappingDto.getEnv());
                mapping.setEnvVersion(version);
                mapping.setRemark(mappingDto.getRemark());
                versionMappingDao.insert(mapping);
            }
        }
    }

    @Override
    public void deleteMapping(Long id) {
        versionMappingDao.deleteById(id);
    }

    @Override
    public List<VersionMappingDto> getMappingByRelease(Long releaseId) {
        List<VersionMapping> mappings = versionMappingDao.selectByReleaseId(releaseId);
        return BeanUtil.copyToList(mappings, VersionMappingDto.class);
    }

    @Override
    public List<VersionMappingDto> getMappingByProject(Long projectId) {
        LambdaQueryWrapper<VersionMapping> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VersionMapping::getProjectId, projectId);
        List<VersionMapping> mappings = versionMappingDao.selectList(queryWrapper);
        return BeanUtil.copyToList(mappings, VersionMappingDto.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMappingByRelease(Long releaseId) {
        LambdaQueryWrapper<VersionMapping> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VersionMapping::getReleaseId, releaseId);
        versionMappingDao.delete(queryWrapper);
    }
}