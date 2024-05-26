package com.hu.oneclick.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hu.oneclick.dao.VersionDao;
import com.hu.oneclick.model.domain.Feature;
import com.hu.oneclick.model.domain.Version;
import com.hu.oneclick.model.domain.dto.VersionDto;
import com.hu.oneclick.model.domain.dto.VersionRequestDto;
import com.hu.oneclick.server.service.VersionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class VersionServiceImpl implements VersionService {

    @Autowired
    VersionDao versionDao;
    @Override
    public void releaseCreation(VersionRequestDto releaseCreationDto) {
        Version version = new Version();
        BeanUtil.copyProperties(releaseCreationDto, version);
        versionDao.insert(version);
    }

    @Override
    public void releaseModification(VersionRequestDto releaseModification) {
        Version db = new Version();
        BeanUtil.copyProperties(releaseModification, db);
        db.setId(releaseModification.getVersionId());
        versionDao.updateById(db);

    }

    @Override
    public VersionDto getVersion(Long version) {
        Version v = versionDao.selectById(version);
        VersionDto versionDto = new VersionDto();
        BeanUtil.copyProperties(v, versionDto);
        return versionDto;
    }

    @Override
    public List<VersionDto> getVersionList(VersionRequestDto versionRequestDto) {
        LambdaQueryWrapper<Version> queryWrapper = new LambdaQueryWrapper<>();
        if(versionRequestDto.getProjectId() != null) {
            queryWrapper.eq(Version::getProjectId, versionRequestDto.getProjectId());
        }
        List<Version> list = versionDao.selectList(queryWrapper);
        return BeanUtil.copyToList(list, VersionDto.class);
    }
}
