package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SystemConfig;

import java.util.List;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/10/11
 * @since JDK 1.8.0
 */
public interface SystemConfigService {
    /** 增
     * @Param: [systemConfig]
     * @return: com.hu.oneclick.model.base.Resp<com.hu.oneclick.model.domain.TestCase>
     * @Author: MaSiyi
     * @Date: 2021/10/11
     */
    Resp<String> insert(SystemConfig systemConfig);

    /** 改
     * @Param: [systemConfig]
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2021/10/11
     */
    Resp<String> update(SystemConfig systemConfig);

    /** 查
     * @Param: [key]
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2021/10/11
     */
    String getData(String key);

    /** 删
     * @Param: [key]
     * @return: java.lang.String
     * @Author: MaSiyi
     * @Date: 2021/10/11
     */
    String delete(String key);

    /** 根据key和group查询数据
     * @Param: [valueOf]
     * @return: java.lang.String
     * @Author: MaSiyi
     * @Date: 2021/10/15
     */
    String getDateForKeyAndGroup(String key, String group);

    /** 根据group获取key
     * @Param: [group]
     * @return: java.lang.String
     * @Author: MaSiyi
     * @Date: 2021/10/21
     */
    List<String> getKeyForGroup(String group);

    /** 查UI
     * @Param: [key]
     * @return: java.lang.String
     * @Author: MaSiyi
     * @Date: 2021/12/27
     */
    SystemConfig getDataUI(String key);
}
