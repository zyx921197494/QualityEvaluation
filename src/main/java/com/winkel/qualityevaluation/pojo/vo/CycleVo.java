package com.winkel.qualityevaluation.pojo.vo;
/*
  @ClassName CycleVo
  @Description
  @Author winkel
  @Date 2022-04-16 15:09
  */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class CycleVo {

    private String provinceName;
    private String countyName;
    private String locationCode;
    private Integer currentCycle;

}
