package com.winkel.qualityevaluation.entity.evaluate;
/*
  @ClassName EvaluateIndex2
  @Description
  @Author winkel
  @Date 2022-03-16 11:06
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
public class EvaluateIndex2 {
    private Integer index2Id;
    private Integer index1Id;
    private String index2Name;
    private String index2Content;
}
