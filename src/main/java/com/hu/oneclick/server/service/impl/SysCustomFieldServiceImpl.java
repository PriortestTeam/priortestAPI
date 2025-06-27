package com.hu.oneclick.server.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.google.common.base.CaseFormat;
import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.util.SnowFlakeUtil;
import com.hu.oneclick.dao.SysCustomFieldDao;
import com.hu.oneclick.dao.SysCustomFieldExpandDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.entity.SysCustomField;
import com.hu.oneclick.model.entity.SysCustomFieldExpand;
import com.hu.oneclick.model.entity.SysUser;
import com.hu.oneclick.model.domain.dto.SysCustomFieldVo;
import com.hu.oneclick.server.service.SysCustomFieldService;
import com.hu.oneclick.server.user.UserService;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SysCustomFieldServiceImpl implements SysCustomFieldService {

    private final static Logger logger = LoggerFactory.getLogger(SysCustomFieldServiceImpl.class);

    @Autowired
    private  JwtUserServiceImpl jwtUserService;

    @Autowired
    private  RedissonClient redisClient;

    @Autowired
    private  SysCustomFieldDao sysCustomFieldDao;

    @Autowired
    private  SysCustomFieldExpandDao sysCustomFieldExpandDao;

    @Autowired
    private  UserService userService;


    @Override
    public Resp<List<SysCustomFieldVo>> querySysCustomFields() {
        try {
            String masterId = jwtUserService.getMasterId();
            String projectId = jwtUserService.getUserLoginInfo().getSysUser().getUserUseOpenProject().getProjectId();
            String redisKey = OneConstant.REDIS_KEY_PREFIX.SYS_CUSTOM_FIELDS + "-" + masterId + "-" + projectId;
            List<SysCustomFieldVo> result;
            //先查缓存
            RBucket<String> bucket = redisClient.getBucket(redisKey);
            String s = bucket.get();
            if (s != null) {
                result = JSONObject.parseArray(s, SysCustomFieldVo.class);
                return new Resp.Builder<List<SysCustomFieldVo>>().setData(result).totalSize(result.size()).ok();
            }
            //缓存没有查数据库
            List<SysCustomFieldVo> sysCustomFieldVos = queryAll(masterId, projectId);
            bucket.set(JSONObject.toJSONString(sysCustomFieldVos), 24, TimeUnit.HOURS);
            return new Resp.Builder<List<SysCustomFieldVo>>().setData(sysCustomFieldVos).totalSize(sysCustomFieldVos.size()).ok();
        } catch (BizException e) {
            logger.error("class: SysCustomFieldServiceImpl#querySysCustomFields,error []" + e.getMessage());
            return new Resp.Builder<List<SysCustomFieldVo>>().buildResult("查询失败");
        }
    }

    @Override
    @Transactional
    public Resp<String> updateSysCustomFields(SysCustomFieldVo sysCustomFieldVo) {
        try {
            sysCustomFieldVo.verify();
            List<String> requestValues = sysCustomFieldVo.getMergeValues();
            List<String> saveValues = new ArrayList<>();
            if (requestValues == null || requestValues.size() <= 0) {
                return new Resp.Builder<String>().buildResult("请填写值后再更新");
            }
            //查询系统字段
            SysCustomField sysCustomField = sysCustomFieldVo.getSysCustomField();
            //过滤系统字段
            SysCustomField querySysCustomField = sysCustomFieldDao.queryByFieldName(sysCustomField.getFieldName());
            String defaultValues = StringUtils.isEmpty(querySysCustomField.getDefaultValues()) ? "" : querySysCustomField.getDefaultValues();

            List<String> sysDefaultValues = StringUtils.isEmpty(defaultValues) ? null : Arrays.asList(defaultValues.split(","));

            if (sysDefaultValues == null) {
                saveValues.addAll(requestValues);
            } else {
                for (String requestValue : requestValues) {
                    //检查是否为空,为空代表没有默认值
                    if (sysDefaultValues.contains(requestValue)) {
                        continue;
                    }
                    saveValues.add(requestValue.trim());
                }
            }

            String projectId = jwtUserService.getUserLoginInfo().getSysUser().getUserUseOpenProject().getProjectId();
            String masterId = jwtUserService.getMasterId();
            //检查是否有扩展字段
            SysCustomFieldExpand querySysCustomFieldExpand =
                    sysCustomFieldExpandDao.queryByUserIdAndFieldName(
                            sysCustomField.getFieldName(),
                            masterId,
                            projectId);
            int flag;
            String s = saveValues.toString();
            s = s.replace("[", "");
            s = s.replace("]", "");
            s = s.replace(", ", ",");
            //有更新
            if (querySysCustomFieldExpand != null) {
                querySysCustomFieldExpand.setValues(s);
                flag = sysCustomFieldExpandDao.update(querySysCustomFieldExpand);
            } else {
                //没有添加
                querySysCustomFieldExpand = new SysCustomFieldExpand();
                querySysCustomFieldExpand.setValues(s);
                querySysCustomFieldExpand.setId(String.valueOf(SnowFlakeUtil.getFlowIdInstance().nextId()));
                querySysCustomFieldExpand.setProjectId(projectId);
                querySysCustomFieldExpand.setLinkSysCustomField(querySysCustomField.getFieldName());
                querySysCustomFieldExpand.setSysCustomFieldId(querySysCustomField.getId());
                // 移除 master id - 待修改
                SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();
                String userId = sysUser.getId();
                querySysCustomFieldExpand.setUserId(userId);

                flag = sysCustomFieldExpandDao.insert(querySysCustomFieldExpand);
            }
            Result.updateResult(flag);
            String redisKey = OneConstant.REDIS_KEY_PREFIX.SYS_CUSTOM_FIELDS + "-" + masterId + "-" + projectId;
            //先查缓存
            RBucket<String> bucket = redisClient.getBucket(redisKey);
            String str = bucket.get();
            if (str != null) {
                //不为空删除缓存
                bucket.delete();
            }
            return new Resp.Builder<String>().setData(SysConstantEnum.UPDATE_SUCCESS.getValue()).ok();
        } catch (BizException e) {
            logger.error("class: SysCustomFieldServiceImpl#updateSysCustomFields,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    @Override
    public Resp<SysCustomFieldVo> getSysCustomField(String fieldName) {
        try {
            //做一下驼峰转下划线
            if (!StringUtils.isEmpty(fieldName)) {
                fieldName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
            }
            List<SysCustomFieldVo> result;
            String masterId = jwtUserService.getMasterId();
            String projectId = jwtUserService.getUserLoginInfo().getSysUser().getUserUseOpenProject().getProjectId();
            String redisKey = OneConstant.REDIS_KEY_PREFIX.SYS_CUSTOM_FIELDS + "-" + masterId + "-" + projectId;
            //先查缓存，缓存有返回
            RBucket<String> bucket = redisClient.getBucket(redisKey);
            String s = bucket.get();
            if (s != null) {
                result = JSONObject.parseArray(s, SysCustomFieldVo.class);
            } else {
                result = queryAll(masterId, projectId);
            }
            //循环获取值
            for (SysCustomFieldVo sysCustomFieldVo : result) {
                if (sysCustomFieldVo.getSysCustomField().getFieldName().equals(fieldName)) {
                    return new Resp.Builder<SysCustomFieldVo>().setData(sysCustomFieldVo).ok();
                }
            }
            return new Resp.Builder<SysCustomFieldVo>().setData(null).ok();
        } catch (BizException e) {
            logger.error("class: SysCustomFieldServiceImpl#getSysCustomField,error []" + e.getMessage());
            return new Resp.Builder<SysCustomFieldVo>().buildResult("查询失败");
        }

    }


    private List<SysCustomFieldVo> queryAll(String masterId, String projectId) {
        //查询系统字段
        List<SysCustomField> sysCustomFields = sysCustomFieldDao.queryAll();
        //查询当前项目所使用所有字段
        SysCustomFieldExpand sysCustomFieldExpand = new SysCustomFieldExpand();
        sysCustomFieldExpand.setUserId(masterId);
        sysCustomFieldExpand.setProjectId(projectId);
        List<SysCustomFieldExpand> sysCustomFieldExpands = sysCustomFieldExpandDao.queryList(sysCustomFieldExpand);
        //组装 SysCustomFieldExpand
        List<SysCustomFieldVo> result = new ArrayList<>(sysCustomFields.size());
        for (SysCustomField customField : sysCustomFields) {
            SysCustomFieldVo sysCustomFieldVo = new SysCustomFieldVo();
            //如果相等了，先取出系统默认值，在取出扩展合并
            StringBuilder defaultValues =
                    new StringBuilder((StringUtils.isEmpty(customField.getDefaultValues()) ? "" : customField.getDefaultValues()));
            int flag = 0;
            for (SysCustomFieldExpand customFieldExpand : sysCustomFieldExpands) {
                if (customField.getFieldName().equals(customFieldExpand.getLinkSysCustomField())) {
                    if (!"".equals(defaultValues.toString()) && flag == 0) {
                        defaultValues.append(",");
                    }
                    flag++;
                    //取扩展值
                    String values = StringUtils.isEmpty(customFieldExpand.getValues()) ? "" : customFieldExpand.getValues();
                    //合并
                    defaultValues.append(values);
                    sysCustomFieldVo.setSysCustomFieldExpand(customFieldExpand);
                    //置位空
                    customFieldExpand.setValues("");
                }
            }
            //置位空
            customField.setDefaultValues("");
            List<String> mergeValues;

            mergeValues = StringUtils.isEmpty(defaultValues.toString()) ? new ArrayList<>()
                    : Arrays.asList(defaultValues.toString().split(","));

            sysCustomFieldVo.setSysCustomField(customField);
            sysCustomFieldVo.setMergeValues(mergeValues);
            result.add(sysCustomFieldVo);
        }
        return result;
    }


    /**
     * 获取项目负责人
     *
     * @Param: []
     * @return: com.hu.oneclick.model.base.Resp<java.util.List < java.lang.String>>
     * @Author: MaSiyi
     * @Date: 2021/12/15
     */
    @Override
    public Resp<List<String>> getThePersonInCharge() {
        List<String> list = new ArrayList<>();
        //系统字段
        SysCustomField report_name = sysCustomFieldDao.queryByFieldName("report_name");
        String defaultValues = report_name.getDefaultValues();
        if (!StringUtils.isEmpty(defaultValues)) {
            String[] split = defaultValues.split(",");
            for (String s : split) {
                list.add(s);
            }
        }
        //主账户和子账户
        String masterId = jwtUserService.getMasterId();
        List<SysUser> listPsn = userService.queryByUserIdAndParentId(masterId);
        for (SysUser sysUser : listPsn) {
            list.add(sysUser.getUserName());
        }
        return new Resp.Builder<List<String>>().setData(list).ok();
    }
}