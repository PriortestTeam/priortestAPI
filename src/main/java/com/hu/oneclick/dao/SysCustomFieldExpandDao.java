package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.SysCustomFieldExpand;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (SysCustomFieldExpand)表数据库访问层
 *
 * @author makejava
 * @since 2021-04-11 15:04:59
 */
public interface SysCustomFieldExpandDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    SysCustomFieldExpand queryById(String id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<SysCustomFieldExpand> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param sysCustomFieldExpand 实例对象
     * @return 对象列表
     */
    List<SysCustomFieldExpand> queryList(SysCustomFieldExpand sysCustomFieldExpand);

    SysCustomFieldExpand queryByUserIdAndFieldName(@Param("fieldName") String fieldName,
                                                   @Param("masterId") String masterId,
                                                   @Param("projectId") String projectId);
    /**
     * 新增数据
     *
     * @param sysCustomFieldExpand 实例对象
     * @return 影响行数
     */
    int insert(SysCustomFieldExpand sysCustomFieldExpand);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<SysCustomFieldExpand> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<SysCustomFieldExpand> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<SysCustomFieldExpand> 实例对象列表
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("entities") List<SysCustomFieldExpand> entities);

    /**
     * 修改数据
     *
     * @param sysCustomFieldExpand 实例对象
     * @return 影响行数
     */
    int update(SysCustomFieldExpand sysCustomFieldExpand);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(String id);

    /** 获取用户自己添加的自定义系统字段
     * @Param: [userId, projectId]
     * @return: java.util.List<com.hu.oneclick.model.domain.SysCustomFieldExpand>
     * @Author: MaSiyi
     * @Date: 2021/12/30
     */
    List<SysCustomFieldExpand> getAllSysCustomFieldExpand(String userId, String projectId);
}
