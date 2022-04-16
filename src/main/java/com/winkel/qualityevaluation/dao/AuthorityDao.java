package com.winkel.qualityevaluation.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.winkel.qualityevaluation.entity.Authority;
import com.winkel.qualityevaluation.pojo.UserAuthority;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface AuthorityDao extends BaseMapper<Authority> {

    boolean insertUserAuthority(@Param("userId")String userId, @Param("authorityId") int authorityId);

    boolean insertUserAuthorityBatch(List<UserAuthority> userAuthorities);

}
