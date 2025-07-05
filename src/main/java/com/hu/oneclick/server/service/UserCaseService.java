package com.hu.oneclick.server.service;

import com.hu.oneclick.model.param.UserCaseParam;
import com.hu.oneclick.model.domain.vo.UserCaseVo;

import java.util.List;

public interface UserCaseService {

    public boolean insertUserCase(UserCaseParam userCaseParam);

    public boolean updateUserCase(UserCaseParam userCaseParam);

    public List<UserCaseVo> listData(UserCaseParam userCaseParam);

    public UserCaseVo getUserCaseInfoById(long id);

    public boolean removeUserCaseById(long id);

}
