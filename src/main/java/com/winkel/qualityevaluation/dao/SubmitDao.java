package com.winkel.qualityevaluation.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.winkel.qualityevaluation.entity.task.EvaluateSubmit;
import com.winkel.qualityevaluation.vo.CountDTO;
import com.winkel.qualityevaluation.vo.Index2Vo;
import com.winkel.qualityevaluation.vo.ScoreDTO;
import com.winkel.qualityevaluation.vo.ScoreVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

@Mapper
public interface SubmitDao extends BaseMapper<EvaluateSubmit> {

    List<EvaluateSubmit> selectAllSubmitByUserId(@Param("id") String userId);

    Double selectSumByIndex1IdAndCountycode(@Param("indexId") Integer index1Id, @Param("countyCode") String countyCode);

    Double selectSumByIndex1IdAndCitycode(@Param("indexId") Integer index1Id, @Param("cityCode") String cityCode);

    Double selectSumByIndex1IdAndProvincecode(@Param("indexId") Integer index1Id, @Param("provinceCode") String provinceCode);

    Double selectCountByCountycode(@Param("countyCode") String countyCode);

//    Double selectCountByCitycode(@Param("cityCode") String cityCode);
//
//    Double selectCountByProvincecode(@Param("provinceCode") String provinceCode);

    List<ScoreVo> selectIndex1ScoreByCountycode(@Param("countyCode") String countycode, @Param("taskStatus") Integer taskStatus);

    List<ScoreVo> selectTotalScoreByCountycode(@Param("countyCode") String countycode, @Param("taskStatus") Integer taskStatus);

    //以下方法均为城市幼儿园和农村幼儿园的总分均值和各一级指标均值

    Double selectCountByLocationCodeAndType(@Param("countDTO") CountDTO countDTO);

    //是否城市
    List<ScoreVo> selectScoreByIsCity(@Param("scoreDTO") ScoreDTO scoreDTO);

    List<ScoreVo> selectIndex1ByIsCity(@Param("scoreDTO") ScoreDTO scoreDTO);

//    //是否公办
//    List<ScoreVo> selectScoreByIsPublic(@Param("scoreDTO") ScoreDTO scoreDTO);
//
//    List<ScoreVo> selectIndex1ByIsPublic(@Param("scoreDTO") ScoreDTO scoreDTO);
//
//    //是否普惠
//    List<ScoreVo> selectScoreByIsGB(@Param("scoreDTO") ScoreDTO scoreDTO);
//
//    List<ScoreVo> selectIndex1ByIsGB(@Param("scoreDTO") ScoreDTO scoreDTO);
//
//    //是否在册
//    List<ScoreVo> selectScoreByIsRegister(@Param("scoreDTO") ScoreDTO scoreDTO);
//
//    List<ScoreVo> selectIndex1ByIsRegister(@Param("scoreDTO") ScoreDTO scoreDTO);

    List<Index2Vo> selectComplete(@Param("taskId") Integer taskId);

    List<Index2Vo> listIndex2ByEvaluateId(@Param("evaluateId") Integer evaluateId);

}
