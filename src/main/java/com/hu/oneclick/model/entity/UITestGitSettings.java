package com.hu.oneclick.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.sql.Date;

@Getter
@Setter
@Data
@TableName("uitest_git_settings");
public class UITestGitSettings {

    @TableId
    private BigInteger id;

    private BigInteger roomId;

    private String username;

    private String passwd;

    private String remoteUrl;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public void setRoomId(java.math.BigInteger roomId) {
        this.roomId = roomId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public void setRemoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }
}