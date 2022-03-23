package com.winkel.qualityevaluation.service.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.winkel.qualityevaluation.entity.task.EvaluateSubmit;

import java.util.List;

public interface SubmitService extends IService<EvaluateSubmit> {

    List<EvaluateSubmit> getALlSubmitByUserId(String userId);

}
