package com.winkel.qualityevaluation.vo;
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

}
