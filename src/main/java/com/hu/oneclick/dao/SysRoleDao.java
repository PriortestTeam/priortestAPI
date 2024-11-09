package com.hu.oneclick.dao;

import com.hu.oneclick.model.entity.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 平台角色表(SysRole)表数据库访问层
 *
 * @author makejava
 * @since 2021-01-06 13:10:06
 */
public interface SysRoleDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    SysRole queryById(@Param("id") String id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<SysRole> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param sysRole 实例对象
     * @return 对象列表
     */
    List<SysRole> queryAll(SysRole sysRole);

    /**
     * 新增数据
     *
     * @param sysRole 实例对象
     * @return 影响行数
     */
    int insert(SysRole sysRole);

    /**
     * 修改数据
     *
     * @param sysRole 实例对象
     * @return 影响行数
     */
    int update(SysRole sysRole);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(@Param("id") String id);

    /** 根据角色名称查角色
     * @Param: [roleName]
     * @return: com.hu.oneclick.model.entity.SysRole
     * @Author: MaSiyi
     * @Date: 2022/1/3
     */
    SysRole queryByRoleName(@Param("roleName")String roleName);
}
