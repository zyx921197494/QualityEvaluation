package com.winkel.qualityevaluation.pojo.vo;
/*
  @ClassName ScoreVo
  @Description
  @Author winkel
  @Date 2022-03-29 14:37
  */

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ScoreVo {

    private String schoolCode;

    private String schoolName;

    protected Integer score;

    private String name;  // 一级指标的编号，如：A1

    private String content;  // 一级指标的名称，如：办园条件(满分250分)

    private String locationTypeCode;  // 城市/农村

    private String host;  // 公办/民办

    private Integer isGb;  // 是否普惠

    private Integer isRegister;  // 是否在册

}
