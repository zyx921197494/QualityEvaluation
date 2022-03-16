package com.winkel.qualityevaluation.entity.evaluate;
/*
  @ClassName EvaluateIndex3
  @Description
  @Author winkel
  @Date 2022-03-16 11:07
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
public class EvaluateIndex3 {
    private Integer index3Id;
    private Integer index2Id;
    private String index3Name;

    private Integer Type;  //类型：1数字型 2文本型 3单选型 4多选型
    private Integer length;  //长度限制
    private String selected; //选择的答案
    private String memo;  //备注
}
