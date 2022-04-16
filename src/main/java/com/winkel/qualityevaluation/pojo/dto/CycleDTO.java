package com.winkel.qualityevaluation.pojo.dto;
/*
  @ClassName CycleDTO
  @Description
  @Author winkel
  @Date 2022-04-16 15:23
  */

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class CycleDTO {

    private String locationCode;
    private Integer type;

}
