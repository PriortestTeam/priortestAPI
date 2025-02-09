package com.hu.oneclick.server.service;

import com.hu.oneclick.model.entity.UITestSourceCodeAccess;
import org.springframework.stereotype.Service;

@Service
public interface GitMangerService {
    void create(UITestSourceCodeAccess access);

    void update(String id, UITestSourceCodeAccess access);

    void remove(String id);
}
