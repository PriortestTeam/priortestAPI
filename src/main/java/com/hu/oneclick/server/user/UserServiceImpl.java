
package com.hu.oneclick.server.user;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hu.oneclick.dao.SysUserDao;
import com.hu.oneclick.dao.SysUserTokenDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.SysUser;
import com.hu.oneclick.model.entity.SysUserToken;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private SysUserDao sysUserDao;

    @Autowired
    private SysUserTokenDao sysUserTokenDao;

    @Autowired
    private RedissonClient redisClient;

    @Override
    public boolean consumeApiTimes(String token, SysUser sysUser) {
        List<SysUserToken> sysUserTokens = sysUserTokenDao.selectByUserIdAndToken(sysUser.getId(), token);
        if (sysUserTokens.isEmpty()) {
            return false;
        }

        for (SysUserToken sysUserToken : sysUserTokens) {
            if (sysUserToken.getApiTimes() > 0) {
                sysUserTokenDao.decreaseApiTimes(sysUserToken.getId());
            }
        }
        return true;
    }

    @Override
    public List<SysUser> queryByUserIdAndParentId(String masterId) {
        SysUser sysUser = new SysUser();
        sysUser.setId(masterId);
        return sysUserDao.queryAllIdOrParentId(sysUser);
    }

    @Override
    public Resp<String> getUserActivNumber(String email) {
        SysUser sysUser = sysUserDao.queryByEmail(email);
        return new Resp.Builder<String>().setData(String.valueOf(sysUser.getActivitiNumber())).ok();
    }

    @Override
    public Resp<String> verifyLinkString(String linkStr) {
        RBucket<String> bucket = redisClient.getBucket(linkStr);
        String redisCode = bucket.get();

        if (redisCode != null) {
            return new Resp.Builder<String>().setData(redisCode).ok();
        } else {
            return new Resp.Builder<String>().fail();
        }
    }

    @Override
    public Resp<List<Map<String, Object>>> listUserByProjectId(Long projectId) {
        List<Map<String, Object>> list = sysUserDao.listUserByProjectId(projectId);
        return new Resp.Builder<List<Map<String, Object>>>().setData(list).ok();
    }

    @Override
    public Resp<String> getUserAccountInfo(String param1, String param2) {
        // 实现缺失的方法
        return new Resp.Builder<String>().ok();
    }
}
