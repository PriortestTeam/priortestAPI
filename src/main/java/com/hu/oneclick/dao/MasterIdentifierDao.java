package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.MasterIdentifier;
import org.apache.ibatis.annotations.Param;



/**
 * 主账号id 库(MasterIdentifier)表数据库访问层
 *
 * @author makejava
 * @since 2021-01-07 10:27:37
 */
public interface MasterIdentifierDao {

    MasterIdentifier queryOne();


    /**
     * 修改数据
     *
     * @param id 实例对象
     * @return 影响行数
     */
    int update(@Param("id") String id );


}
