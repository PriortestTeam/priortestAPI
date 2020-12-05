package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.FieldRadio;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 单选框(FieldRadio)表数据库访问层
 *
 * @author makejava
 * @since 2020-12-04 15:38:40
 */
public interface FieldRadioDao {

    /**
     * 通过ID查询单条数据
     *
     * @param 主键
     * @return 实例对象
     */
    FieldRadio queryById(@Param("id") String id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<FieldRadio> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param fieldRadio 实例对象
     * @return 对象列表
     */
    List<FieldRadio> queryAll(FieldRadio fieldRadio);

    /**
     * 新增数据
     *
     * @param fieldRadio 实例对象
     * @return 影响行数
     */
    int insert(FieldRadio fieldRadio);

    /**
     * 修改数据
     *
     * @param fieldRadio 实例对象
     * @return 影响行数
     */
    int update(FieldRadio fieldRadio);

    /**
     * 通过主键删除数据
     *
     * @param 主键
     * @return 影响行数
     */
    int deleteById(@Param("id") String id);

    FieldRadio queryFieldRadioById(@Param("customFieldId") String customFieldId,@Param("masterId") String masterId);
}
