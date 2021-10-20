package com.hu.oneclick.server.user;

import com.hu.oneclick.dao.SysUserOrderRecordDao;
import com.hu.oneclick.model.domain.SysUserOrderRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/10/20
 * @since JDK 1.8.0
 */
@Service
public class SysUserOrderRecordImpl implements SysUserOrderRecordService{
    @Autowired
    private SysUserOrderRecordDao sysUserOrderRecordDao;

    /**
     * 增
     *
     * @param sysUserOrderRecord
     * @Param: [sysUserOrderRecord]
     * @return: void
     * @Author: MaSiyi
     * @Date: 2021/10/20
     */
    @Override
    public void insert(SysUserOrderRecord sysUserOrderRecord) {
        sysUserOrderRecordDao.insert(sysUserOrderRecord);
    }
}
