package com.winkel.qualityevaluation.service.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.winkel.qualityevaluation.entity.task.EvaluateTask;
import com.winkel.qualityevaluation.pojo.dto.CycleDTO;
import com.winkel.qualityevaluation.pojo.dto.SchoolTaskDTO;
import com.winkel.qualityevaluation.pojo.vo.CycleVo;
import com.winkel.qualityevaluation.pojo.vo.SchoolTaskVo;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskService extends IService<EvaluateTask> {

    boolean startCycle(String locationCode);

    Integer getCurrentCycle(String locationCode);

    Integer getTaskIdByUserId(String userId, Integer taskType);

    Integer getAllTaskIdByUserId(String userId, Integer taskType);

//    EvaluateTask getTaskByUserId(String userId, Integer taskType);

    List<Integer> getFinishTaskIdByCountyAdminId(String userId);

    List<Integer> getFinishTaskIdByCityAdminId(String userId);

    List<Integer> getFinishTaskIdByProvinceAdminId(String userId);

    List<EvaluateTask> getCountByCountycodeAndTasktypeAndStatus(String countycode, Integer status, Integer tasktype);

    List<EvaluateTask> getCountByCitycodeAndTasktypeAndStatus(String citycode, Integer status, Integer tasktype);

    List<EvaluateTask> getCountByProvincecodeAndTasktypeAndStatus(String provincecode, Integer status, Integer tasktype);

    List<EvaluateTask> getCountyTask(String locationCode);

//    List<EvaluateTask> getCityTask(String locationCode);
//
//    List<EvaluateTask> getProvinceTask(String locationCode);

    List<SchoolTaskVo> listAllBySort(SchoolTaskDTO schoolTaskDTO);

    LocalDateTime getLastSubmitTimeByTaskId(Integer taskId);

    LocalDateTime getFirstSubmitTimeByTaskId(Integer taskId);

    List<String> getFileNameBySchoolcodeAndType(List<String> schoolCodes, Integer type);

    Integer getTaskIdByBySchoolcodeAndType(String schoolCode, Integer type);

    List<CycleVo> getCycleByLocationAndRegionType(CycleDTO cycleDTO);

}
