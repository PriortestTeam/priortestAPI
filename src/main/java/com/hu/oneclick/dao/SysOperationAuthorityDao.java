package com.hu.oneclick.dao;

import com.hu.oneclick.model.entity.SysOperationAuthority;

import java.util.List;
/**
 * 操作权限(SysOperationAuthority)表数据库访问层
 *
 * @author makejava
 * @since 2021-01-11 10:07:54
 */
public interface SysOperationAuthorityDao {

    List<SysOperationAuthority> queryAll();

}
