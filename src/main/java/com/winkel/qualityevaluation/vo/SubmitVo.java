package com.winkel.qualityevaluation.vo;
/*
  @ClassName SubmitVo
  @Description
  @Author winkel
  @Date 2022-03-22 16:26
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
public class SubmitVo {

    private Integer taskId;

    private Integer index3Id;

    private Integer type;

    private String content;

}
