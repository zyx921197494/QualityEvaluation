package com.winkel.qualityevaluation.entity.task;
/*
  @ClassName EvaluateTask
  @Description 评价任务定义
  @Author winkel
  @Date 2022-03-16 11:31
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
public class EvaluateTask {
    private Integer id;
    private String name;
    private Integer evaluateId;  //评价体系id
    private LocalDateTime startTime;
    private LocalDateTime endTIme;
    private String content;
}
