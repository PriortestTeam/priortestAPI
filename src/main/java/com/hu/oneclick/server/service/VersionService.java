package com.hu.oneclick.server.service;

import com.hu.oneclick.model.domain.dto.VersionDto;
import com.hu.oneclick.model.domain.dto.VersionRequestDto;

import java.util.List;

public interface VersionService {
    void releaseCreation(VersionRequestDto releaseCreationDto);

    void releaseModification(VersionRequestDto releaseModification);

    VersionDto getRecordByIdAndVersion(Long id, String version, Long projectId);

    VersionDto getVersion(Long version);

    List<VersionDto> getVersionList(VersionRequestDto releaseModification);
}
