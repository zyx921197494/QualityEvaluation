package com.winkel.qualityevaluation.service.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.winkel.qualityevaluation.entity.task.EvaluateTask;

public interface TaskService extends IService<EvaluateTask> {

    boolean startCycle(String locationCode);

    Integer getCurrentCycle(String locationCode);

    Integer getTaskIdByUserId(String userId, Integer taskType);

    EvaluateTask getTaskByUserId(String userId, Integer taskType);

}
