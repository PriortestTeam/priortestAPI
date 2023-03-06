package com.hu.oneclick.common.security.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class FillMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        //this.setFieldValByName("createUserId", SecurityUtils.getUserIdOrDefault(), metaObject);
        //this.setFieldValByName("createUserName", SecurityUtils.getUserNameOrDefault(), metaObject);
        //this.setFieldValByName("updateUserId", SecurityUtils.getUserIdOrDefault(), metaObject);
        //this.setFieldValByName("updateUserName", SecurityUtils.getUserNameOrDefault(), metaObject);
        this.setFieldValByName("createTime", Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()), metaObject);
        this.setFieldValByName("updateTime", Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()), metaObject);
        //this.setFieldValByName("isDelete", DeleteStatus.VALID.ordinal(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        //this.setFieldValByName("updateUserId", SecurityUtils.getUserIdOrDefault(), metaObject);
        //this.setFieldValByName("updateUserName", SecurityUtils.getUserNameOrDefault(), metaObject);
        this.setFieldValByName("updateTime", Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()), metaObject);
    }
}
