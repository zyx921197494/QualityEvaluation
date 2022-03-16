package com.winkel.qualityevaluation.entity.task;
/*
  @ClassName EvaluateSubmit
  @Description 评价数据提交
  @Author winkel
  @Date 2022-03-16 12:03
  */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class EvaluateSubmit {
    private Integer id;
    private String schoolCode;
    private Integer taskId;
    private LocalDateTime submitTime;
    private Integer index3Id;
    private String content;
}
