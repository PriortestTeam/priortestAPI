package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.SysCustomField;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (SysCustomField)表数据库访问层
 *
 * @author makejava
 * @since 2021-04-11 14:40:14
 */
public interface SysCustomFieldDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    SysCustomField queryById(String id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<SysCustomField> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @return 对象列表
     */
    List<SysCustomField> queryAll();

    SysCustomField queryByFieldName(String fieldName);

    /**
     * 新增数据
     *
     * @param sysCustomField 实例对象
     * @return 影响行数
     */
    int insert(SysCustomField sysCustomField);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<SysCustomField> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<SysCustomField> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<SysCustomField> 实例对象列表
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("entities") List<SysCustomField> entities);

    /**
     * 修改数据
     *
     * @param sysCustomField 实例对象
     * @return 影响行数
     */
    int update(SysCustomField sysCustomField);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(String id);


    List<SysCustomField> getAllSysCustomFieldByScope(String scope);
}
