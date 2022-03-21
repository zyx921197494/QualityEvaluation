package com.winkel.qualityevaluation.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.winkel.qualityevaluation.entity.task.EvaluateReportFile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReportFileDao extends BaseMapper<EvaluateReportFile> {
}
