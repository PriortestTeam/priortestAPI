package com.hu.oneclick.server.service;

import com.hu.oneclick.model.domain.SysUserBusiness;

import java.util.List;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/11/13
 * @since JDK 1.8.0
 */
public interface UserBusinessService {
    List<SysUserBusiness> getBasicData(String userId, String userRole);

    String getUBValueByTypeAndKeyId(String type, String keyId);

    Long checkIsValueExist(String type, String keyId);

    Integer updateBtnStr(String keyId, String type, String btnStr);
}
