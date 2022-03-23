package com.winkel.qualityevaluation.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.winkel.qualityevaluation.entity.task.EvaluateSubmit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SubmitDao extends BaseMapper<EvaluateSubmit> {

    List<EvaluateSubmit> selectALlSubmitByUserId(@Param("id") String userId);


}
