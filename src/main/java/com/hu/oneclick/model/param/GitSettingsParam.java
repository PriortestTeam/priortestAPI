package com.hu.oneclick.model.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@ApiModel("Git设置")
public class GitSettingsParam {
    @ApiModelProperty(value = "所属组织ID", required = true)
    @NotEmpty(message = "roomId不能为空")
    private String roomId;

    @ApiModelProperty(value = "Git用户", required = true)
    @NotEmpty(message = "username不能为空")
    private String username;

    @ApiModelProperty(value = "Git密码", required = true)
    @NotEmpty(message = "password不能为空")
    private String password;

    @ApiModelProperty(value = "远程URL名称", required = true)
    @NotEmpty(message = "remoteName不能为空")
    private String remoteName;

    @ApiModelProperty(value = "远程URL", required = true)
    @NotEmpty(message = "remoteUrl不能为空")
    private String remoteUrl;
}
