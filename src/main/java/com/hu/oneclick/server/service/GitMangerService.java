package com.hu.oneclick.server.service;

import com.hu.oneclick.model.entity.UITestSourceCodeAccess;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GitMangerService {
    List<UITestSourceCodeAccess> getWithRoomId(String roomId);

    void create(UITestSourceCodeAccess access);

    void update(String id, UITestSourceCodeAccess access);

    void remove(String id);

    void removeByRoomId(String roomId);
}
