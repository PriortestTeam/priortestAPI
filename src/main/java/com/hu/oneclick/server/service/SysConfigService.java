package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SysConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName SysConfigService.java
 * @Description
 * @Author Vince
 * @CreateTime 2022年12月24日 18:45:00
 */
public interface SysConfigService {

    Resp<List<SysConfig>> listByGroup(String scope);
}
