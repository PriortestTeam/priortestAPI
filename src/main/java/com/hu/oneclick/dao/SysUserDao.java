package com.hu.oneclick.dao;
import com.hu.oneclick.model.annotation.Page;
import com.hu.oneclick.model.entity.SysUser;
import com.hu.oneclick.model.domain.dto.PlatformUserDto;
import com.hu.oneclick.model.domain.dto.SubUserDto;
import com.hu.oneclick.model.domain.dto.SysUserRoleDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;
/**
 * (SysUser)表数据库访问层
 *
 * @author makejava
 * @since 2020-11-14 23:32:43
 */
@Component
