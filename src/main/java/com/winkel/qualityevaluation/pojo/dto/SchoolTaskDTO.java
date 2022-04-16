package com.winkel.qualityevaluation.pojo.dto;
/*
  @ClassName SchoolTaskDTO
  @Description
  @Author winkel
  @Date 2022-04-08 12:24
  */

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class SchoolTaskDTO {

    private String provinceCode;
    private String cityCode;
    private String countyCode;

    private Integer isCity;
    private Integer isPublic;
    private Integer isGb;

    private Integer taskType;
    private Integer taskStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;
    private String keyName;
    private String schoolCode;

    private Integer pageSize;
    private Integer currentPage;

}
