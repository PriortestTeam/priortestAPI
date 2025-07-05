package com.hu.oneclick.dao;

import com.hu.oneclick.model.entity.SysUserOrderRecord;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SysUserOrderRecordDao {
    int deleteByPrimaryKey(Integer id);

    int insert(SysUserOrderRecord record);

    int insertSelective(SysUserOrderRecord record);

    SysUserOrderRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysUserOrderRecord record);

    int updateByPrimaryKey(SysUserOrderRecord record);

    void payment(String id);

    List<SysUserOrderRecord> getUserOrderRecord(String userId);

    SysUserOrderRecord getOrderRecordForDate(Date startTime, Date endTime, Long orderId);
}
