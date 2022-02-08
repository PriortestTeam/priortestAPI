package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.CustomField;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 自定义字段表(CustomFieldDto)表数据库访问层
 *
 * @author makejava
 * @since 2020-12-04 15:35:48
 */
public interface CustomFieldDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    CustomField queryById(@Param("id") String id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<CustomField> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param customField 实例对象
     * @return 对象列表
     */
    List<CustomField> queryAll(CustomField customField);

    /**
     * 新增数据
     *
     * @param customField 实例对象
     * @return 影响行数
     */
    int insert(CustomField customField);

    /**
     * 修改数据
     *
     * @param customField 实例对象
     * @return 影响行数
     */
    int update(CustomField customField);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(@Param("id") String id,@Param("userId") String userId);

    /**
     * 查询是否已存在title
     * @param masterId
     * @param title
     * @param projectId
     * @return
     */
    int queryByFieldName(@Param("masterId")String masterId, @Param("fieldName")String fieldName, @Param("projectId")String projectId);
}
