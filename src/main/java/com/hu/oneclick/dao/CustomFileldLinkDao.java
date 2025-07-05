package com.hu.oneclick.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hu.oneclick.model.entity.CustomFileldLink;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 下拉菜单 Mapper 接口
 * </p>
 *
 * @author vince
 * @since 2022-12-14
 */
public interface CustomFileldLinkDao extends BaseMapper<CustomFileldLink> {

    int insertBatch(@Param("customFileldLinkList") List<CustomFileldLink> customFileldLinkList);

    int deleteBatchByCustomFieldId(@Param("customFieldIds")  Set<Long> customFieldIds);

    List<CustomFileldLink> findByCustomFieldIds (@Param("customFieldIds") Set<Long> customFieldIds);
}
