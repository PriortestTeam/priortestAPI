package

import org.apache.ibatis.annotations.Param;

import java.util.List;

.dao;
        .entity.SysCustomFieldExpand;

/**
 * (SysCustomFieldExpand)表数据库访问层
 *
 * @author makejava
 * @since 2021-04-01 10:57:39
 */
public interface SysCustomFieldExpandDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    SysCustomFieldExpand queryById(@Param("id") String id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<SysCustomFieldExpand> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param sysCustomFieldExpand 实例对象
     * @return 对象列表
     */
    List<SysCustomFieldExpand> queryAll(SysCustomFieldExpand sysCustomFieldExpand);

    /**
     * 新增数据
     *
     * @param sysCustomFieldExpand 实例对象
     * @return 影响行数
     */
    int insert(SysCustomFieldExpand sysCustomFieldExpand);

    /**
     * 修改数据
     *
     * @param sysCustomFieldExpand 实例对象
     * @return 影响行数
     */
    int update(SysCustomFieldExpand sysCustomFieldExpand);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(@Param("id") String id);

}
