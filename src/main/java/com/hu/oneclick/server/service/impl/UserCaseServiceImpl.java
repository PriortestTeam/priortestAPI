package com.hu.oneclick.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hu.oneclick.dao.UserCaseDao;
import com.hu.oneclick.model.domain.dto.UserCaseDto;
import com.hu.oneclick.model.domain.param.UserCaseParam;
import com.hu.oneclick.model.domain.vo.UserCaseVo;
import com.hu.oneclick.server.service.UserCaseService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户故事用例业务处理类
 */
@Slf4j
@Service
public class UserCaseServiceImpl extends ServiceImpl<UserCaseDao, UserCaseDto> implements UserCaseService {
    @Override
    public boolean insertUserCase(UserCaseParam userCaseParam) {
        UserCaseDto entity = BeanUtil.copyProperties(userCaseParam, UserCaseDto.class);

        // 保存自定义字段
        if (!JSONUtil.isNull(userCaseParam.getUsecaseExpand())) {
            String jsonStr = JSONUtil.toJsonStr(userCaseParam.getUsecaseExpand());
            entity.setUsecaseExpand(jsonStr);
        }
        super.save(entity);
        return true;
    }

    @Override
    public boolean updateUserCase(UserCaseParam userCaseParam) {
        UserCaseDto entity = BeanUtil.copyProperties(userCaseParam, UserCaseDto.class);

        // 修改自定义字段
        if (!JSONUtil.isNull(userCaseParam.getUsecaseExpand())) {
            String jsonStr = JSONUtil.toJsonStr(userCaseParam.getUsecaseExpand());
            entity.setUsecaseExpand(jsonStr);
        }

        super.updateById(entity);
        return false;
    }

    @Override
    public List<UserCaseVo> listData(UserCaseParam userCaseParam) {
        LambdaQueryWrapper<UserCaseDto> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.like(StrUtil.isNotBlank(userCaseParam.getTitle()), UserCaseDto::getTitle, userCaseParam.getTitle())
                .eq(StrUtil.isNotBlank(userCaseParam.getUseCategory()), UserCaseDto::getUseCategory, userCaseParam.getUseCategory())
                //.eq(StrUtil.isNotEmpty(userCaseParam.getGrade()), UserCaseDto::getGrade, userCaseParam.getGrade())
                //.eq(StrUtil.isNotEmpty(userCaseParam.getLevel()), UserCaseDto::getLevel, userCaseParam.getLevel())
                //.eq(StrUtil.isNotBlank(userCaseParam.getFeatureId()), UserCaseDto::getFeatureId, userCaseParam.getFeatureId());
                .eq(StrUtil.isNotBlank(Long.toString(userCaseParam.getFeatureId())), UserCaseDto::getFeatureId, userCaseParam.getFeatureId());
        List<UserCaseDto> list = this.baseMapper.selectList(queryWrapper);
        List<UserCaseVo> resultList = BeanUtil.copyToList(list, UserCaseVo.class);
        resultList.forEach(obj -> {
            //obj.setCustomFieldDatas(obj.getUsecaseExpand());
            obj.getUsecaseExpand();

        });
        return resultList;
    }

    @Override
    public UserCaseVo getUserCaseInfoById(long id) {
        UserCaseDto entity = this.baseMapper.selectById(id);
        UserCaseVo resultEntity = BeanUtil.copyProperties(entity, UserCaseVo.class);
        return resultEntity;
    }

    @Override
    public boolean removeUserCaseById(long id) {
        int index = this.baseMapper.deleteById(id);
        return index > 0;
    }
}
