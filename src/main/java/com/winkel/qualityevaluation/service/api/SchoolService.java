package com.winkel.qualityevaluation.service.api;
/*
  @ClassName SchoolService
  @Description
  @Author winkel
  @Date 2022-03-16 17:13
  */

import com.baomidou.mybatisplus.extension.service.IService;
import com.winkel.qualityevaluation.entity.School;
import com.winkel.qualityevaluation.pojo.vo.SchoolVo;

public interface SchoolService extends IService<School> {

    SchoolVo getSchoolVoBySchoolCode(String schoolCode);

}
