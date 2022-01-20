package com.hu.oneclick.model.domain;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * project_sign_off
 * @author 
 */
@ApiModel(value="com.hu.oneclick.model.domain.ProjectSignOff")
@Data
public class ProjectSignOff implements Serializable {
    private String id;

    private String projectId;

    private String userId;

    private Date createTime;

    private String filePath;

    private String fileName;

    private String createUser;

    private static final long serialVersionUID = 1L;
}