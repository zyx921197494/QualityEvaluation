package com.winkel.qualityevaluation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winkel.qualityevaluation.dao.SubmitFileDao;
import com.winkel.qualityevaluation.entity.task.EvaluateSubmitFile;
import com.winkel.qualityevaluation.service.api.SubmitFileService;
import org.springframework.stereotype.Service;

@Service
public class SubmitFileServiceImpl extends ServiceImpl<SubmitFileDao, EvaluateSubmitFile> implements SubmitFileService {
}
