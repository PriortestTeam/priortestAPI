package com.hu.oneclick.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hu.oneclick.model.domain.Room;
import org.apache.ibatis.annotations.Param;

public interface RoomDao extends BaseMapper<Room> {

    Room queryByCompanyNameAndUserEmail (@Param("companyName") String companyName, @Param("email")String email);

    int updateRoom(Room room);

    int insertRoom(Room room);
}
