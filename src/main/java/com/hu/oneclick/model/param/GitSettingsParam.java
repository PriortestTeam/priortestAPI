package com.hu.oneclick.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;

@Data
@Schema(description = "Git设置")
public class GitSettingsParam {
    @Schema(description = "所属组织ID", required = true)
    @NotEmpty(message = "roomId不能为空")
    private String roomId;

    @Schema(description = "Git用户", required = true)
    @NotEmpty(message = "username不能为空")
    private String username;

    @Schema(description = "Git密码", required = true)
    @NotEmpty(message = "password不能为空")
    private String password;

    @Schema(description = "远程URL", required = true)
    @NotEmpty(message = "remoteUrl不能为空")
    private String remoteUrl;
}
