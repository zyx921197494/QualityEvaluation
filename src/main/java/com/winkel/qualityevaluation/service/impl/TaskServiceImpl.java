package com.winkel.qualityevaluation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winkel.qualityevaluation.dao.TaskDao;
import com.winkel.qualityevaluation.entity.task.EvaluateTask;
import com.winkel.qualityevaluation.service.api.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
