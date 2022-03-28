package com.winkel.qualityevaluation.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.winkel.qualityevaluation.entity.task.EvaluateTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TaskDao extends BaseMapper<EvaluateTask> {

    boolean startCycle(@Param("locationCode") String locationCode);

    Integer selectCurrentCycle(@Param("locationCode") String locationCode);

    Integer selectTaskIdByUserIdAndType(@Param("id") String userId, @Param("type") Integer taskType);

    EvaluateTask selectTaskByUserId(@Param("id") String userId, @Param("type") Integer taskType);

//    List<Integer> selectTaskIdByAdminId(@Param("id") String userId);

    List<Integer> selectFinishedTaskIdByCountyAdminId(@Param("id") String userId);

    List<Integer> selectFinishedTaskIdByCityAdminId(@Param("id") String userId);

    List<Integer> selectFinishedTaskIdByProvinceAdminId(@Param("id") String userId);

    List<EvaluateTask> selectTaskByCountycodeAndTasktypeAndStatus(@Param("countycode") String countycode, @Param("status") Integer status, @Param("tasktype") Integer tasktype);

    List<EvaluateTask> selectTaskByCitycodeAndTasktypeAndStatus(@Param("citycode") String citycode, @Param("status") Integer status, @Param("tasktype") Integer tasktype);

    List<EvaluateTask> selectTaskByProvincecodeAndTasktypeAndStatus(@Param("provincecode") String provincecode, @Param("status") Integer status, @Param("tasktype") Integer tasktype);

    List<EvaluateTask> selectCountyTask(@Param("locationCode") String locationCode);

//    List<EvaluateTask> selectCityTask(@Param("locationCode") String locationCode);
//
//    List<EvaluateTask> selectProvinceTask(@Param("locationCode") String locationCode);


}
