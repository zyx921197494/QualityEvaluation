package com.winkel.qualityevaluation.dao;
/*
  @ClassName SchoolDao
  @Description
  @Author winkel
  @Date 2022-03-16 17:12
  */

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.winkel.qualityevaluation.entity.School;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SchoolDao extends BaseMapper<School> {
}
