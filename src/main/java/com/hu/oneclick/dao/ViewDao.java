package com.hu.oneclick.dao;

import com.hu.oneclick.model.annotation.Page;
import com.hu.oneclick.model.domain.View;
import com.hu.oneclick.model.domain.dto.ViewTreeDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 视图表(View)表数据库访问层
 *
 * @author makejava
 * @since 2020-12-31 09:33:51
 */
public interface ViewDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    View queryById(@Param("id") String id,@Param("masterId") String masterId);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<View> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param view 实例对象
     * @return 对象列表
     */
    @Page
    List<View> queryAll(View view);

    /**
     * 新增数据
     *
     * @param view 实例对象
     * @return 影响行数
     */
    int insert(View view);

    /**
     * 修改数据
     *
     * @param view 实例对象
     * @return 影响行数
     */
    int update(View view);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(@Param("masterId") String masterId,@Param("id") String id);

    /**
     * 查询项目是否存在
     * @param masterId
     * @param title
     * @param projectId
     * @param scope
     * @return
     */
    int queryTitleIsExist(@Param("masterId") String masterId,
                              @Param("title") String title,
                              @Param("projectId") String projectId,
                              @Param("scope")String scope);

    List<View> queryViewParents(@Param("masterId") String masterId,
                                @Param("scope") String scope,
                                @Param("projectId") String projectId);

    String queryTitleByParentId(@Param("parentId") String parentId);

    List<ViewTreeDto> queryViewByScopeAll(@Param("masterId") String masterId,
                                          @Param("projectId") String projectId,
                                          @Param("scope") String scope);

    List<ViewTreeDto> queryViewTreeById(@Param("masterId") String masterId,@Param("viewId") String viewId);

    List<Object> sql(@Param("sqlen")String sqlen);

    /** 通过ID查询单条数据
     * @Param: [id, masterId]
     * @return: com.hu.oneclick.model.domain.View
     * @Author: MaSiyi
     * @Date: 2021/12/22
     */
    View queryOnlyById(@Param("id") String id);
}
