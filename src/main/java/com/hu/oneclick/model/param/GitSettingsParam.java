package com.hu.oneclick.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Git设置参数");


public class GitSettingsParam {

    @Schema(description = "房间ID");
    private String roomId;

    @Schema(description = "用户名");
    private String username;

    @Schema(description = "密码");
    private String password;

    @Schema(description = "远程仓库地址");
    private String remoteUrl;

    private static final long serialVersionUID = 1L;

    public String getRoomId() {
        return roomId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }
}