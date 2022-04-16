package com.winkel.qualityevaluation.pojo.vo;
/*
  @ClassName UserVo
  @Description
  @Author winkel
  @Date 2022-03-23 11:08
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
public class UserVo {

    private String name;
    private String email;

}
