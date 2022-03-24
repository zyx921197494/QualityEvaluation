package com.winkel.qualityevaluation.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.winkel.qualityevaluation.entity.task.EvaluateTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TaskDao extends BaseMapper<EvaluateTask> {

    boolean startCycle(@Param("locationCode") String locationCode);

    Integer selectCurrentCycle(@Param("locationCode") String locationCode);

    Integer selectTaskIdByUserId(@Param("id") String userId, @Param("type") Integer taskType);

    EvaluateTask selectTaskByUserId(@Param("id") String userId, @Param("type") Integer taskType);

}
