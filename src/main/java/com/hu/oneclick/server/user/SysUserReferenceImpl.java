package com.hu.oneclick.server.user;

import com.hu.oneclick.dao.SysUserReferenceDao;
import com.hu.oneclick.model.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/10/15
 * @since JDK 1.8.0
 */
@Service
public class SysUserReferenceImpl implements SysUserReferenceService {

    @Autowired
    private SysUserReferenceDao sysUserReferenceDao;

    /**
     * 推荐人数
     *
     * @param sysUser
     * @Param: [sysUser]
     * @return: int
     * @Author: MaSiyi
     * @Date: 2021/10/15
     */
    @Override
    public int getReferenceTime(SysUser sysUser) {

        return sysUserReferenceDao.getReferenceTime(sysUser.getId());
    }

    /**
     * 引用别人人数
     *
     * @param sysUser
     * @Param: [sysUser]
     * @return: int
     * @Author: MaSiyi
     * @Date: 2021/10/15
     */
    @Override
    public int getReferencePersonNo(SysUser sysUser) {
        return sysUserReferenceDao.getReferencePersonNo(sysUser.getId());

    }
}
