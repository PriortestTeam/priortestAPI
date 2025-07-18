package com.hu.oneclick.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hu.oneclick.model.entity.CustomFields;
import com.hu.oneclick.model.domain.dto.CustomFieldDto;
import com.hu.oneclick.model.domain.dto.CustomFieldPossBileDto;
import com.hu.oneclick.model.domain.dto.CustomFieldsDto;
import com.hu.oneclick.model.domain.vo.CustomFileldLinkVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 自定义字段表 Mapper 接口
 * </p>
 *
 * @author vince
 * @since 2022-12-13
 */
public interface CustomFieldsDao extends BaseMapper<CustomFields> {

    int updateByPrimaryKeySelective(CustomFields customFields);

    int deleteBatchByKey(@Param("customFieldIds") Set<Long> customFieldsIds);

    /**
     * 通过实体作为筛选条件查询
     *
     * @param customField 实例对象
     * @return 对象列表
     */
    List<CustomFields> queryCustomList(CustomFields customField);

    List<CustomFileldLinkVo> getAllCustomList(@Param("customFieldDto") CustomFieldDto customFieldDto);

    List<String> getFieldTypeByProjectId(@Param("projectId") Long projectId);

    List<CustomFileldLinkVo> getDropDownBox(@Param("customFieldDto") CustomFieldDto customFieldDto);

    int updateValueDropDownBox(@Param("customFieldsDto") CustomFieldsDto customFieldsDto);

    CustomFields getByCustomFieldId(@Param("customFieldId") Long customFieldId);

    List<CustomFileldLinkVo> getAllCustomListByScopeId(Long scopeId);

   List<CustomFieldPossBileDto> getPossBile(String fieldName);
   
   List<CustomFieldPossBileDto> getPossBileWithProject(@Param("fieldName") String fieldName, @Param("projectId") String projectId);
}
