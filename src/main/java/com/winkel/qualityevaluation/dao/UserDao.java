package com.winkel.qualityevaluation.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.winkel.qualityevaluation.entity.Authority;
import com.winkel.qualityevaluation.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserDao extends BaseMapper<User> {

    int checkPassword(@Param("username") String username, @Param("password") String password);

    List<Authority> selectAuthorities(@Param("username") String username);

    boolean updateUserPassword(@Param("schoolCode") String schoolCode, @Param("authorityId") Integer authorityId, @Param("newPwd") String newPwd, @Param("currentCycle")Integer currentCycle);

    boolean unlockSelfUserByLocationCode(@Param("locationCode") String locationCode);

    boolean unlockUserBySchoolCode(@Param("schoolCode") String schoolCode, @Param("type") Integer type);

}
