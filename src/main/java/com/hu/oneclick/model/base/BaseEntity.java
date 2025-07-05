package com.hu.oneclick.model.base;

import com.hu.oneclick.common.util.SnowFlakeUtil;

/**
 * @author qingyang
 */


public class BaseEntity {

    private  String id = String.valueOf(SnowFlakeUtil.getFlowIdInstance().nextId();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
}
