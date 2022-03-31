package com.winkel.qualityevaluation.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.winkel.qualityevaluation.entity.Authority;
import com.winkel.qualityevaluation.entity.User;
import com.winkel.qualityevaluation.vo.AccountVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserDao extends BaseMapper<User> {

    int checkPassword(@Param("username") String username, @Param("password") String password);

    List<Authority> selectAuthorities(@Param("username") String username);

    Integer checkCreated(@Param("schoolCode") String schoolCode, @Param("cycle") Integer cycle, @Param("authorityId") Integer authorityId);

    boolean updateUserPassword(@Param("schoolCode") String schoolCode, @Param("authorityId") Integer authorityId, @Param("newPwd") String newPwd, @Param("currentCycle") Integer currentCycle);

    boolean unlockSelfUserByLocationCode(@Param("locationCode") String locationCode);

    boolean unlockUserBySchoolCodeAndType(@Param("schoolCode") String schoolCode, @Param("type") Integer type);

    boolean lockUserBySchoolCodeAndType(@Param("schoolCode") String schoolCode, @Param("type") Integer type);

    List<AccountVo> selectAccountBySchoolCodeAndAuthorityType(@Param("schoolCode") String schoolCode, @Param("authorityId") Integer authorityId);

}
