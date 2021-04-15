package com.hu.oneclick.server.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.util.SnowFlakeUtil;
import com.hu.oneclick.dao.SysCustomFieldDao;
import com.hu.oneclick.dao.SysCustomFieldExpandDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.SysCustomField;
import com.hu.oneclick.model.domain.SysCustomFieldExpand;
import com.hu.oneclick.model.domain.dto.SysCustomFieldVo;
import com.hu.oneclick.server.service.SysCustomFieldService;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SysCustomFieldServiceImpl implements SysCustomFieldService {

    private final static Logger logger = LoggerFactory.getLogger(SysCustomFieldServiceImpl.class);

    private final JwtUserServiceImpl jwtUserService;
    private final RedissonClient redisClient;

    private final SysCustomFieldDao sysCustomFieldDao;

    private final SysCustomFieldExpandDao sysCustomFieldExpandDao;

    public SysCustomFieldServiceImpl(JwtUserServiceImpl jwtUserService, RedissonClient redisClient, SysCustomFieldDao sysCustomFieldDao, SysCustomFieldExpandDao sysCustomFieldExpandDao) {
        this.jwtUserService = jwtUserService;
        this.redisClient = redisClient;
        this.sysCustomFieldDao = sysCustomFieldDao;
        this.sysCustomFieldExpandDao = sysCustomFieldExpandDao;
    }

    @Override
    public Resp<List<SysCustomFieldVo>> querySysCustomFields() {
        String masterId = jwtUserService.getMasterId();
        String projectId = jwtUserService.getUserLoginInfo().getSysUser().getUserUseOpenProject().getProjectId();
        String redisKey = OneConstant.REDIS_KEY_PREFIX.SYS_CUSTOM_FIELDS + "-" +masterId + "-" + projectId;
        List<SysCustomFieldVo> result;
        try {
            //先查缓存
            RBucket<String> bucket = redisClient.getBucket(redisKey);
            String s = bucket.get();
            if (s != null){
                result = JSONObject.parseArray(s, SysCustomFieldVo.class);
                return new Resp.Builder<List<SysCustomFieldVo>>().setData(result).ok();
            }
            //查询系统字段
            List<SysCustomField> sysCustomFields = sysCustomFieldDao.queryAll();

            //查询当前项目所使用所有字段
            SysCustomFieldExpand sysCustomFieldExpand = new SysCustomFieldExpand();
            sysCustomFieldExpand.setUserId(masterId);
            sysCustomFieldExpand.setProjectId(projectId);
            List<SysCustomFieldExpand> sysCustomFieldExpands = sysCustomFieldExpandDao.queryList(sysCustomFieldExpand);
            //组装 SysCustomFieldExpand
            result = new ArrayList<>(sysCustomFields.size());
            for (SysCustomField customField : sysCustomFields) {
                SysCustomFieldVo sysCustomFieldVo = new SysCustomFieldVo();
                String mergeVal = null;
                //如果相等了，先取出系统默认值，在取出扩展合并
                String defaultValues = StringUtils.isEmpty(customField.getDefaultValues()) ? "" : customField.getDefaultValues();
                for (SysCustomFieldExpand customFieldExpand : sysCustomFieldExpands) {
                    if (customField.getFieldName().equals(customFieldExpand.getLinkSysCustomField())){
                        //取扩展值
                        String values = StringUtils.isEmpty(customFieldExpand.getValues()) ? "" : customFieldExpand.getValues();
                        //合并
                        mergeVal = defaultValues + values;
                        sysCustomFieldVo.setSysCustomFieldExpand(customFieldExpand);
                    }
                }
                List<String> mergeValues = StringUtils.isEmpty(mergeVal) ? null : Arrays.asList(mergeVal.split(","));
                sysCustomFieldVo.setSysCustomField(customField);
                sysCustomFieldVo.setMergeValues(mergeValues);
                result.add(sysCustomFieldVo);
            }
            bucket.set(JSONObject.toJSONString(result),24, TimeUnit.HOURS);
            return new Resp.Builder<List<SysCustomFieldVo>>().setData(result).ok();
        }catch (BizException e){
            logger.error("class: SysCustomFieldServiceImpl#querySysCustomFields,error []" + e.getMessage());
            return new Resp.Builder<List<SysCustomFieldVo>>().buildResult("查询失败");
        }
    }

    @Override
    @Transactional
    public Resp<String> updateSysCustomFields(SysCustomFieldVo sysCustomFieldVo) {
        try {
            List<String> requestValues = sysCustomFieldVo.getMergeValues();
            List<String> saveValues = new ArrayList<>();
            if (requestValues == null || requestValues.size() <= 0){
                return new Resp.Builder<String>().buildResult("请填写值后再更新");
            }
            //查询系统字段
            SysCustomField sysCustomField = sysCustomFieldVo.getSysCustomField();
            //过滤系统字段
            SysCustomField querySysCustomField = sysCustomFieldDao.queryByFieldName(sysCustomField.getFieldName());
            String defaultValues = StringUtils.isEmpty(querySysCustomField.getDefaultValues()) ? "" : querySysCustomField.getDefaultValues();

            List<String> sysDefaultValues = StringUtils.isEmpty(defaultValues) ? null : Arrays.asList(defaultValues.split(","));
            for (String requestValue : requestValues) {
                //检查是否为空,为空代表没有默认值
                if (sysDefaultValues == null) {
                    saveValues.add(requestValue);
                    continue;
                }
                for (String sysDefaultValue : sysDefaultValues) {
                    //两值相等，则不添加默认值
                    if (requestValue.equals(sysDefaultValue)){
                        continue;
                    }
                    saveValues.add(requestValue);
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
            int flag = 0;
            //有更新
            if (querySysCustomFieldExpand != null){
                querySysCustomFieldExpand.setValues(saveValues.toString());
                flag = sysCustomFieldExpandDao.update(querySysCustomFieldExpand);
            }else{
                //没有添加
                querySysCustomFieldExpand = new SysCustomFieldExpand();
                querySysCustomFieldExpand.setValues(saveValues.toString());
                querySysCustomFieldExpand.setId(String.valueOf(SnowFlakeUtil.getFlowIdInstance().nextId());
                querySysCustomFieldExpand.setProjectId(projectId);
                querySysCustomFieldExpand.setLinkSysCustomField(querySysCustomField.getFieldName());
                querySysCustomFieldExpand.setUserId(masterId);
                flag = sysCustomFieldExpandDao.insert(querySysCustomFieldExpand);
            }
            Result.updateResult(flag);
            String redisKey = OneConstant.REDIS_KEY_PREFIX.SYS_CUSTOM_FIELDS + "-" +masterId + "-" + projectId;
            //先查缓存
            RBucket<String> bucket = redisClient.getBucket(redisKey);
            String s = bucket.get();
            if (s != null){
                //不为空删除缓存
                bucket.delete();
            }
            return new Resp.Builder<String>().setData(SysConstantEnum.UPDATE_SUCCESS.getValue()).ok();
        }catch (BizException e){
            logger.error("class: SysCustomFieldServiceImpl#updateSysCustomFields,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(SysConstantEnum.UPDATE_FAILED.getValue());
        }
    }

    @Override
    public Resp<SysCustomFieldVo> getSysCustomField(String fieldName) {
//        try {
//            String masterId = jwtUserService.getMasterId();
//            String projectId = jwtUserService.getUserLoginInfo().getSysUser().getUserUseOpenProject().getProjectId();
//            String redisKey = OneConstant.REDIS_KEY_PREFIX.SYS_CUSTOM_FIELDS + "-" +masterId + "-" + projectId;
//            //先查缓存，缓存有返回
//            RBucket<String> bucket = redisClient.getBucket(redisKey);
//            String s = bucket.get();
//            if (s != null){
//                List<SysCustomFieldVo> result = JSONObject.parseArray(s, SysCustomFieldVo.class);
//                for (SysCustomFieldVo sysCustomFieldVo : result) {
//                    if (sysCustomFieldVo.getSysCustomField().getFieldName().equals(fieldName)){
//                        return new Resp.Builder<SysCustomFieldVo>().setData(sysCustomFieldVo).ok();
//                    }
//                }
//            }
//            //缓存没有查询数据库
//            SysCustomFieldExpand querySysCustomFieldExpand =
//                    sysCustomFieldExpandDao.queryByUserIdAndFieldName(
//                            sysCustomField.getFieldName(),
//                            masterId,
//                            projectId);
//        }catch (BizException e){
//
//        }

        return null;
    }
}
