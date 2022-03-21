package com.winkel.qualityevaluation.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.winkel.qualityevaluation.entity.task.EvaluateSubmitFile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SubmitFileDao extends BaseMapper<EvaluateSubmitFile> {
}
