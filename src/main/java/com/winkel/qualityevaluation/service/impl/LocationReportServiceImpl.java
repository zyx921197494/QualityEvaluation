package com.winkel.qualityevaluation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winkel.qualityevaluation.dao.LocationReportDao;
import com.winkel.qualityevaluation.entity.LocationReport;
import com.winkel.qualityevaluation.service.api.LocationReportService;
import org.springframework.stereotype.Service;

@Service
public class LocationReportServiceImpl extends ServiceImpl<LocationReportDao, LocationReport> implements LocationReportService {
}
