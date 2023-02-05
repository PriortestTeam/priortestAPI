package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.SubUserProject;
import com.hu.oneclick.model.domain.dto.SubUserDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (SubUserProject)表数据库访问层
 *
 * @author makejava
 * @since 2020-12-09 21:23:07
 */
public interface SubUserProjectDao {

    SubUserProject queryByUserId(String userId);



    /**
     * 新增数据
     *
     * @param subUserProject 实例对象
     * @return 影响行数
     */
    int insert(SubUserProject subUserProject);

    /**
     * 修改数据
     *
     * @param subUserProject 实例对象
     * @return 影响行数
     */
    int update(SubUserProject subUserProject);

    int deleteByUserId(@Param("userId") String userId);

    List<String> selectTitlesByUserId(@Param("userId") String userId,@Param("projectIds") String[] projectIds);
}
