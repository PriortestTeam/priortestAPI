package com.hu.oneclick.server.user;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.Project;
import com.hu.oneclick.model.entity.SubUserProject;
import com.hu.oneclick.model.domain.dto.SubUserDto;

import java.util.List;
import java.util.Map;

/**
 * @author qingyang
 */
public interface SubUserService {

    /**
     * 查询成员列表
     */
    Resp<List<Map<String, Object>>> querySubUsers(int pageNum, int pageSize);

    /**
     * 创建一个成员用户
     *
     * @param sysUser
     * @return
     */
    Resp<String> createSubUser(SubUserDto sysUser);

    /**
     * 修改成员用户
     *
     * @param sysUser
     * @return
     */
    Resp<String> updateSubUser(SubUserDto sysUser);

    /**
     * 设置子用户密码
     *
     * @param sysUser
     * @return
     */
    Resp<String> updateSubUserPassword(SubUserDto sysUser);

    /**
     * 删除子用户
     *
     * @param id
     * @return
     */
    Resp<String> deleteSubUser(String id);


    /**
     * 返回用户的项目列表
     *
     * @Param: [userId]
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2022/2/8
     */
    Resp<SubUserProject> getSubUserProject(String userId);

    /**
     * 返回当前用户的项目列表
     *
     * @return ...
     */
    Resp<List<Project>> getProjectByUserId();
}

