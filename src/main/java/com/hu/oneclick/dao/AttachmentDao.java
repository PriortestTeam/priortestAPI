package com.hu.oneclick.dao;

import com.hu.oneclick.model.annotation.Page;
import com.hu.oneclick.model.entity.Attachment;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 附件表(Attachment)表数据库访问层
 *
 * @author makejava
 * @since 2020-12-20 20:40:38
 */
public interface AttachmentDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Attachment queryById(@Param("masterId") String masterId, @Param("id") String id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<Attachment> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param attachment 实例对象
     * @return 对象列表
     */
    @Page
    List<Attachment> queryAll(Attachment attachment);

    /**
     * 新增数据
     *
     * @param attachment 实例对象
     * @return 影响行数
     */
    int insert(Attachment attachment);

    /**
     * 修改数据
     *
     * @param attachment 实例对象
     * @return 影响行数
     */
    int update(Attachment attachment);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(@Param("id") String id, @Param("masterId") String masterId);

    List<Map<String,Object>> getUserAttachment(String userId, String areaType);

}
