package com.winkel.qualityevaluation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winkel.qualityevaluation.dao.Index3Dao;
import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex3;
import com.winkel.qualityevaluation.service.api.Index3Service;
import org.springframework.stereotype.Service;

@Service
public class Index3ServiceImpl extends ServiceImpl<Index3Dao, EvaluateIndex3> implements Index3Service {
}
