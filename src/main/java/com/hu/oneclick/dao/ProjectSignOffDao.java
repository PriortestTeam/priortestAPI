package com.hu.oneclick.dao;

import com.hu.oneclick.model.entity.ProjectSignOff;

import java.util.List;

public interface ProjectSignOffDao {
    int deleteByPrimaryKey(Integer id);

    int insert(ProjectSignOff record);

    int insertSelective(ProjectSignOff record);

    ProjectSignOff selectByPrimaryKey(Integer id);


    List<ProjectSignOff> selectByUserProject(String userId,String projectId);



    int updateByPrimaryKeySelective(ProjectSignOff record);

    int updateByPrimaryKey(ProjectSignOff record);


}
