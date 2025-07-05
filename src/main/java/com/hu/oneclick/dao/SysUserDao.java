package com.hu.oneclick.dao;

import com.hu.oneclick.model.annotation.Page;
import com.hu.oneclick.model.entity.SysUser;
import com.hu.oneclick.model.domain.dto.PlatformUserDto;
import com.hu.oneclick.model.domain.dto.SubUserDto;
import com.hu.oneclick.model.domain.dto.SysUserRoleDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * (SysUser)表数据库访问层
 *
 * @author makejava
 * @since 2020-11-14 23:32:43
 */
@Component
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

    List<SysUser> queryAllIdOrParentId(SysUser sysUser);

    /**
     * 根据用户邮箱查询
     *
     * @param email
     * @return
     */
    SysUser queryByEmail(@Param("email") String email);

    /**
     * 查询成员列表
     *
     * @return
     */
    @Page
    List<SubUserDto> querySubUsers(SysUser sysUser);

    /**
     * 查詢子用户信息
     *
     * @param userId
     * @param masterId
     * @return
     */
    SubUserDto querySubUserInfo(@Param("userId") String userId, @Param("masterId") String masterId);


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
     *
     * @param sysUser
     * @return
     */
    int updateSubUser(SysUser sysUser);

    /**
     * 设置子用户密码
     *
     * @param sysUser
     * @return
     */
    int updateSubUserPassword(SubUserDto sysUser);


    /**
     * 删除子用户
     *
     * @param id
     * @return
     */
    int deleteSubUser(@Param("id") String id);

    // query all user name in the same room
    List<SubUserDto> queryNameUsersByRoomId(@Param("roomId") String room_id, @Param("subUserName") String subUserName);

    /**
     * 查詢平台用戶
     *
     * @param platformUserDto
     * @return
     */
    List<PlatformUserDto> queryPlatformUsers(PlatformUserDto platformUserDto);

    /**
     * 更新平台用户
     *
     * @param platformUserDto
     * @return
     */
    int updatePlatformUser(PlatformUserDto platformUserDto);

    /**
     * 根据父id删除
     *
     * @param parentId 父id
     */
    void deleteByParentId(@Param("parentId") String parentId);

    Date getExpireDate(String userId);

    /**
     * 根据用户邮箱模糊查询
     *
     * @param email
     * @return
     */
    List<SysUser> queryByLikeEmail(@Param("email") String email);

    List<SysUserRoleDto> getAccountRole(String roomId, String roleId);

    List<Map<String, Object>> queryUsersByRoomId(@Param("roomId") BigInteger roomId, int pageNum, int pageSize);

    List<Map<String, Object>> listUserByProjectId(@Param("projectId") Long projectId);
}
