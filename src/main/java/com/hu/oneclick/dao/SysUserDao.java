package com.hu.oneclick.dao;

import com.hu.oneclick.model.annotation.Page;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.dto.PlatformUserDto;
import com.hu.oneclick.model.domain.dto.SubUserDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (SysUser)表数据库访问层
 *
 * @author makejava
 * @since 2020-11-14 23:32:43
 */
public interface SysUserDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    SysUser queryById(@Param("id") String id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<SysUser> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param sysUser 实例对象
     * @return 对象列表
     */
    List<SysUser> queryAll(SysUser sysUser);

    /**
     * 根据用户邮箱查询
     * @param email
     * @return
     */
    SysUser queryByEmail(@Param("email") String email);

    /**
     * 查询成员列表
     * @return
     */
    @Page
    List<SubUserDto> querySubUsers(SysUser sysUser);

    /**
     * 查詢子用户信息
     * @param userId
     * @param masterId
     * @return
     */
    SubUserDto querySubUserInfo(@Param("userId") String userId,@Param("masterId") String masterId);


    /**
     * 新增数据
     *
     * @param sysUser 实例对象
     * @return 影响行数
     */
    int insert(SysUser sysUser);

    /**
     * 修改数据
     *
     * @param sysUser 实例对象
     * @return 影响行数
     */
    int update(SysUser sysUser);

    /**
     * 修改数据
     *
     * @param sysUser 实例对象
     * @return 影响行数
     */
    int updatePassword(SysUser sysUser);
    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(@Param("id") String id);

    /**
     * 修改子用户信息
     * @param sysUser
     * @return
     */
    int updateSubUser(SubUserDto sysUser);

    /**
     * 设置子用户密码
     * @param sysUser
     * @return
     */
    int updateSubUserPassword(SubUserDto sysUser);

    /**
     * 删除子用户
     * @param id
     * @param masterId
     * @return
     */
    int deleteSubUser(@Param("id") String id, @Param("masterId") String masterId);

    List<SubUserDto> queryByNameSubUsers(@Param("masterId") String masterId,@Param("subUserName") String subUserName);

    /**
     * 查詢平台用戶
     * @param platformUserDto
     * @return
     */
    List<PlatformUserDto> queryPlatformUsers(PlatformUserDto platformUserDto);
}
