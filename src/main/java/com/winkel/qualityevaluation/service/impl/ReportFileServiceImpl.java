package com.winkel.qualityevaluation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winkel.qualityevaluation.dao.ReportFileDao;
import com.winkel.qualityevaluation.entity.task.EvaluateReportFile;
import com.winkel.qualityevaluation.service.api.ReportFileService;
import org.springframework.stereotype.Service;

@Service
public class ReportFileServiceImpl extends ServiceImpl<ReportFileDao, EvaluateReportFile> implements ReportFileService {
}
