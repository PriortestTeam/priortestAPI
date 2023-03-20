package com.hu.oneclick.common.security.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class FillMetaObjectHandler implements MetaObjectHandler {

    @Resource
    @Lazy
    private JwtUserServiceImpl jwtUserService;

    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("createUserId", Long.valueOf(jwtUserService.getMasterId()), metaObject);
        //this.setFieldValByName("createUserName", SecurityUtils.getUserNameOrDefault(), metaObject);
        this.setFieldValByName("updateUserId", Long.valueOf(jwtUserService.getMasterId()), metaObject);
        //this.setFieldValByName("updateUserName", SecurityUtils.getUserNameOrDefault(), metaObject);
        this.setFieldValByName("createTime", Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()), metaObject);
        this.setFieldValByName("updateTime", Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()), metaObject);
        //this.setFieldValByName("isDelete", DeleteStatus.VALID.ordinal(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateUserId", Long.valueOf(jwtUserService.getMasterId()), metaObject);
        //this.setFieldValByName("updateUserName", SecurityUtils.getUserNameOrDefault(), metaObject);
        this.setFieldValByName("updateTime", Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()), metaObject);
    }
}
