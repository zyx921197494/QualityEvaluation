package com.winkel.qualityevaluation.pojo.dto;
/*
  @ClassName CountDTO
  @Description
  @Author winkel
  @Date 2022-03-29 22:46
  */

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CountDTO {

    private String locationCode;

    private Integer locationType;

}
