package com.winkel.qualityevaluation.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.winkel.qualityevaluation.entity.task.EvaluateSubmit;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SubmitDao extends BaseMapper<EvaluateSubmit> {
}
