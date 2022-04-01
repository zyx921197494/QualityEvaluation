package com.winkel.qualityevaluation.dao;
/*
  @ClassName SchoolDao
  @Description
  @Author winkel
  @Date 2022-03-16 17:12
  */

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.winkel.qualityevaluation.entity.School;
import com.winkel.qualityevaluation.vo.SchoolVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SchoolDao extends BaseMapper<School> {

    SchoolVo selectSchoolVoBySchoolCode(@Param("schoolCode")String schoolCode);

}
