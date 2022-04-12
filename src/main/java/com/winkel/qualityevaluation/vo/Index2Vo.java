package com.winkel.qualityevaluation.vo;
/*
  @ClassName Index2Vo
  @Description
  @Author winkel
  @Date 2022-04-09 13:48
  */

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Index2Vo {

    private Integer count;
    private Integer index2Id;

}
