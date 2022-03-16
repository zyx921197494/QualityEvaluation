package com.winkel.qualityevaluation.entity.evaluate;
/*
  @ClassName EvaluateIndex
  @Description 评价体系设置
  @Author winkel
  @Date 2022-03-16 11:01
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
public class EvaluateIndex {
    private Integer evaluateId;
    private String evaluateName;
    private String evaluateMemo;
}
