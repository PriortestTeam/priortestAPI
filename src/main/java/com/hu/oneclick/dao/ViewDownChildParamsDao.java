package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.ViewDownChildParams;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * (ViewDownChildParams)表数据库访问层
 *
 * @author makejava
 * @since 2021-04-16 16:25:22
 */
public interface ViewDownChildParamsDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    ViewDownChildParams queryById(@Param("id") String id);

    ViewDownChildParams queryByScope(@Param("scope") String scope);
    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<ViewDownChildParams> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param viewDownChildParams 实例对象
     * @return 对象列表
     */
    List<ViewDownChildParams> queryList(ViewDownChildParams viewDownChildParams);

    /**
     * 新增数据
     *
     * @param viewDownChildParams 实例对象
     * @return 影响行数
     */
    int insert(ViewDownChildParams viewDownChildParams);

    /**
     * 修改数据
     *
     * @param viewDownChildParams 实例对象
     * @return 影响行数
     */
    int update(ViewDownChildParams viewDownChildParams);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(@Param("id") String id);

}
