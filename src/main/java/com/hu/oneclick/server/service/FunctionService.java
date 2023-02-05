package com.hu.oneclick.server.service;

import com.hu.oneclick.model.domain.SysFunction;

import java.util.List;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/11/13
 * @since JDK 1.8.0
 */
public interface FunctionService {
    List<SysFunction> getRoleFunction(String pNumber);

    List<SysFunction> findRoleFunction(String s);

    List<SysFunction> findByIds(String funIds);
}
