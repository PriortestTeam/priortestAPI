package com.hu.oneclick.server.user;

import com.hu.oneclick.dao.SysUserReferenceDao;
import com.hu.oneclick.model.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

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
     * @param startTime
     * @param endTime
     * @Param: [sysUser]
     * @return: int
     * @Author: MaSiyi
     * @Date: 2021/10/15
     */
    @Override
    public int getReferenceTime(SysUser sysUser, Date startTime, Date endTime) {

        return sysUserReferenceDao.getReferenceTime(sysUser.getId(),startTime,endTime);
    }

    /**
     * 引用别人人数
     *
     * @param sysUser
     * @param startTime
     * @param endTime
     * @Param: [sysUser]
     * @return: int
     * @Author: MaSiyi
     * @Date: 2021/10/15
     */
    @Override
    public int getReferencePersonNo(SysUser sysUser, Date startTime, Date endTime) {
        return sysUserReferenceDao.getReferencePersonNo(sysUser.getId(),startTime,endTime);

    }
}
}
}
