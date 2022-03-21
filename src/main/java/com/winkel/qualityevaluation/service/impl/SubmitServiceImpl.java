package com.winkel.qualityevaluation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winkel.qualityevaluation.dao.SubmitDao;
import com.winkel.qualityevaluation.entity.task.EvaluateSubmit;
import com.winkel.qualityevaluation.service.api.SubmitService;
import org.springframework.stereotype.Service;

@Service
public class SubmitServiceImpl extends ServiceImpl<SubmitDao, EvaluateSubmit> implements SubmitService {
}
