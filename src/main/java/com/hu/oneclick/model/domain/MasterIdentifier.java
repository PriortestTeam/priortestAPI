package com.hu.oneclick.model.domain;

import java.io.Serializable;

/**
 * 主账号id 库(MasterIdentifier)实体类
 *
 * @author makejava
 * @since 2021-01-07 10:27:36
 */
public class MasterIdentifier implements Serializable {
    private static final long serialVersionUID = 605421839979302170L;
    /**
     * id
     */
    private String id;
    /**
     * 默认 0 1 为已使用
     */
    private Integer flag;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

}
