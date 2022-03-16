package com.winkel.qualityevaluation.dao;
/*
  @ClassName LocationDao
  @Description
  @Author winkel
  @Date 2022-03-16 14:09
  */

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.winkel.qualityevaluation.entity.Location;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LocationDao extends BaseMapper<Location> {
}
