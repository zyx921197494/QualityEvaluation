package com.winkel.qualityevaluation.service.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.winkel.qualityevaluation.entity.task.EvaluateSubmit;
import com.winkel.qualityevaluation.vo.CountDTO;
import com.winkel.qualityevaluation.vo.Index2Vo;
import com.winkel.qualityevaluation.vo.ScoreDTO;
import com.winkel.qualityevaluation.vo.ScoreVo;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.formula.functions.Count;

import java.util.List;

public interface SubmitService extends IService<EvaluateSubmit> {

    List<EvaluateSubmit> getALlSubmitByUserId(String userId);

    Double getSumByIndex1IdAndCountycode(Integer index1Id, String countyCode);

    Double getSumByIndex1IdAndCitycode(Integer index1Id, String cityCode);

    Double getSumByIndex1IdAndProvincecode(Integer index1Id, String provinceCode);

    Double getCountByCountycode(String countyCode);

//    Double getCountByCitycode(String cityCode);
//
//    Double getCountByProvincecode(String provinceCode);

    /**
     * desc: 获取县域内各学校在不同督评完成情况下5项一级指标的得分
     * params: [countycode, taskStatus] countycode：县的地址码 taskStatus  taskStatus：督评完成状态
     * return: java.util.List<com.winkel.qualityevaluation.vo.ScoreVo>
     * exception:
     **/
    List<ScoreVo> getIndex1ScoreByCountycode(String countycode, Integer taskStatus);

    /**
     * desc: 获取县域内各学校在不同督评完成情况下一级指标的总分，并按降序排序
     * params: [countycode, taskStatus] countycode：县的地址码 taskStatus  taskStatus：督评完成状态
     * return: java.util.List<com.winkel.qualityevaluation.vo.ScoreVo>
     * exception:
     **/
    List<ScoreVo> getTotalScoreByCountycode(String countycode, Integer taskStatus);

    // 以幼儿园某项属性区分评估得分
    List<ScoreVo> getScoreByIsCity(ScoreDTO scoreDTO);

    List<ScoreVo> getIndex1ByIsCity(ScoreDTO scoreDTO);

    Double getCountByLocationCodeAndType(CountDTO CountDTO);

    List<Index2Vo> getComplete(Integer taskId);

    List<Index2Vo> getIndex2ByEvaluateId(Integer evaluateId);

}
