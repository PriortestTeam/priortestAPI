
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
        
        // 添加DEV版本映射
        if (CollectionUtil.isNotEmpty(mappingDto.getDevVersions())) {
            for (String devVersion : mappingDto.getDevVersions()) {
                VersionMapping mapping = new VersionMapping();
                mapping.setProjectId(mappingDto.getProjectId());
                mapping.setReleaseId(mappingDto.getReleaseId());
                mapping.setReleaseVersion(mappingDto.getReleaseVersion());
                mapping.setEnv("dev");
                mapping.setEnvVersion(devVersion);
                mapping.setRemark(mappingDto.getRemark());
                mappings.add(mapping);
            }
        }
        
        // 添加STG版本映射
        if (CollectionUtil.isNotEmpty(mappingDto.getStgVersions())) {
            for (String stgVersion : mappingDto.getStgVersions()) {
                VersionMapping mapping = new VersionMapping();
                mapping.setProjectId(mappingDto.getProjectId());
                mapping.setReleaseId(mappingDto.getReleaseId());
                mapping.setReleaseVersion(mappingDto.getReleaseVersion());
                mapping.setEnv("stg");
                mapping.setEnvVersion(stgVersion);
                mapping.setRemark(mappingDto.getRemark());
                mappings.add(mapping);
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
    public void addMapping(VersionMappingDto mappingDto) {
        VersionMapping mapping = new VersionMapping();
        BeanUtil.copyProperties(mappingDto, mapping);
        mapping.setCreateTime(new java.util.Date());
        mapping.setUpdateTime(new java.util.Date());
        // 设置创建者ID，如果需要的话可以从上下文获取
        // mapping.setCreateUserId(getCurrentUserId());
        // mapping.setUpdateUserId(getCurrentUserId());
        versionMappingDao.insert(mapping);
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
