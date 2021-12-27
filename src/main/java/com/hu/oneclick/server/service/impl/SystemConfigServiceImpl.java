package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.dao.SystemConfigDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SystemConfig;
import com.hu.oneclick.server.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/10/11
 * @since JDK 1.8.0
 */
@Service
public class SystemConfigServiceImpl implements SystemConfigService {

    @Autowired
    private SystemConfigDao systemConfigDao;

    /**
     * 增
     *
     * @param systemConfig
     * @Param: [systemConfig]
     * @return: com.hu.oneclick.model.base.Resp<com.hu.oneclick.model.domain.TestCase>
     * @Author: MaSiyi
     * @Date: 2021/10/11
     */
    @Override
    public Resp<String> insert(SystemConfig systemConfig) {
        if (systemConfigDao.insert(systemConfig) == 0) {
            return new Resp.Builder<String>().buildResult(SysConstantEnum.ADD_FAILED.getCode(), SysConstantEnum.ADD_FAILED.getValue());
        }
        return new Resp.Builder<String>().buildResult(SysConstantEnum.ADD_SUCCESS.getCode(), SysConstantEnum.ADD_SUCCESS.getValue());
    }

    /**
     * 改
     *
     * @param systemConfig
     * @Param: [systemConfig]
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2021/10/11
     */
    @Override
    public Resp<String> update(SystemConfig systemConfig) {
        if (systemConfigDao.updateByKey(systemConfig) == 0) {
            return new Resp.Builder<String>().buildResult(SysConstantEnum.UPDATE_FAILED.getCode(), SysConstantEnum.UPDATE_FAILED.getValue());
        }
        return new Resp.Builder<String>().buildResult(SysConstantEnum.UPDATE_SUCCESS.getCode(), SysConstantEnum.UPDATE_SUCCESS.getValue());
    }

    /**
     * 查
     *
     * @param key
     * @Param: [key]
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2021/10/11
     */
    @Override
    public String getData(String key) {
        return systemConfigDao.getDate(key);
    }

    /**
     * 删
     *
     * @param key
     * @Param: [key]
     * @return: java.lang.String
     * @Author: MaSiyi
     * @Date: 2021/10/11
     */
    @Override
    public String delete(String key) {
        if (systemConfigDao.deleteByKey(key) == 0) {
            throw new BizException(SysConstantEnum.DELETE_FAILED.getCode(),SysConstantEnum.DELETE_FAILED.getValue());
        }
        return SysConstantEnum.DELETE_SUCCESS.getValue();
    }

    /**
     * 根据key和group查询数据
     *
     * @param key
     * @param group
     * @Param: [valueOf]
     * @return: java.lang.String
     * @Author: MaSiyi
     * @Date: 2021/10/15
     */
    @Override
    public String getDateForKeyAndGroup(String key, String group) {
        return systemConfigDao.getDateForKeyAndGroup(key,group);
    }

    /**
     * 根据group获取key
     *
     * @param group
     * @Param: [group]
     * @return: java.lang.String
     * @Author: MaSiyi
     * @Date: 2021/10/21
     */
    @Override
    public List<String> getKeyForGroup(String group) {
        return systemConfigDao.getKeyForGroup(group);
    }


    /** 查UI
     * @Param: [key]
     * @return: java.lang.String
     * @Author: MaSiyi
     * @Date: 2021/12/27
     */
    @Override
    public SystemConfig getDataUI(String key) {
        return systemConfigDao.getDataUI(key);
    }

    /**
     * 查所有ui
     *
     * @Param: []
     * @return: java.util.List<com.hu.oneclick.model.domain.SystemConfig>
     * @Author: MaSiyi
     * @Date: 2021/12/27
     */
    @Override
    public List<SystemConfig> getAllUi() {

        return systemConfigDao.getAllUi();
    }
}

