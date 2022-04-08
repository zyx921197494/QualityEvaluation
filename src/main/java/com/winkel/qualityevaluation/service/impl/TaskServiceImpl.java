package com.winkel.qualityevaluation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winkel.qualityevaluation.dao.TaskDao;
import com.winkel.qualityevaluation.entity.task.EvaluateTask;
import com.winkel.qualityevaluation.exception.TaskException;
import com.winkel.qualityevaluation.service.api.TaskService;
import com.winkel.qualityevaluation.util.Const;
import com.winkel.qualityevaluation.vo.SchoolTaskDTO;
import com.winkel.qualityevaluation.vo.SchoolTaskVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskDao, EvaluateTask> implements TaskService {

    @Autowired
    private TaskDao taskDao;

    @Override
    public boolean startCycle(String locationCode) {
        return taskDao.startCycle(locationCode);
    }

    @Override
    public Integer getCurrentCycle(String locationCode) {
        return taskDao.selectCurrentCycle(locationCode);
    }

    @Override
    public Integer getTaskIdByUserId(String userId, Integer taskType) {
        Integer taskId = taskDao.selectTaskIdByUserIdAndType(userId, taskType);
        EvaluateTask task = taskDao.selectOne(new QueryWrapper<EvaluateTask>().eq("evaluate_task_id", taskId));
        if (task != null && Objects.equals(task.getIsLocked(), Const.LOCKED)) {
            throw new TaskException("任务已锁定，请联系管理员");
        }
        return taskId;
    }

    @Override
    public Integer getAllTaskIdByUserId(String userId, Integer taskType) {
        return taskDao.selectTaskIdByUserIdAndType(userId, taskType);
    }

    //    @Override
//    public EvaluateTask getTaskByUserId(String userId, Integer taskType) {
//        return taskDao.selectTaskByUserId(userId, taskType);
//    }

    @Override
    public List<Integer> getFinishTaskIdByCountyAdminId(String userId) {
        return taskDao.selectFinishedTaskIdByCountyAdminId(userId);
    }

    @Override
    public List<Integer> getFinishTaskIdByCityAdminId(String userId) {
        return taskDao.selectFinishedTaskIdByCityAdminId(userId);
    }

    @Override
    public List<Integer> getFinishTaskIdByProvinceAdminId(String userId) {
        return taskDao.selectFinishedTaskIdByProvinceAdminId(userId);
    }

    @Override
    public List<EvaluateTask> getCountByCountycodeAndTasktypeAndStatus(String countycode, Integer status, Integer tasktype) {
        return taskDao.selectTaskByCountycodeAndTasktypeAndStatus(countycode, status, tasktype);
    }

    @Override
    public List<EvaluateTask> getCountByCitycodeAndTasktypeAndStatus(String citycode, Integer status, Integer tasktype) {
        return taskDao.selectTaskByCitycodeAndTasktypeAndStatus(citycode, status, tasktype);
    }

    @Override
    public List<EvaluateTask> getCountByProvincecodeAndTasktypeAndStatus(String provincecode, Integer status, Integer tasktype) {
        return taskDao.selectTaskByProvincecodeAndTasktypeAndStatus(provincecode, status, tasktype);
    }

    @Override
    public List<EvaluateTask> getCountyTask(String locationCode) {
        return taskDao.selectCountyTask(locationCode);
    }

//    @Override
//    public List<EvaluateTask> getCityTask(String locationCode) {
//        return taskDao.selectCityTask(locationCode);
//    }
//
//    @Override
//    public List<EvaluateTask> getProvinceTask(String locationCode) {
//        return taskDao.selectProvinceTask(locationCode);
//    }


    @Override
    public List<SchoolTaskVo> listAllBySort(SchoolTaskDTO schoolTaskDTO) {
        return taskDao.listAllBySort(schoolTaskDTO);
    }

    @Override
    public LocalDateTime getLastSubmitTimeByTaskId(Integer taskId) {
        return taskDao.selectLastSubmitTimeByTaskId(taskId);
    }

    @Override
    public List<String> getFileNameBySchoolcodeAndType(List<String> schoolCodes, Integer type) {
        return taskDao.selectFileNameBySchoolcodeAndType(schoolCodes, type);
    }

    @Override
    public Integer getTaskIdByBySchoolcodeAndType(String schoolCode, Integer type) {
        return taskDao.getTaskIdByBySchoolcodeAndType(schoolCode, type);
    }

}
