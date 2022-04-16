package com.winkel.qualityevaluation.pojo.dto;
/*
  @ClassName ScoreDTO
  @Description
  @Author winkel
  @Date 2022-03-29 20:34
  */

import lombok.Data;

@Data
public class ScoreDTO {

    private String locationCode;
    private Integer locationType;
    private Integer taskType;
    private Integer taskStatus;

    private String isCity;  // 城市/农村

    private String isPublic;  // 公办/民办

    private String isGb;  // 是否普惠

    private String isRegister;  // 是否在册

}
