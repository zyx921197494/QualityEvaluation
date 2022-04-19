package com.winkel.qualityevaluation.pojo.vo;
/*
  @ClassName TableVo
  @Description
  @Author winkel
  @Date 2022-04-18 21:32
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
public class TableVo {

    private String name;
    private String x;
    private Integer y;
    private Double z;  // y和z均为纵坐标值，根据数据类型选其一

}
