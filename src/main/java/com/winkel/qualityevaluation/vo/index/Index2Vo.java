package com.winkel.qualityevaluation.vo.index;
/*
  @ClassName Index2Vo
  @Description
  @Author winkel
  @Date 2022-04-13 20:55
  */

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
public class Index2Vo {

    private EvaluateIndex2 evaluateIndex2;

    private List<EvaluateIndex3> index3List;

}
