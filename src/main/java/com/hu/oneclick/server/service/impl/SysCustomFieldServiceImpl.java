package com.hu.oneclick.server.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.SysCustomFieldDao;
import com.hu.oneclick.dao.SysCustomFieldExpandDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SysCustomField;
import com.hu.oneclick.model.domain.SysCustomFieldExpand;
import com.hu.oneclick.model.domain.dto.SysCustomFieldVo;
import com.hu.oneclick.server.service.SysCustomFieldService;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
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
        String redisKey = OneConstant.REDIS_KEY_PREFIX.SYS_CUSTOM_FIELDS + "-" +masterId;
        List<SysCustomFieldVo> result = null;
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
            SysCustomFieldExpand sysCustomFieldExpand = new SysCustomFieldExpand();
            sysCustomFieldExpand.setUserId(masterId);

            //查询企业字段
            List<SysCustomFieldExpand> sysCustomFieldExpands = sysCustomFieldExpandDao.queryAll(sysCustomFieldExpand);
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
        }catch (Exception e){
            logger.error("class: SysCustomFieldServiceImpl#querySysCustomFields,error []" + e.getMessage());
            return new Resp.Builder<List<SysCustomFieldVo>>().buildResult("查询失败");
        }
    }
}
