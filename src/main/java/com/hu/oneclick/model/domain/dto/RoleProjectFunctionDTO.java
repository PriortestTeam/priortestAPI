package com.hu.oneclick.model.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author houaixia
 * 权限设置DTO
 */
@Schema(description = "角色项目功能DTO")
@Data
public class RoleProjectFunctionDTO {

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "项目ID")
    private Long projectId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "功能列表")
    private java.util.List<String> functionList;

    @NotNull(message = "角色id不能为空")
    @Schema(description = "角色id")
    private Long  roleId;

    @NotBlank(message = "角色名称不能为空")
    @Schema(description = "角色名称")
    private String roleName;

    @NotNull(message = "项目id不能为空")
    @Schema(description = "项目id")
    private Long projectId;

    @NotBlank(message = "项目名称不能为空")
    @Schema(description = "项目名称")
    private String projectName;

    @NotNull(message = "用户id不能为空")
    @Schema(description = "用户id")
    private Long userId;

    @NotBlank(message = "用户名称不能为空")
    @Schema(description = "用户名称")
    private String userName;

    @Schema(description = "功能id格式[1][2]")
    private String value;

    @Schema(description = "功能权限")
    private List<FunctionModelDTO> functionList;

    @Schema(description = "角色ID")
    private String roleIdStr;

    @Schema(description = "项目ID")
    private String projectIdStr;
}