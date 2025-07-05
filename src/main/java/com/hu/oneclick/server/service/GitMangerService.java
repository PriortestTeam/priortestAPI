package com.hu.oneclick.server.service;

import com.hu.oneclick.model.entity.UITestGitRepo;
import com.hu.oneclick.model.entity.UITestGitSettings;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GitMangerService {
    List<UITestGitSettings> getWithRoomId(String roomId);

    void create(UITestGitSettings access);

    void update(String id, UITestGitSettings access);

    void remove(String id);

    void removeByRoomId(String roomId);

    void initProjectRepo(String roomId, UITestGitRepo gitRepo);
}
