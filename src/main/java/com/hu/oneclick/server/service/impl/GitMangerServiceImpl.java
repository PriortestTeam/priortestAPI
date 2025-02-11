package com.hu.oneclick.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.dao.UITestSourceCodeAccessDao;
import com.hu.oneclick.model.entity.UITestSourceCodeAccess;
import com.hu.oneclick.server.service.GitMangerService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class GitMangerServiceImpl implements GitMangerService {
    private final UITestSourceCodeAccessDao uiTestSourceCodeAccessDao;

    public GitMangerServiceImpl(UITestSourceCodeAccessDao uiTestSourceCodeAccessDao) {
        this.uiTestSourceCodeAccessDao = uiTestSourceCodeAccessDao;
    }

    @Override
    public List<UITestSourceCodeAccess> getWithRoomId(String roomId) {
        QueryWrapper<UITestSourceCodeAccess> query = Wrappers.query();
        query.eq("room_id", new BigInteger(roomId));

        Long count = uiTestSourceCodeAccessDao.selectCount(query);
        if (count <= 0) {
            throw new BizException("200", "没有记录", HttpStatus.NOT_FOUND.value());
        }

        return uiTestSourceCodeAccessDao.selectList(query);
    }

    @Override
    public void create(UITestSourceCodeAccess access) {
        QueryWrapper<UITestSourceCodeAccess> query = Wrappers.query();
        query.eq("remote_name", access.getRemoteName());

        Long count = uiTestSourceCodeAccessDao.selectCount(query);
        if (count > 0) {
            throw new BizException("200", "Git远程地址命名已存在", HttpStatus.NOT_ACCEPTABLE.value());
        }

        uiTestSourceCodeAccessDao.insert(access);
    }

    @Override
    public void update(String id, UITestSourceCodeAccess access) {
        QueryWrapper<UITestSourceCodeAccess> query = Wrappers.query();
        query.eq("id", new BigInteger(id));

        Long count = uiTestSourceCodeAccessDao.selectCount(query);
        if (count <= 0) {
            throw new BizException("200", "找不到更新记录", HttpStatus.NOT_FOUND.value());
        }

        UpdateWrapper<UITestSourceCodeAccess> update = Wrappers.update();
        update.eq("id", new BigInteger(id));

        uiTestSourceCodeAccessDao.update(access, update);
    }

    @Override
    public void remove(String id) {
        QueryWrapper<UITestSourceCodeAccess> query = Wrappers.query();
        query.eq("id", new BigInteger(id));

        Long count = uiTestSourceCodeAccessDao.selectCount(query);
        if (count <= 0) {
            throw new BizException("200", "没此记录", HttpStatus.NOT_FOUND.value());
        }

        uiTestSourceCodeAccessDao.deleteById(id);
    }

    @Override
    public void removeByRoomId(String roomId) {
        QueryWrapper<UITestSourceCodeAccess> query = Wrappers.query();
        query.eq("room_id", new BigInteger(roomId));

        Long count = uiTestSourceCodeAccessDao.selectCount(query);
        if (count <= 0) {
            throw new BizException("200", "无此记录", HttpStatus.NOT_FOUND.value());
        }

        uiTestSourceCodeAccessDao.delete(query);
    }
}
