package com.hu.oneclick.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.util.StringUtil;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.dao.VersionDao;
import com.hu.oneclick.model.entity.ReleaseManagement;
import com.hu.oneclick.model.domain.dto.VersionDto;
import com.hu.oneclick.model.domain.dto.VersionRequestDto;
import com.hu.oneclick.server.service.VersionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j


public class VersionServiceImpl implements VersionService {

    @Autowired
    VersionDao versionDao;

    @Override
    public Long releaseCreation(VersionRequestDto releaseCreationDto) {

        LambdaQueryWrapper<ReleaseManagement> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtil.isNotEmpty(releaseCreationDto.getVersion() && StringUtil.isNotEmpty(releaseCreationDto.getVersion() {
            queryWrapper.eq(ReleaseManagement::getVersion, releaseCreationDto.getVersion()
                .eq(ReleaseManagement::getProjectId, releaseCreationDto.getProjectId();
        }
        List&lt;ReleaseManagement> list = versionDao.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(list) {
            throw new BizException(SysConstantEnum.VERSION_HAVE_EXIST.getCode(),
                SysConstantEnum.VERSION_HAVE_EXIST.getValue(), HttpStatus.OK.value();
        }

        ReleaseManagement version = new ReleaseManagement();
        BeanUtil.copyProperties(releaseCreationDto, version);
        versionDao.insert(version);
        return version.getId();
    }

    @Override
    public void releaseModification(VersionRequestDto releaseModification) {
        // Validation on payload with modified record id
        if (releaseModification.getId() == null) {
            throw new BizException(SysConstantEnum.RECORD_ID_NOT_EXIST.getCode(),
                SysConstantEnum.RECORD_ID_NOT_EXIST.getValue(), HttpStatus.BAD_REQUEST.value();
        }

        // get the existing record as per id, version, project id
        VersionDto versionDto = getRecordByIdAndVersion(releaseModification.getId(), releaseModification.getVersion(), releaseModification.getProjectId();
        if (versionDto == null) {
            throw new BizException(SysConstantEnum.VERSION_PROJECT_NOT_MATCH.getCode(),
                SysConstantEnum.VERSION_PROJECT_NOT_MATCH.getValue(), HttpStatus.BAD_REQUEST.value();
        }
        /*
        VersionDto versionDto = getVersion(releaseModification.getId();
        if(versionDto == null
                || !versionDto.getVersion().equals(releaseModification.getVersion() {
            throw new BizException(SysConstantEnum.VERSION_NOT_MATCH.getCode(),
                    SysConstantEnum.VERSION_NOT_MATCH.getValue(), HttpStatus.BAD_REQUEST.value();
        }
        */
        ReleaseManagement db = new ReleaseManagement();
        BeanUtil.copyProperties(releaseModification, db);
        versionDao.updateById(db);

    }

    @Override
    public VersionDto getRecordByIdAndVersion(Long id, String version, Long projectId) {
        QueryWrapper<ReleaseManagement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id).eq("version", version).eq("project_id", projectId);
        ReleaseManagement releaseManagement = versionDao.selectOne(queryWrapper);
        if (releaseManagement == null) {
            return null;
        }
        VersionDto versionDto = new VersionDto();
        BeanUtil.copyProperties(releaseManagement, versionDto);
        return versionDto;
    }


    @Override
    public VersionDto getVersion(Long version) {
        ReleaseManagement v = versionDao.selectById(version);
        VersionDto versionDto = new VersionDto();
        BeanUtil.copyProperties(v, versionDto);
        return versionDto;
    }

    @Override
    public List&lt;VersionDto> getVersionList(VersionRequestDto versionRequestDto) {
        LambdaQueryWrapper<ReleaseManagement> queryWrapper = new LambdaQueryWrapper<>();
        if (versionRequestDto.getProjectId() != null) {
            queryWrapper.eq(ReleaseManagement::getProjectId, versionRequestDto.getProjectId();
        }
        List&lt;ReleaseManagement> list = versionDao.selectList(queryWrapper);
        return BeanUtil.copyToList(list, VersionDto.class);
    }
}
}
