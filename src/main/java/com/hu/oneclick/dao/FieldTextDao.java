package com.hu.oneclick.dao;

import com.hu.oneclick.model.entity.FieldText;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 自定义文本字段(FieldText)表数据库访问层
 *
 * @author makejava
 * @since 2020-12-05 20:02:02
 */
public interface FieldTextDao {

    /**
     * 通过ID查询单条数据
     *
     * @param 主键
     * @return 实例对象
     */
    FieldText queryById(@Param("id") String id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<FieldText> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param fieldText 实例对象
     * @return 对象列表
     */
    List<FieldText> queryAll(FieldText fieldText);

    /**
     * 新增数据
     *
     * @param fieldText 实例对象
     * @return 影响行数
     */
    int insert(FieldText fieldText);

    /**
     * 修改数据
     *
     * @param fieldText 实例对象
     * @return 影响行数
     */
    int update(FieldText fieldText);

    /**
     * 通过主键删除数据
     *
     * @param 主键
     * @return 影响行数
     */
    int deleteById(@Param("customFieldId") String customFieldId);

    FieldText queryFieldTextById(@Param("customFieldId")String customFieldId,@Param("masterId") String masterId);
}
