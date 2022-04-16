package com.winkel.qualityevaluation.service.impl;
/*
  @ClassName SchoolServiceImpl
  @Description
  @Author winkel
  @Date 2022-03-16 17:14
  */

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winkel.qualityevaluation.dao.SchoolDao;
import com.winkel.qualityevaluation.entity.School;
import com.winkel.qualityevaluation.service.api.SchoolService;
import com.winkel.qualityevaluation.pojo.vo.SchoolVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SchoolServiceImpl extends ServiceImpl<SchoolDao, School> implements SchoolService {

    @Autowired
    private SchoolDao schoolDao;

    @Override
    public SchoolVo getSchoolVoBySchoolCode(String schoolCode) {
        return schoolDao.selectSchoolVoBySchoolCode(schoolCode);
    }

}
