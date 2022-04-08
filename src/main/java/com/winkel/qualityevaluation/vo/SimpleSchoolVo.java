package com.winkel.qualityevaluation.vo;
/*
  @ClassName SimpleSchoolVo
  @Description
  @Author winkel
  @Date 2022-04-08 21:31
  */

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class SimpleSchoolVo {

    private String code;
    private String name;

}
