package com.winkel.qualityevaluation.service.impl;
/*
  @ClassName IndexServiceImpl
  @Description
  @Author winkel
  @Date 2022-03-16 14:11
  */

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winkel.qualityevaluation.dao.IndexDao;
import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex;
import com.winkel.qualityevaluation.service.api.IndexService;
import org.springframework.stereotype.Service;

@Service
public class IndexServiceImpl extends ServiceImpl<IndexDao, EvaluateIndex> implements IndexService {
}
