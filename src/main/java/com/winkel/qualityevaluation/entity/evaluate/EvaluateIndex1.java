package com.winkel.qualityevaluation.entity.evaluate;
/*
  @ClassName EvaluateIndex1
  @Description
  @Author winkel
  @Date 2022-03-16 10:58
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
public class EvaluateIndex1 {
    private Integer index1Id;
    private Integer evaluateIndexId;
    private String index1Name;
    private String index1Content;
}
