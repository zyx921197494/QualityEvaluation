package com.winkel.qualityevaluation.service.impl;
/*
  @ClassName LocationServiceImpl
  @Description
  @Author winkel
  @Date 2022-03-16 14:11
  */

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winkel.qualityevaluation.dao.Index1Dao;
import com.winkel.qualityevaluation.dao.LocationDao;
import com.winkel.qualityevaluation.entity.Location;
import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex1;
import com.winkel.qualityevaluation.service.api.Index1Service;
import com.winkel.qualityevaluation.service.api.LocationService;
import org.springframework.stereotype.Service;

@Service
public class Index1ServiceImpl extends ServiceImpl<Index1Dao, EvaluateIndex1> implements Index1Service {
}
