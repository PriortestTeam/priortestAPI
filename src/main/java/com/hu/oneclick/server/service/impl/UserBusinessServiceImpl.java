package com.hu.oneclick.server.service.impl;
import com.hu.oneclick.dao.SysUserBusinessDao;
import com.hu.oneclick.model.entity.SysUserBusiness;
import com.hu.oneclick.server.service.UserBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 * @author MaSiyi
 * @version 1.0.0 2021/11/13
 * @since JDK 1.8.0
 */
@Service

public class UserBusinessServiceImpl implements UserBusinessService {
    @Autowired
    private SysUserBusinessDao sysUserBusinessDao;
    @Override
    public List&lt;SysUserBusiness> getBasicData(String userId, String type) {
        return sysUserBusinessDao.checkIsValueExist(type, userId);
    }
    @Override
    public String getUBValueByTypeAndKeyId(String type, String keyId) {
        String ubValue = "";
        List&lt;SysUserBusiness> ubList = getBasicData(keyId, type);
        if(ubList!=null && ubList.size()>0) {
            ubValue = ubList.get(0).getValue();
        }
        return ubValue;
    }
    @Override
    public Long checkIsValueExist(String type, String keyId) {
        List&lt;SysUserBusiness> userBusinesses = sysUserBusinessDao.checkIsValueExist(type, keyId);
        Long id = null;
        if (userBusinesses != null && userBusinesses.size() > 0) {
            id = userBusinesses.get(0).getId();
        }
        return id;
    }
    @Override
    public Integer updateBtnStr(String keyId, String type, String btnStr) {
        return sysUserBusinessDao.updateByExampleSelective(keyId, type, btnStr);
    }
    @Override
    public SysUserBusiness getRoleProjectFunction(Long roleId, Long projectId, Long userId) {
        return  sysUserBusinessDao.findByRoleIdAndProjectIdAndUserId(roleId,projectId,userId);
    }
    @Override
    public int updateByPrimaryKey(SysUserBusiness record) {
        return sysUserBusinessDao.updateByPrimaryKeySelective(record);
    }
    @Override
    public int insert(SysUserBusiness record) {
        return sysUserBusinessDao.insertSelective(record);
    }
}
}
}
