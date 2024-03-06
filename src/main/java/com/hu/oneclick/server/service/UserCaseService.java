package com.hu.oneclick.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.hu.oneclick.model.domain.dto.UserCaseDto;
import com.hu.oneclick.model.domain.param.UserCaseParam;
import com.hu.oneclick.model.domain.vo.UserCaseVo;

import java.util.List;

public interface UserCaseService {

    public boolean insertUserCase(UserCaseParam userCaseParam);

    public boolean updateUserCase(UserCaseParam userCaseParam);

    public List<UserCaseVo> listData(UserCaseParam userCaseParam);

    public UserCaseVo getUserCaseInfoById(String id);

    public boolean removeUserCaseById(String id);

}
