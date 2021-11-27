package com.hu.oneclick.model.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 主账号id 库(MasterIdentifier)实体类
 *
 * @author makejava
 * @since 2021-01-07 10:27:36
 */
@Data
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


}
