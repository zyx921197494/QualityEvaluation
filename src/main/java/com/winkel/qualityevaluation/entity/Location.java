package com.winkel.qualityevaluation.entity;
/*
  @ClassName Location
  @Description
  @Author winkel
  @Date 2022-03-16 13:46
  */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location {
    private String code;
    private String pCode;
    private String name;
    private Integer type;
}
