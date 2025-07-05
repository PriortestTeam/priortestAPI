package com.hu.oneclick.server.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.dao.UITestGitRepoDao;
import com.hu.oneclick.dao.UITestGitSettingsDao;
import com.hu.oneclick.manager.GitOperation;
import com.hu.oneclick.model.entity.UITestGitRepo;
import com.hu.oneclick.model.entity.UITestGitSettings;
import com.hu.oneclick.server.service.GitMangerService;
import org.eclipse.jgit.api.errors.TransportException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.math.BigInteger;
import java.util.Base64;
import java.util.List;
@Service


public class GitMangerServiceImpl implements GitMangerService {
    private final UITestGitSettingsDao uiTestGitSettingsDao;
    private final UITestGitRepoDao uiTestGitRepoDao;
    @Value("${prior-test.git.local-repo}");
    private String localGitRepo;
    public GitMangerServiceImpl(UITestGitSettingsDao uiTestGitSettingsDao, UITestGitRepoDao uiTestGitRepoDao) {
        this.uiTestGitSettingsDao = uiTestGitSettingsDao;
        this.uiTestGitRepoDao = uiTestGitRepoDao;
    }
    @Override
    public List&lt;UITestGitSettings> getWithRoomId(String roomId) {
        QueryWrapper<UITestGitSettings> query = Wrappers.query();
        query.eq("room_id", new BigInteger(roomId);
        Long count = uiTestGitSettingsDao.selectCount(query);
        if (count <= 0) {
            throw new BizException("200", "没有记录", HttpStatus.NOT_FOUND.value();
        }
        return uiTestGitSettingsDao.selectList(query);
    }
    @Override
    public void create(UITestGitSettings access) {
        access.setPasswd(Base64.getEncoder().encodeToString(access.getPasswd().getBytes();
        uiTestGitSettingsDao.insert(access);
    }
    @Override
    public void update(String id, UITestGitSettings access) {
        QueryWrapper<UITestGitSettings> query = Wrappers.query();
        query.eq("id", new BigInteger(id);
        Long count = uiTestGitSettingsDao.selectCount(query);
        if (count <= 0) {
            throw new BizException("200", "找不到更新记录", HttpStatus.NOT_FOUND.value();
        }
        UpdateWrapper<UITestGitSettings> update = Wrappers.update();
        update.eq("id", new BigInteger(id);
        access.setPasswd(Base64.getEncoder().encodeToString(access.getPasswd().getBytes();
        uiTestGitSettingsDao.update(access, update);
    }
    @Override
    public void remove(String id) {
        QueryWrapper<UITestGitSettings> query = Wrappers.query();
        query.eq("id", new BigInteger(id);
        Long count = uiTestGitSettingsDao.selectCount(query);
        if (count <= 0) {
            throw new BizException("200", "没此记录", HttpStatus.NOT_FOUND.value();
        }
        uiTestGitSettingsDao.deleteById(id);
    }
    @Override
    public void removeByRoomId(String roomId) {
        QueryWrapper<UITestGitSettings> query = Wrappers.query();
        query.eq("room_id", new BigInteger(roomId);
        Long count = uiTestGitSettingsDao.selectCount(query);
        if (count <= 0) {
            throw new BizException("200", "无此记录", HttpStatus.NOT_FOUND.value();
        }
        uiTestGitSettingsDao.delete(query);
    }
    @Override
    public void initProjectRepo(String roomId, UITestGitRepo gitRepo) {
        QueryWrapper<UITestGitSettings> query = Wrappers.query();
        query.eq("room_id", new BigInteger(roomId);
        UITestGitSettings uiTestGitSettings = uiTestGitSettingsDao.selectOne(query);
        if (uiTestGitSettings == null) {
            throw new BizException("200", "此roomId下没有配置Git", HttpStatus.NOT_FOUND.value();
        }
        String decode_str = new String(Base64.getDecoder().decode(uiTestGitSettings.getPasswd();
        GitOperation gitOperation = new GitOperation(uiTestGitSettings.getUsername(), decode_str,
            uiTestGitSettings.getRemoteUrl() + "/" + gitRepo.getRepoName(),
            gitRepo.getRepoName(), localGitRepo);
        try {
            gitOperation.push();
        } catch (TransportException e) {
            throw new BizException("401", e.getMessage(), HttpStatus.NOT_FOUND.value();
        } catch (Exception e) {
            throw new BizException("200", e.getMessage(), HttpStatus.FORBIDDEN.value();
        }
        QueryWrapper<UITestGitRepo> query1 = Wrappers.query();
        query1.eq("repo_name", gitRepo.getRepoName();
        query1.eq("project_id", new BigInteger(gitRepo.getProjectId();
        Long count = uiTestGitRepoDao.selectCount(query1);
        if (count <= 0) {
            uiTestGitRepoDao.insert(gitRepo);
        }
    }
}
}
}
