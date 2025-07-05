package com.hu.oneclick.model.domain.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;
@Data
@Schema(description = "角色项目功能DTO")

public class RoleProjectFunctionDTO {
    @Schema(description = "角色ID")
    private Long roleId;
    @Schema(description = "项目ID")
    private Long projectId;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "功能列表")
    private List&lt;FunctionModelDTO> functionList;
    @Schema(description = "角色id")
    private Long  roleId;
    @Schema(description = "角色名称")
    private String roleName;
    @Schema(description = "项目id")
    private Long projectId;
    @Schema(description = "项目名称")
    private String projectName;
    @Schema(description = "用户id")
    private Long userId;
    @Schema(description = "用户名称")
    private String userName;
    @Schema(description = "功能id格式[1][2]")
    private String value;
    @Schema(description = "功能权限")
    private List&lt;FunctionModelDTO> functionList;
    @Schema(description = "角色ID")
    private String roleIdStr;
    @Schema(description = "项目ID")
    private String projectIdStr;
    public Long getRoleId() {
        return roleId;
    }
    public Long getProjectId() {
        return projectId;
    }
    public Long getUserId() {
        return userId;
    }
    // 修复类型匹配问题
    public List&lt;FunctionModelDTO> getFunctionList() {
        return functionList;
    }
    public String getRoleName() {
        return roleName;
    }
    public String getProjectName() {
        return projectName;
    }
    public String getUserName() {
        return userName;
    }
    public String getValue() {
        return value;
    }
    public String getRoleIdStr() {
        return roleIdStr;
    }
    public String getProjectIdStr() {
        return projectIdStr;
    }
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public void setFunctionList(List&lt;FunctionModelDTO> functionList) {
        this.functionList = functionList;
    }
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public void setRoleIdStr(String roleIdStr) {
        this.roleIdStr = roleIdStr;
    }
    public void setProjectIdStr(String projectIdStr) {
        this.projectIdStr = projectIdStr;
    }
    public List&lt;FunctionModelDTO> getFunctionList1() {
        return functionList;
    }
    public void setFunctionList1(List&lt;FunctionModelDTO> functionList) {
        this.functionList = functionList;
    }
}