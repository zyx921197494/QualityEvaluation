package com.winkel.qualityevaluation.entity;
/*
  @ClassName Location
  @Description
  @Author winkel
  @Date 2022-03-16 13:46
  */

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Location {

    @TableId(type = IdType.NONE)
    private String code;
    private String pCode;
    private String name;
    private Integer type;
}
