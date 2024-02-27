package com.hu.oneclick.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hu.oneclick.model.domain.Sprint;
import com.hu.oneclick.model.domain.dto.SprintSaveDto;
import com.hu.oneclick.model.domain.param.SprintParam;

import java.util.List;

/**
 * @author qingyang
 */
public interface SprintService extends IService<Sprint> {


    /**
     * 列表
     *
     * @param param
     * @return
     */
    List<Sprint> list(SprintParam param);

    /**
     * 新增
     *
     * @param dto
     * @return
     */
    Sprint add(SprintSaveDto dto);

    /**
     * 修改
     *
     * @param dto
     * @return
     */
    Sprint edit(SprintSaveDto dto);

    Sprint getByIdAndProjectId(Long id, Long projectId);

    /**
     * 详情
     *
     * @param id
     * @return
     */
    Sprint info(Long id);

    void clone(List<Long> ids);
}
