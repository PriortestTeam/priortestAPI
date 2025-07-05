package com.hu.oneclick.server.service.impl;
import com.hu.oneclick.dao.SysConfigDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.SysConfig;
import com.hu.oneclick.server.service.SysConfigService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 * @ClassName SysConfigServiceImpl.java
 * @Description
 * @Author Vince
 * @CreateTime 2022年12月24日 18:45:00
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class SysConfigServiceImpl implements SysConfigService {
    @NonNull
    private final SysConfigDao sysConfigDao;
    @Override
    public Resp<List<SysConfig>> listByGroup(String scope) {
        return new Resp.Builder<List<SysConfig>>().setData(sysConfigDao.selectByGroup(scope).ok();
    }
}
}
}
