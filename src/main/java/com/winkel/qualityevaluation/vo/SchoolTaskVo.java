package com.winkel.qualityevaluation.vo;
/*
  @ClassName SchoolDTO
  @Description
  @Author winkel
  @Date 2022-04-08 11:35
  */

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class SchoolTaskVo {

    private String schoolCode;
    private String schoolName;
    private Integer taskId;

    private LocalDate startDate;
    private String taskStatus;
    private LocalDateTime lastSubmit;
    private LocalDateTime FirstSubmit;

}
