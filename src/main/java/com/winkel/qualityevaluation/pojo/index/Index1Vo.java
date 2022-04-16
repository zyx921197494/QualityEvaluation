package com.winkel.qualityevaluation.pojo.index;
/*
  @ClassName Index1Vo
  @Description
  @Author winkel
  @Date 2022-04-13 20:56
  */

import com.winkel.qualityevaluation.entity.evaluate.EvaluateIndex1;
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
public class Index1Vo {

    private EvaluateIndex1 evaluateIndex1;

    private List<Index2Vo> index2VoList;

}
