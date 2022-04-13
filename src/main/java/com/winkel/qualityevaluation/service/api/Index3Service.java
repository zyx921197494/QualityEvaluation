package com.winkel.qualityevaluation.service.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex;
import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex1;
import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex2;
import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex3;

import java.util.List;

public interface Index3Service extends IService<EvaluateIndex3> {

    List<EvaluateIndex3> listIndex3ByEvaluateId(Integer evaluateId);

}
