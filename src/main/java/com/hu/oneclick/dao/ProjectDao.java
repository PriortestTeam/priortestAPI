package com.hu.oneclick.dao;

import com.hu.oneclick.model.annotation.Page;
import com.hu.oneclick.model.domain.Project;
import com.hu.oneclick.model.domain.UserUseOpenProject;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (Project)表数据库访问层
 *
 * @author makejava
 * @since 2020-12-07 20:59:50
 */
public interface ProjectDao {

    /**
     * 通过ID查询单条数据
     *
     * @param 主键
     * @return 实例对象
     */
    Project queryById(@Param("id") String id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<Project> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param project 实例对象
     * @return 对象列表
     */
    @Page
    List<Project> queryAll(Project project);

    /**
     * 新增数据
     *
     * @param project 实例对象
     * @return 影响行数
     */
    int insert(Project project);

    /**
     * 修改数据
     *
     * @param project 实例对象
     * @return 影响行数
     */
    int update(Project project);

    /**
     * 通过主键删除数据
     *
     * @param 主键
     * @return 影响行数
     */
    int deleteById(@Param("id") String id);

    Integer queryByTitle(@Param("masterId")String masterId,@Param("title") String title);

    /**
     * 查询所有的项目
     * @return
     */
    List<Project> queryAllProjects(String masterId);



    /**
     * 查询所有的项目 and 权限
     * @return
     */
    List<Project> queryAllProjectsAndPermission(@Param("masterId")String masterId);

    /**
     * id查询项目 and 权限
     * @return
     */
    Project queryProjectAndPermissionByProjectId(@Param("masterId")String masterId,
                                                 @Param("projectId")String projectId);
    /**
     * 根据project id 列表 查询 and 权限
     * @param projectIds
     * @param masterId
     * @return
     */
    List<Project> queryInProjectIdsAndPermission(@Param("projectIds") List<String> projectIds, @Param("masterId") String masterId);



    //用户操作已打开的项目

    /**
     * 查询用户已打开的项目
     * @param userId
     * @param userId
     * @return
     */
    UserUseOpenProject queryUseOpenProject(@Param("userId") String userId);


    int insertUseOpenProject(UserUseOpenProject userUseOpenProject);


    int deleteUseOpenProject(@Param("id") String id);


}
