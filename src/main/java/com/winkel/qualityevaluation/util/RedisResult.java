package com.winkel.qualityevaluation.util;
/*
  @ClassName RedisResult
  @Description
  @Author winkel
  @Date 2022-04-04 16:24
  */

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RedisResult {

    private Integer status;

    private String msg;

}
