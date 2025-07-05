package com.hu.oneclick.model.entity;
import com.hu.oneclick.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;
/**
 * (SysUser)实体类
 *
 * @author makejava
 * @since 2020-11-14 23:32:42
 */
@Data
@EqualsAndHashCode(callSuper=false);
public class SysUser extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -67242942971294342L;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 密码
     */
    private String password;
    /**
     * 用户头像地址
     */
    private String photo;
    /**
     * 用户状态 1 启用 3 停止
     */
    private Integer status;
    /**
     * 用户注册时间
     */
    private Date registerDate;
    /**
     * 联系方式
     */
    private String contactNo;
    /**
     * 用户所在企业
     */
    private String company;
    /**
     * 职业
     */
    private String profession;
    /**
     * 行业
     */
    private String industry;
    /**
     * （最近一次关闭账户日期，暂不使用）
     */
    private Date closeDate;
    /**
     * 激活状态， 1 试用中 3 试用过期 2 激活成功 4 激活失败 5 待激活
     */
    private Integer activeState;
    /**
     * 是否启用用户 1启用 3 停用 默认1
     */
    private Integer enable;
    /**
     * 用户注册的时间区域，默认中国，前期中国
     */
    private String timeZone;
    /**
     * 用户注册国家，默认中国，前期中国
     */
    private String locale;
    /**
     * 是否是管理员， 默认为 0, 1 是管理员（负责后台系统的登录）3 子成员
     */
    private Integer manager;
    /**
     * 激活时间
     */
    private Date activitiDate;
    /**
     * 激活次数
     */
    private Integer activitiNumber;
    /**
     * 过期时间
     */
    private Date expireDate;
    /**
     * 0 存在，1 删除
     */
    private Integer delFlag;
    private Date createTime;
    private Date updateTime;
    private Integer sysRoleId;
    /**
     * 用户是否有打开的项目，默认 0 没有
     */
    private int isUseProject = 0;
    private UserUseOpenProject userUseOpenProject;
    private Object userDefaultProject;
    /**
     * VIP,Classic,Trialer 用户身份类型
     */
    private String userClass;
    private Long roomId;
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPhoto() {
        return photo;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public Date getRegisterDate() {
        return registerDate;
    }
    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }
    public String getContactNo() {
        return contactNo;
    }
    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }
    public String getCompany() {
        return company;
    }
    public void setCompany(String company) {
        this.company = company;
    }
    public String getProfession() {
        return profession;
    }
    public void setProfession(String profession) {
        this.profession = profession;
    }
    public String getIndustry() {
        return industry;
    }
    public void setIndustry(String industry) {
        this.industry = industry;
    }
    public Date getCloseDate() {
        return closeDate;
    }
    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }
    public Integer getActiveState() {
        return activeState;
    }
    public void setActiveState(Integer activeState) {
        this.activeState = activeState;
    }
    public Integer getEnable() {
        return enable;
    }
    public void setEnable(Integer enable) {
        this.enable = enable;
    }
    public String getTimeZone() {
        return timeZone;
    }
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
    public String getLocale() {
        return locale;
    }
    public void setLocale(String locale) {
        this.locale = locale;
    }
    public Integer getManager() {
        return manager;
    }
    public void setManager(Integer manager) {
        this.manager = manager;
    }
    public Date getActiviDate() {
        return activitiDate;
    }
    public void setActiviDate(Date activitiDate) {
        this.activitiDate = activitiDate;
    }
    public Integer getActiviNumber() {
        return activitiNumber;
    }
    public void setActiviNumber(Integer activitiNumber) {
        this.activitiNumber = activitiNumber;
    }
    public Date getExpireDate() {
        return expireDate;
    }
    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }
    public Integer getDelFlag() {
        return delFlag;
    }
    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }
    public Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public Date getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    public Integer getSysRoleId() {
        return sysRoleId;
    }
    public void setSysRoleId(Integer sysRoleId) {
        this.sysRoleId = sysRoleId;
    }
    public int getIsUseProject() {
        return isUseProject;
    }
    public void setIsUseProject(int isUseProject) {
        this.isUseProject = isUseProject;
    }
    public UserUseOpenProject getUserUseOpenProject() {
        return userUseOpenProject;
    }
    public void setUserUseOpenProject(UserUseOpenProject userUseOpenProject) {
        this.userUseOpenProject = userUseOpenProject;
    }
    public Object getUserDefaultProject() {
        return userDefaultProject;
    }
    public void setUserDefaultProject(Object userDefaultProject) {
        this.userDefaultProject = userDefaultProject;
    }
    public String getUserClass() {
        return userClass;
    }
    public void setUserClass(String userClass) {
        this.userClass = userClass;
    }
    public Long getRoomId() {
        return roomId;
    }
    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }
}