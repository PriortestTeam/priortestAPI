package com.hu.oneclick.model.domain.dto;

import lombok.Data;

/**
 * @author 王富贵
 * @version 1.0.0 2021/9/17
 * @since JDK 1.8.0
 */
@Data
public class SignOffDto {
    /**
     *项目
     */
    private String projectId;
    /**
     *测试环境
     */
    private String env;
    /**
     * 发布版本
     */
    private String version;
    /**
     * 测试周期
     */
    private String testCycle;
    /**
     * 缺陷
     */
    private String issue;
    /**
     * 签名
     */
    private String fileUrl;

}
