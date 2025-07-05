package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.dao.SysFunctionDao;
import com.hu.oneclick.model.entity.SysFunction;
import com.hu.oneclick.server.service.FunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/11/13
 * @since JDK 1.8.0
 */
@Service
public class FunctionServiceImpl implements FunctionService {

    @Autowired
    private SysFunctionDao sysFunctionDao;

    @Override
    public List<SysFunction> getRoleFunction(String pNumber) {
        return sysFunctionDao.getRoleFunction(pNumber);
    }

    @Override
    public List<SysFunction> findRoleFunction(String pNumber) {
        return sysFunctionDao.getRoleFunction(pNumber);
    }

    @Override
    public List<SysFunction> findByIds(String functionsIds) {
        List<Long> idList = this.strToLongList(functionsIds);

        return sysFunctionDao.findByIds(idList);
    }

    public List<Long> strToLongList(String strArr) {
        List<Long> idList=new ArrayList<Long>();
        String[] d=strArr.split(",");
        for (int i = 0, size = d.length; i < size; i++) {
            if(d[i]!=null) {
                idList.add(Long.parseLong(d[i]));
            }
        }
        return idList;
    }
}
