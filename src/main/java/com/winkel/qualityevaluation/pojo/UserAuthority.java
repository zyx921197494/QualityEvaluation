package com.winkel.qualityevaluation.pojo;
/*
  @ClassName UserAuthority
  @Description
  @Author winkel
  @Date 2022-03-17 12:12
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
public class UserAuthority {
    private String userId;
    private Integer authorityId;
}
