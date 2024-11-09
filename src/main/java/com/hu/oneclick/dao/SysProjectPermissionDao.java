package com.hu.oneclick.dao;

import com.hu.oneclick.model.entity.SysProjectPermission;
import com.hu.oneclick.model.domain.dto.SysProjectPermissionDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 子用户关联的项目权限表(SysProjectPermission)表数据库访问层
 *
 * @author makejava
 * @since 2020-11-20 10:32:51
 */
public interface SysProjectPermissionDao {

    /**
     * userId 查询权限列表
     * @param userId
     * @return
     */
    List<SysProjectPermissionDto> queryBySubUserId(String userId);


    /**
     * 新增数据
     *
     * @param sysProjectPermission 实例对象
     * @return 影响行数
     */
    int insert(SysProjectPermission sysProjectPermission);

    /**
     * 修改数据
     *
     * @param sysProjectPermission 实例对象
     * @return 影响行数
     */
    int update(SysProjectPermission sysProjectPermission);

    /**
     * 通过主键删除数据
     *
     * @param subUserId 主键
     * @return 影响行数
     */
    int deleteBySubUserId(@Param("subUserId") String subUserId);

    /**
     * 创建子用户关联项目的权限
     * @return
     */
    int batchInsert(@Param("sysProjectPermissions")List<SysProjectPermission> sysProjectPermissions);

}
