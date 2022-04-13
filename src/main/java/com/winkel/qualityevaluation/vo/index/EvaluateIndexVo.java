package com.winkel.qualityevaluation.vo.index;
/*
  @ClassName EvaluateIndexVo
  @Description
  @Author winkel
  @Date 2022-04-13 19:23
  */

import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex;
import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex1;
import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex2;
import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex3;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class EvaluateIndexVo {

    private EvaluateIndex evaluateIndex;

    private List<Index1Vo> index1List;

}
