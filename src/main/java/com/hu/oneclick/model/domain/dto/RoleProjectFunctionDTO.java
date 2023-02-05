package com.hu.oneclick.model.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author houaixia
 * 权限设置DTO
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleProjectFunctionDTO implements Serializable {

    @NotNull(message = "角色id不能为空")
    @ApiModelProperty(name= "roleId", value = "角色id")
    private Long  roleId;

    @NotBlank(message = "角色名称不能为空")
    @ApiModelProperty(name= "roleName", value = "角色名称")
    private String roleName;

    @NotNull(message = "项目id不能为空")
    @ApiModelProperty(name= "projectId", value = "项目id")
    private Long projectId;

    @NotBlank(message = "项目名称不能为空")
    @ApiModelProperty(name= "projectName", value = "项目名称")
    private String projectName;

    @NotNull(message = "用户id不能为空")
    @ApiModelProperty(name= "userId", value = "用户id")
    private Long userId;

    @NotBlank(message = "用户名称不能为空")
    @ApiModelProperty(name= "userName", value = "用户名称")
    private String userName;

    @ApiModelProperty(name= "value", value = "功能id格式[1][2]")
    private String value;

    @ApiModelProperty(name= "functionList", value = "功能权限")
    private List<FunctionModelDTO> functionList;

}
