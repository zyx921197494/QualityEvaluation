package com.winkel.qualityevaluation.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.winkel.qualityevaluation.entity.task.EvaluateTask;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskDao extends BaseMapper<EvaluateTask> {
}
