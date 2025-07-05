package com.hu.oneclick.model.entity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;
/**
 * sys_user_token
 * @author 
 */
@Schema(description = "用户Token实体")
@Data

public class SysUserToken implements Serializable {
    private Integer id;
    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;
    /**
     * token名称
     */
    @Schema(description = "token名称")
    private String tokenName;
    /**
     * token值
     */
    @Schema(description = "token值")
    private String tokenValue;
    /**
     * 过期时间
     */
    @Schema(description = "过期时间")
    private Date expirationTime;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getTokenName() {
        return tokenName;
    }
    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }
    public String getTokenValue() {
        return tokenValue;
    }
    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }
    public Date getExpirationTime() {
        return expirationTime;
    }
    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;
    public Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    /**
     * 是否删除
     */
    @Schema(description = "是否删除")
    private Boolean isDel;
    public Boolean getDel() {
        return isDel;
    }
    public void setDel(Boolean del) {
        isDel = del;
    }
    /**
     * 状态
     */
    @Schema(description = "状态")
    private Boolean status;
    public Boolean getStatus() {
        return status;
    }
    public void setStatus(Boolean status) {
        this.status = status;
    }
    /**
     * 剩余调用api次数
     */
    @Schema(description = "剩余调用api次数")
    private Long apiTimes;
    public Long getApiTimes() {
        return apiTimes;
    }
    public void setApiTimes(Long apiTimes) {
        this.apiTimes = apiTimes;
    }
    /**
     * 创建人
     */
    @Schema(description="创建人");
    private String createId;
    public String getCreateId() {
        return createId;
    }
    public void setCreateId(String createId) {
        this.createId = createId;
    }
    private static final long serialVersionUID = 1L;
}