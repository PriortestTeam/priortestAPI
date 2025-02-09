package com.hu.oneclick.model.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@ApiModel("Git设置")
public class GitSettingsParam {
    @ApiModelProperty(value = "Git用户", required = true)
    @NotEmpty
    private String username;

    @ApiModelProperty(value = "Git密码", required = true)
    @NotEmpty
    private String password;

    @ApiModelProperty(value = "远程URL名称", required = true)
    @NotEmpty
    private String remoteName;

    @ApiModelProperty(value = "远程URL", required = true)
    @NotEmpty
    private String remoteUrl;
}
