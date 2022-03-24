package com.winkel.qualityevaluation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winkel.qualityevaluation.dao.TaskDao;
import com.winkel.qualityevaluation.entity.task.EvaluateTask;
import com.winkel.qualityevaluation.service.api.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return taskDao.selectTaskIdByUserId(userId,taskType);
    }

    @Override
    public EvaluateTask getTaskByUserId(String userId, Integer taskType) {
        return taskDao.selectTaskByUserId(userId, taskType);
    }
}
