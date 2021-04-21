package com.hu.oneclick.model.domain;

import com.alibaba.fastjson.JSONObject;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.model.base.BaseEntity;
import com.hu.oneclick.model.base.VerifyParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 视图表(View)实体类
 *
 * @author makejava
 * @since 2020-12-31 09:33:51
 */
public class View extends BaseEntity implements VerifyParam, Serializable {
    private static final long serialVersionUID = -52199543861564334L;

    /**
     * 关联用户id （主用户）
     */
    private String userId;
    /**
     * 关联项目id
     */
    private String projectId;
    /**
     * view 名称
     */
    private String title;
    /**
     * 使用区域
     */
    private String scope;
    /**
     * 公开还是私有,默认 0 公开
     */
    private Integer isPrivate;
    /**
     * 创建人
     */
    private String owner;
    /**
     * {},自定义json 对象
     */
    private String filter;

    private List<OneFilter> oneFilters;

    /**
     * 修改人
     */
    private String modifyUser;
    /**
     * 修改时间
     */
    private Date modifyDate;

    private Date createTime;

    private Date updateTime;

    private String parentId;

    @Transient
    private String parentTitle;



    @Override
    public void verify() throws BizException {
        if(StringUtils.isEmpty(title)){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"视图名称" + SysConstantEnum.PARAM_EMPTY.getValue());
        }else if(isPrivate == null){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"请选择公开还是私有," + SysConstantEnum.PARAM_EMPTY.getValue());
        }

        verifyOneFilter();
    }

    /**
     * 验证OneFilter 参数格式
     */
    public void verifyOneFilter(){
        if(StringUtils.isEmpty(scope)) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "作用域" + SysConstantEnum.PARAM_EMPTY.getValue());
        }
        if (oneFilters == null || oneFilters.size() == 0){
            return;
        }

        //验证是否有重复
        Set<String> isRedundant = new HashSet<>(oneFilters.size());
        for (OneFilter oneFilter : this.oneFilters) {
            String fieldName = oneFilter.getFieldName();
            if (isRedundant.contains(fieldName)){
                throw new BizException(SysConstantEnum.VIEW_ONN_FILTER_IS_REDUNDANT.getCode(), SysConstantEnum.VIEW_ONN_FILTER_IS_REDUNDANT.getValue());
            }
            isRedundant.add(fieldName);
        }

        //验证参数是否异常
        this.oneFilters.forEach(OneFilter::verify);
        //转换对象为字符串方便存入数据库
        this.filter = JSONObject.toJSONString(this.oneFilters);
    }

    /**
     * 验证用户类型并修改是否公开
     * 默认空，子用户只能查公开
     * @param type
     */
    public void verifyUserType(Integer type){
        if (type != 0 && type != 1){
            //子成员只能查看公开的
            isPrivate = 0;
        }
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Integer getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Integer isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
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

    public List<OneFilter> getOneFilters() {
        return oneFilters;
    }

    public void setOneFilters(List<OneFilter> oneFilters) {
        this.oneFilters = oneFilters;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentTitle() {
        return parentTitle;
    }

    public void setParentTitle(String parentTitle) {
        this.parentTitle = parentTitle;
    }
}
